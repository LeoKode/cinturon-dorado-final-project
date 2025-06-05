package com.cinturondorado.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cinturondorado.model.CalendarioClase;
import com.cinturondorado.repository.CalendarioClaseRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class CalendarioClaseService {
    private final CalendarioClaseRepository horarioRepository;

    public CalendarioClaseService(CalendarioClaseRepository horarioRepository) {
        this.horarioRepository = horarioRepository;
    }

    public List<CalendarioClase> listarHorarios() {
        return horarioRepository.findAll();
    }

    public CalendarioClase guardarHorario(CalendarioClase horario) {
        try {
            if (horarioRepository.existsByDiaAndHora(horario.getDia(), horario.getHora())) {
                throw new IllegalStateException("Ya existe un horario para ese día y hora");
            }
            horario.setDia(horario.getDia().toUpperCase()); // Normalizar el día a mayúsculas
            return horarioRepository.save(horario);
        } catch (Exception e) {
            log.error("Error al guardar el horario: ", e);
            throw new RuntimeException("Error al guardar el horario", e);
        }
    }

    public void eliminarHorario(Long id) {
        try {
            horarioRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Error al eliminar el horario: ", e);
            throw new RuntimeException("Error al eliminar el horario", e);
        }
    }

    public CalendarioClase obtenerHorario(Long id) {
        return horarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Horario no encontrado con ID: " + id));
    }

    public boolean existeHorario(String dia, String hora) {
        return horarioRepository.existsByDiaAndHora(dia, hora);
    }
}