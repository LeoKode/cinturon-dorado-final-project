package com.cinturondorado.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cinturondorado.model.Clase;
import com.cinturondorado.model.HorarioDisponible;
import com.cinturondorado.repository.ClaseRepository;
import com.cinturondorado.repository.HorarioDisponibleRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class ClaseService {
    private final ClaseRepository claseRepository;
    private final HorarioDisponibleRepository horarioRepository;

    public ClaseService(ClaseRepository claseRepository,
            HorarioDisponibleRepository horarioRepository) {
        this.claseRepository = claseRepository;
        this.horarioRepository = horarioRepository;
    }

    public boolean existeClaseEnHorario(String dia, String horaInicio) {
        HorarioDisponible horario = horarioRepository.findByHora(horaInicio)
            .orElse(null);
        return horario != null && claseRepository.findByDiaAndHorario(dia, horario).isPresent();
    }

    public boolean esHorarioDisponibleParaClase(Long claseId, String dia, String horaInicio) {
        log.debug("Verificando disponibilidad del horario para clase ID: {}, día: {}, hora: {}",
                claseId, dia, horaInicio);
        
        HorarioDisponible horario = horarioRepository.findByHora(horaInicio)
            .orElse(null);
        
        if (horario == null) {
            return true;
        }

        Optional<Clase> claseExistente = claseRepository.findByDiaAndHorario(dia, horario);

        if (claseExistente.isEmpty()) {
            return true;
        }

        return claseExistente.get().getId().equals(claseId);
    }

    public List<Clase> listarTodasClases() {
        try {
            log.debug("Obteniendo todas las clases");
            return claseRepository.findAll();
        } catch (Exception e) {
            log.error("Error al obtener las clases: ", e);
            throw new RuntimeException("Error al obtener las clases", e);
        }
    }

    public Clase guardarClase(Clase clase) {
        // Verificar que el horario existe
        HorarioDisponible horario = horarioRepository.findById(clase.getHorario().getId())
            .orElseThrow(() -> new IllegalArgumentException("Horario no encontrado"));

        // Verificar disponibilidad
        if (claseRepository.findByDiaAndHorario(clase.getDia(), horario).isPresent()) {
            throw new IllegalStateException("Ya existe una clase programada para " +
                clase.getDia() + " a las " + horario.getHora());
        }

        clase.setHorario(horario);
        return claseRepository.save(clase);
    }

    public void eliminarClase(Long id) {
        try {
            log.debug("Eliminando clase con ID: {}", id);
            if (!claseRepository.existsById(id)) {
                throw new IllegalArgumentException("No existe una clase con ID: " + id);
            }
            claseRepository.deleteById(id);
            log.debug("Clase eliminada correctamente");
        } catch (Exception e) {
            log.error("Error al eliminar la clase: ", e);
            throw new RuntimeException("Error al eliminar la clase", e);
        }
    }

    public void eliminarHorario(Long id) {
        HorarioDisponible horario = horarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Horario no encontrado"));
                
        // Verificar si hay clases en este horario
        boolean existenClases = false;
        List<String> diasConClases = new ArrayList<>();
        String[] dias = { "LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO" };
        
        for (String dia : dias) {
            if (claseRepository.existsByDiaAndHorario(dia, horario)) {
                existenClases = true;
                diasConClases.add(dia);
            }
        }

        if (existenClases) {
            throw new IllegalStateException(
                "No se puede eliminar el horario porque existen clases programadas en: " + 
                String.join(", ", diasConClases) + 
                ". Por favor, elimine primero estas clases."
            );
        }

        horarioRepository.delete(horario);
    }
}