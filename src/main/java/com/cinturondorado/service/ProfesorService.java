package com.cinturondorado.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cinturondorado.model.Profesor;
import com.cinturondorado.repository.ProfesorRepository;
import com.cinturondorado.exception.ResourceNotFoundException;
import com.cinturondorado.model.enums.NivelCinturon;

import java.util.List;

@Service
@Transactional
public class ProfesorService {

    private final ProfesorRepository profesorRepository;

    public ProfesorService(ProfesorRepository profesorRepository) {
        this.profesorRepository = profesorRepository;
    }

    public List<Profesor> listarTodos() {
        return profesorRepository.findAll();
    }

    public Profesor obtenerPorId(Long id) {
        return profesorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Profesor no encontrado con ID: " + id));
    }

    public List<Profesor> obtenerProfesoresActivos() {
        return profesorRepository.findByActivo(true);
    }

    public List<Profesor> obtenerPorNivelCinturon(NivelCinturon nivel) {
        return profesorRepository.findByNivelCinturon(nivel);
    }

    public Profesor guardarProfesor(Profesor profesor) {
        return profesorRepository.save(profesor);
    }

    public void eliminarProfesor(Long id) {
        profesorRepository.deleteById(id);
    }
}