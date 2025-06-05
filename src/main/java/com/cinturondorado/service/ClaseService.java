package com.cinturondorado.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;

import com.cinturondorado.exception.ResourceNotFoundException;
import com.cinturondorado.model.Alumno;
import com.cinturondorado.model.Clase;
import com.cinturondorado.model.CalendarioClase;
import com.cinturondorado.repository.AlumnoRepository;
import com.cinturondorado.repository.ClaseRepository;
import com.cinturondorado.repository.CalendarioClaseRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class ClaseService {
    private final ClaseRepository claseRepository;
    private final CalendarioClaseRepository horarioRepository;
    private final AlumnoRepository alumnoRepository;

    public ClaseService(ClaseRepository claseRepository, 
                       CalendarioClaseRepository horarioRepository,
                       AlumnoRepository alumnoRepository) { 
        this.claseRepository = claseRepository;
        this.horarioRepository = horarioRepository;
        this.alumnoRepository = alumnoRepository;
    }

    public List<Clase> listarClases() {
        try {
            log.debug("Obteniendo todas las clases");
            return claseRepository.findAll();
        } catch (Exception e) {
            log.error("Error al obtener las clases: ", e);
            throw new RuntimeException("Error al obtener las clases", e);
        }
    }

    public Clase obtenerClase(Long id) {
        return claseRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Clase no encontrada con ID: " + id));
    }

    public Clase guardarClase(Clase clase) {
        try {
            if (clase.getId() == null && claseRepository.existsByNombre(clase.getNombre())) {
                throw new IllegalStateException("Ya existe una clase con el nombre: " + clase.getNombre());
            }
            return claseRepository.save(clase);
        } catch (Exception e) {
            log.error("Error al guardar la clase: ", e);
            throw new RuntimeException("Error al guardar la clase", e);
        }
    }

    public void asignarHorario(Long claseId, String dia, String hora) {
        try {
            Clase clase = obtenerClase(claseId);
            CalendarioClase calendarioClase = horarioRepository
                .findByClaseIdAndDiaAndHora(claseId, dia, hora)
                .orElseGet(() -> {
                    CalendarioClase nuevoHorario = new CalendarioClase();
                    nuevoHorario.setDia(dia);
                    nuevoHorario.setHora(hora);
                    nuevoHorario.setClase(clase);
                    return horarioRepository.save(nuevoHorario);
                });

            clase.addCalendarioClase(calendarioClase);
            claseRepository.save(clase);
        } catch (Exception e) {
            log.error("Error al asignar horario a la clase: ", e);
            throw new RuntimeException("Error al asignar horario a la clase", e);
        }
    }

    public void desasignarHorario(Long claseId, String dia, String hora) {
        Clase clase = obtenerClase(claseId);
        CalendarioClase calendarioClase = horarioRepository
            .findByClaseIdAndDiaAndHora(claseId, dia, hora)
            .orElseThrow(() -> new IllegalStateException("Horario no encontrado"));
            
        clase.removeCalendarioClase(calendarioClase);
        horarioRepository.delete(calendarioClase);
        claseRepository.save(clase);
    }

    public void eliminarClase(Long id) {
        try {
            log.debug("Eliminando clase con ID: {}", id);
            Clase clase = obtenerClase(id);
            claseRepository.delete(clase);
        } catch (Exception e) {
            log.error("Error al eliminar la clase: ", e);
            throw new RuntimeException("Error al eliminar la clase", e);
        }
    }

    public List<CalendarioClase> listarHorarios() {
        return horarioRepository.findAll(Sort.by(Sort.Direction.ASC, "hora"));
    }

    public void inscribirAlumno(Long claseId, Long alumnoId) {
        Clase clase = claseRepository.findById(claseId)
            .orElseThrow(() -> new ResourceNotFoundException("Clase no encontrada"));
            
        Alumno alumno = alumnoRepository.findById(alumnoId)
            .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado"));

        if (!clase.tieneCupoDisponible()) {
            throw new IllegalStateException("La clase ha alcanzado su cupo máximo");
        }

        clase.addAlumno(alumno);
        claseRepository.save(clase);
    }

    public void desinscribirAlumno(Long claseId, Long alumnoId) {
        Clase clase = claseRepository.findById(claseId)
            .orElseThrow(() -> new ResourceNotFoundException("Clase no encontrada"));
            
        Alumno alumno = alumnoRepository.findById(alumnoId)
            .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado"));

        clase.removeAlumno(alumno);
        claseRepository.save(clase);
    }

    public void agregarHorario(String hora, String dia) {
        try {
            if (horarioRepository.existsByDiaAndHora(hora, dia)) {
                throw new IllegalStateException("Ya existe un horario con esa hora");
            }

            CalendarioClase nuevoHorario = new CalendarioClase();
            nuevoHorario.setHora(hora);
            horarioRepository.save(nuevoHorario);
            
            log.debug("Horario agregado correctamente: {}", hora);
        } catch (Exception e) {
            log.error("Error al agregar horario: ", e);
            throw new RuntimeException("Error al agregar el horario", e);
        }
    }
}