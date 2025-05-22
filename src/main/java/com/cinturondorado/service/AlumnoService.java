package com.cinturondorado.service;

import javax.validation.ValidationException;

import lombok.extern.slf4j.Slf4j;

import org.hibernate.ResourceClosedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.cinturondorado.model.Alumno;
import com.cinturondorado.model.enums.NivelCinturon;
import com.cinturondorado.model.enums.CategoriaAlumno;
import com.cinturondorado.repository.AlumnoRepository;
import com.cinturondorado.dto.AlumnoDTO;
import com.cinturondorado.exception.ResourceNotFoundException;

import java.util.List;

@Slf4j
@Service
@Transactional
public class AlumnoService {
    @Autowired
    private AlumnoRepository alumnoRepository;
    private final NotificacionService notificacionService;

    public AlumnoService(AlumnoRepository alumnoRepository,
            NotificacionService notificacionService) {
        this.alumnoRepository = alumnoRepository;
        this.notificacionService = notificacionService;
    }

    public Alumno registrarAlumno(AlumnoDTO alumnoDTO) {
        // Validar datos
        validarDatosAlumno(alumnoDTO);

        System.out.println("Datos validados");

        // Convertir DTO a entidad
        Alumno alumno = new Alumno();
        alumno.setNombre(alumnoDTO.getNombre());
        alumno.setFechaNacimiento(alumnoDTO.getFechaNacimiento());
        alumno.setEmail(alumnoDTO.getEmail());
        alumno.setTelefono(alumnoDTO.getTelefono());
        alumno.setNivelCinturon(alumnoDTO.getNivelCinturon()); // Nivel inicial
        alumno.setCategoria(alumnoDTO.getCategoria());

        // Guardar / revisar luego lo de notificar
        Alumno alumnoGuardado = alumnoRepository.save(alumno);
        System.out.println("Alumno guardado correctamente");

        return alumnoGuardado;
    }

    public void actualizarAlumno(AlumnoDTO alumnoDTO) {
        Alumno alumno = alumnoRepository.findById(alumnoDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado"));

        alumno.setNombre(alumnoDTO.getNombre());
        alumno.setFechaNacimiento(alumnoDTO.getFechaNacimiento());
        alumno.setEmail(alumnoDTO.getEmail());
        alumno.setTelefono(alumnoDTO.getTelefono());
        alumno.setNivelCinturon(alumnoDTO.getNivelCinturon());
        alumno.setCategoria(alumnoDTO.getCategoria());

        alumnoRepository.save(alumno);
    }

    public void actualizarNivelCinturon(Long alumnoId, NivelCinturon nuevoNivel) {
        Alumno alumno = alumnoRepository.findById(alumnoId)
                .orElseThrow(() -> new ResourceClosedException("Alumno no encontrado"));

        alumno.setNivelCinturon(nuevoNivel);
        alumnoRepository.save(alumno);
        notificacionService.notificarCambioNivel(alumno, nuevoNivel);
    }

    private void validarDatosAlumno(AlumnoDTO alumnoDTO) {

        if (alumnoDTO.getNombre() == null || alumnoDTO.getNombre().trim().isEmpty()) {
            throw new ValidationException("El nombre del alumno no puede estar vacío");
        }

        if (alumnoDTO.getEmail() == null) {
            throw new ValidationException("El email proporcionado no es válido");
        }
        // Más validaciones según necesidad
    }

    public boolean toggleActivo(Long id) {
        Alumno alumno = obtenerPorId(id);
        alumno.setActivo(!alumno.isActivo());
        alumnoRepository.save(alumno);
        return alumno.isActivo();
    }

    public Page<Alumno> listarTodos(Pageable pageable) {
        return alumnoRepository.findAll(pageable);
    }

    public Alumno obtenerPorId(Long id) {
        return alumnoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado con ID: " + id));
    }

    public List<Alumno> obtenerAlumnosConPagosPendientes() {
        return alumnoRepository.findAlumnosConPagosPendientes();
    }

    public void eliminar(Long id) {
        Alumno alumno = obtenerPorId(id);
        alumnoRepository.delete(alumno);
    }

    public Page<Alumno> buscarPorActivo(boolean activo, Pageable pageable) {
        return alumnoRepository.findByActivo(activo, pageable);
    }

    public Page<Alumno> buscarPorNombre(String nombre, Pageable pageable) {
        return alumnoRepository.findByNombreContainingIgnoreCase(nombre, pageable);
    }

    public Page<Alumno> buscarPorNivelCinturon(NivelCinturon nivelCinturon, Pageable pageable) {
        if (nivelCinturon == null) {
            return alumnoRepository.findAll(pageable);
        }

        // Instead of trying to handle pagination manually, let the repository do it
        return alumnoRepository.findByNivelCinturon(nivelCinturon, pageable);
    }

    public List<Alumno> buscarPorNombreYNivel(String nombre, NivelCinturon nivelCinturon) {
        return alumnoRepository.findByNombreContainingIgnoreCaseAndNivelCinturon(nombre, nivelCinturon);
    }

    public Page<Alumno> buscarPorCategoria(CategoriaAlumno categoria, Pageable pageable) {
        return alumnoRepository.findByCategoria(categoria, pageable);
    }

    public Page<Alumno> buscarPorNombreYCategoria(String nombre, CategoriaAlumno categoria, Pageable pageable) {
        return alumnoRepository.findByNombreContainingIgnoreCaseAndCategoria(nombre, categoria, pageable);
    }

    public Page<Alumno> buscarPorNivelYCategoria(NivelCinturon nivelCinturon, CategoriaAlumno categoria,
            Pageable pageable) {
        return alumnoRepository.findByNivelCinturonAndCategoria(nivelCinturon, categoria, pageable);
    }

    public void actualizarAlumno(Alumno alumno) {
        alumnoRepository.save(alumno);
    }

    public long contarAlumnos() {
        return alumnoRepository.count();
    }

    public Page<Alumno> buscarPorNombreYNivel(String nombre, NivelCinturon nivelCinturon, Pageable pageable) {
        return alumnoRepository.findByNombreContainingIgnoreCaseAndNivelCinturon(nombre, nivelCinturon, pageable);
    }
}
