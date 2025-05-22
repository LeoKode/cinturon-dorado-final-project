package com.cinturondorado.service;

import com.cinturondorado.model.Alumno;
import com.cinturondorado.model.Examen;
import com.cinturondorado.model.enums.NivelCinturon;
import com.cinturondorado.model.enums.EstadoExamen;
import com.cinturondorado.model.enums.CategoriaAlumno;
import com.cinturondorado.repository.ExamenRepository;
import com.cinturondorado.dto.ExamenDTO;
import com.cinturondorado.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Slf4j
@Service
@Transactional
public class ExamenService {

    private final ExamenRepository examenRepository;
    private final AlumnoService alumnoService;

    public ExamenService(ExamenRepository examenRepository,
            AlumnoService alumnoService,
            ProfesorService profesorService) {
        this.examenRepository = examenRepository;
        this.alumnoService = alumnoService;
    }

    public List<Examen> listarTodos() {
        return examenRepository.findAll();
    }

    public Examen obtenerPorId(Long id) {
        return examenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examen no encontrado con ID: " + id));
    }

    public List<Examen> registrarExamenes(ExamenDTO examenDTO) {
        List<Examen> examenes = new ArrayList<>();

        for (Long alumnoId : examenDTO.getAlumnoIds()) {
            Alumno alumno = alumnoService.obtenerPorId(alumnoId);

            Examen examen = new Examen();
            examen.setAlumno(alumno);
            examen.setFecha(examenDTO.getFecha());
            examen.setNivelActual(alumno.getNivelCinturon());
            examen.setNivelAspirante(obtenerSiguienteNivel(alumno.getNivelCinturon()));
            examen.setEstado(EstadoExamen.PENDIENTE);

            examenes.add(examenRepository.save(examen));
        }

        return examenes;
    }

    private NivelCinturon obtenerSiguienteNivel(NivelCinturon nivelActual) {
        return NivelCinturon.values()[nivelActual.ordinal() + 1];
    }

    public List<Examen> buscarPorFiltros(NivelCinturon nivelAspirante, EstadoExamen estado) {
        log.debug("Buscando exámenes con filtros - nivelAspirante: {}, estado: {}", nivelAspirante, estado);

        try {
            List<Examen> examenes;
            if (nivelAspirante != null && estado != null) {
                examenes = examenRepository.buscarPorFiltros(null, nivelAspirante, estado);
            } else if (nivelAspirante != null) {
                examenes = examenRepository.findByNivelAspirante(nivelAspirante);
            } else if (estado != null) {
                examenes = examenRepository.findByEstado(estado);
            } else {
                examenes = listarTodos();
            }

            log.debug("Resultados encontrados: {}", examenes.size());
            return examenes;

        } catch (Exception e) {
            log.error("Error al buscar exámenes: {}", e.getMessage());
            throw new RuntimeException("Error al filtrar exámenes", e);
        }
    }

    public Examen evaluarExamen(Long id, ExamenDTO evaluacion) {
        Examen examen = obtenerPorId(id);

        if (examen.getEstado() != EstadoExamen.PENDIENTE) {
            throw new IllegalStateException("No se puede evaluar un examen que no está pendiente");
        }

        // Cambiar esta línea para usar el campo resultado
        EstadoExamen nuevoEstado = "APROBADO".equals(evaluacion.getResultado()) ? EstadoExamen.APROBADO
                : EstadoExamen.REPROBADO;

        examen.setEstado(nuevoEstado);
        examen.setObservaciones(evaluacion.getObservaciones());
        examen.setResultado(evaluacion.getResultado());

        // Actualizar nivel del alumno si aprobó
        if (nuevoEstado == EstadoExamen.APROBADO) {
            actualizarNivelAlumno(examen);
        }

        return examenRepository.save(examen);
    }

    private void actualizarNivelAlumno(Examen examen) {
        if (examen.getEstado() == EstadoExamen.APROBADO) {
            examen.getAlumno().setNivelCinturon(examen.getNivelAspirante());
            alumnoService.actualizarAlumno(examen.getAlumno());
        }
    }

    public void actualizarObservaciones(Long id, String observaciones) {
        Examen examen = obtenerPorId(id);
        examen.setObservaciones(observaciones);
        examenRepository.save(examen);
    }

    public void eliminarExamen(Long id) {
        Examen examen = obtenerPorId(id);
        if (examen == null) {
            throw new ResourceNotFoundException("Examen no encontrado");
        }
        examenRepository.delete(examen);
    }

    public long contarProximosExamenes() {
        LocalDate hoy = LocalDate.now();
        return examenRepository.countByFechaGreaterThanEqualAndEstado(hoy, EstadoExamen.PENDIENTE);
    }

    public Page<Examen> listarTodosPaginados(Pageable pageable) {
        return examenRepository.findAll(pageable);
    }

    public Page<Examen> buscarPorFiltrosPaginados(NivelCinturon nivelAspirante, EstadoExamen estado,
            Pageable pageable) {
        if (nivelAspirante != null && estado != null) {
            return examenRepository.findByNivelAspiranteAndEstado(nivelAspirante, estado, pageable);
        } else if (nivelAspirante != null) {
            return examenRepository.findByNivelAspirante(nivelAspirante, pageable);
        } else if (estado != null) {
            return examenRepository.findByEstado(estado, pageable);
        } else {
            return examenRepository.findAll(pageable);
        }
    }

    public Page<Examen> obtenerExamenesPorRangoFecha(LocalDate fechaDesde, LocalDate fechaHasta, Pageable pageable) {
        return examenRepository.findByFechaRange(fechaDesde, fechaHasta, pageable);
    }

    public Page<Examen> buscarPorFiltros(
            NivelCinturon nivelAspirante,
            EstadoExamen estado,
            String categoriaAlumno,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Pageable pageable) {

        if (fechaInicio != null && fechaFin != null) {
            // Si hay filtro de año, usar el repositorio con todos los filtros
            if (categoriaAlumno != null && !categoriaAlumno.isEmpty()) {
                try {
                    CategoriaAlumno categoria = CategoriaAlumno.valueOf(categoriaAlumno);
                    return examenRepository.findByAllFilters(
                            categoria,
                            nivelAspirante,
                            estado,
                            fechaInicio,
                            fechaFin,
                            pageable);
                } catch (IllegalArgumentException e) {
                    log.error("Error al convertir categoría: {}", e.getMessage());
                }
            }
            return examenRepository.findByFiltersAndDateRange(
                    nivelAspirante,
                    estado,
                    fechaInicio,
                    fechaFin,
                    pageable);
        }

        // Si no hay filtro de año, usar la lógica existente
        if (categoriaAlumno != null && !categoriaAlumno.isEmpty()) {
            try {
                CategoriaAlumno categoria = CategoriaAlumno.valueOf(categoriaAlumno);
                return examenRepository.findByAlumnoCategoriaAndFilters(
                        categoria,
                        nivelAspirante,
                        estado,
                        pageable);
            } catch (IllegalArgumentException e) {
                log.error("Error al convertir categoría: {}", e.getMessage());
            }
        }
        return buscarPorFiltrosPaginados(nivelAspirante, estado, pageable);
    }
}
