package com.cinturondorado.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cinturondorado.model.HorarioDisponible;
import com.cinturondorado.repository.HorarioDisponibleRepository;
import com.cinturondorado.repository.ClaseRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class HorarioDisponibleService {
    private final HorarioDisponibleRepository horarioRepository;
    private final ClaseRepository claseRepository;

    public HorarioDisponibleService(HorarioDisponibleRepository horarioRepository,
            ClaseRepository claseRepository) {
        this.horarioRepository = horarioRepository;
        this.claseRepository = claseRepository;
    }

    public HorarioDisponible agregarHorario(String hora) {
        if (horarioRepository.existsByHora(hora)) {
            throw new IllegalArgumentException("Ya existe un horario para las " + hora);
        }

        HorarioDisponible horario = new HorarioDisponible();
        horario.setHora(hora);
        return horarioRepository.save(horario);
    }

    public HorarioDisponible actualizarHorario(Long id, String nuevaHora) {
        if (horarioRepository.existsByHora(nuevaHora)) {
            throw new IllegalArgumentException("Ya existe un horario para las " + nuevaHora);
        }
    
        HorarioDisponible horario = horarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Horario no encontrado"));
                
        // Verificar si hay clases en este horario
        String[] dias = { "LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO" };
        for (String dia : dias) {
            if (claseRepository.existsByDiaAndHorario(dia, horario)) {
                throw new IllegalStateException("No se puede modificar el horario porque hay clases programadas");
            }
        }
    
        horario.setHora(nuevaHora);
        return horarioRepository.save(horario);
    }

    public List<HorarioDisponible> listarHorariosActivos() {
        return horarioRepository.findByActivoOrderByHoraAsc(true);
    }

    public void eliminarHorario(Long id) {
        HorarioDisponible horario = horarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Horario no encontrado"));
                
        // Verificar si hay clases en este horario
        boolean existenClases = false;
        String[] dias = { "LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO" };
        for (String dia : dias) {
            if (claseRepository.existsByDiaAndHorario(dia, horario)) {
                existenClases = true;
                break;
            }
        }

        if (existenClases) {
            throw new IllegalStateException("No se puede eliminar el horario porque hay clases programadas");
        }

        horarioRepository.delete(horario);
    }
}