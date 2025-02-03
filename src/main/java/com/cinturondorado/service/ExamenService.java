package com.cinturondorado.service;

import com.cinturondorado.model.Alumno;
import com.cinturondorado.model.Examen;
import com.cinturondorado.model.enums.NivelCinturon;
import com.cinturondorado.model.enums.EstadoExamen;
import com.cinturondorado.repository.ExamenRepository;
import com.cinturondorado.dto.ExamenDTO;
import com.cinturondorado.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
public class ExamenService {

    private final ExamenRepository examenRepository;
    private final AlumnoService alumnoService;
    private final ProfesorService profesorService;

    public ExamenService(ExamenRepository examenRepository,
            AlumnoService alumnoService,
            ProfesorService profesorService) {
        this.examenRepository = examenRepository;
        this.alumnoService = alumnoService;
        this.profesorService = profesorService;
    }

    public List<Examen> listarTodos() {
        return examenRepository.findAll();
    }

    public Examen obtenerPorId(Long id) {
        return examenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examen no encontrado con ID: " + id));
    }

    public Examen registrarExamen(ExamenDTO examenDTO) {
        // Obtener el alumno primero
        Alumno alumno = alumnoService.obtenerPorId(examenDTO.getAlumno().getId());

        Examen examen = new Examen();
        examen.setAlumno(alumno);
        // Solo buscar evaluador si se proporciona un ID
        if (examenDTO.getEvaluadorId() != null) {
            examen.setEvaluador(profesorService.obtenerPorId(examenDTO.getEvaluadorId()));
        }

        examen.setFecha(examenDTO.getFecha());

        // Establecer el nivel actual desde el alumno
        examen.setNivelActual(alumno.getNivelCinturon());

        // Establecer el siguiente nivel como nivel aspirante
        examen.setNivelAspirante(obtenerSiguienteNivel(alumno.getNivelCinturon()));

        examen.setEstado(EstadoExamen.PENDIENTE);

        return examenRepository.save(examen);
    }

    private NivelCinturon obtenerSiguienteNivel(NivelCinturon nivelActual) {
        return NivelCinturon.values()[nivelActual.ordinal() + 1];
    }

    public List<Examen> buscarPorFiltros(NivelCinturon nivelActual, NivelCinturon nivelAspirante) {
        if (nivelActual != null && nivelAspirante != null) {
            return examenRepository.findByNivelActualAndNivelAspirante(nivelActual, nivelAspirante);
        } else if (nivelActual != null) {
            return examenRepository.findByNivelActual(nivelActual);
        } else if (nivelAspirante != null) {
            return examenRepository.findByNivelAspirante(nivelAspirante);
        }
        return listarTodos();
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
        examen.setFechaEvaluacion(LocalDateTime.now());

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
}
