package com.cinturondorado.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cinturondorado.exception.ResourceNotFoundException;
import com.cinturondorado.model.Clase;
import com.cinturondorado.repository.ClaseRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ClaseService {

    private final ClaseRepository claseRepository;

    public ClaseService(ClaseRepository claseRepository) {
        this.claseRepository = claseRepository;
    }

    public List<Clase> listarTodasClases() {
        return claseRepository.findAll();
    }

    public Clase obtenerClasePorId(Long id) {
        return claseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clase no encontrada con ID: " + id));
    }

    // Método específico para guardar plantillas
    public Clase guardarPlantillaClase(Clase clase) {
        // Para plantillas, la fechaHora es null
        clase.setFechaHora(null);
        return claseRepository.save(clase);
    }

    public Clase guardarClase(Clase clase) {
        return claseRepository.save(clase);
    }

    public void eliminarClase(Long id) {
        claseRepository.deleteById(id);
    }

    public void actualizarFechaClase(Long id, LocalDateTime nuevaFecha) {
        Clase clase = obtenerClasePorId(id);
        clase.setFechaHora(nuevaFecha);
        claseRepository.save(clase);
    }
}
