package com.cinturondorado.service;

import javax.validation.ValidationException;

import org.hibernate.ResourceClosedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cinturondorado.model.Alumno;
import com.cinturondorado.model.enums.NivelCinturon;
import com.cinturondorado.repository.AlumnoRepository;
import com.cinturondorado.dto.AlumnoDTO;
import com.cinturondorado.exception.ResourceNotFoundException;

import java.util.List;

@Service
@Transactional
public class AlumnoService {
    private final AlumnoRepository alumnoRepository;
    private final NotificacionService notificacionService;

    @Autowired
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
        alumno.setEdad(alumnoDTO.getEdad());
        alumno.setEmail(alumnoDTO.getEmail());
        alumno.setNivelCinturon(alumnoDTO.getNivelCinturon()); // Nivel inicial
        
        // Guardar / revisar luego lo de notificar
        Alumno alumnoGuardado = alumnoRepository.save(alumno);
        System.out.println("Alumno guardado correctamente");
        
        return alumnoGuardado;
    }

    public void actualizarNivelCinturon(Long alumnoId, NivelCinturon nuevoNivel) {
        Alumno alumno = alumnoRepository.findById(alumnoId)
            .orElseThrow(() -> new ResourceClosedException("Alumno no encontrado"));
            
        alumno.setNivelCinturon(nuevoNivel);
        alumnoRepository.save(alumno);
        notificacionService.notificarCambioNivel(alumno, nuevoNivel);
    }

    private void validarDatosAlumno(AlumnoDTO alumnoDTO) {
        if (alumnoDTO.getEdad() < 4) {
            throw new ValidationException("El alumno debe tener al menos 4 años");
        }

        if (alumnoDTO.getNombre() == null || alumnoDTO.getNombre().trim().isEmpty()) {
            throw new ValidationException("El nombre del alumno no puede estar vacío");
        }
        
        if (alumnoDTO.getEmail() == null || !alumnoDTO.getEmail().matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
            throw new ValidationException("El email proporcionado no es válido");
        }
        // Más validaciones según necesidad
    }

    public Alumno obtenerPorId(Long id) {
        return alumnoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado con ID: " + id));
    }

    public List<Alumno> obtenerAlumnosConPagosPendientes() {
        return alumnoRepository.findByPagoPendienteTrue();
    }

    public List<Alumno> listarTodos() {
        return alumnoRepository.findAll();
    }

    public void eliminar(Long id) {
        Alumno alumno = obtenerPorId(id);
        alumnoRepository.delete(alumno);
    }

    public List<Alumno> buscarPorNombre(String nombre) {
        return alumnoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public void actualizarAlumno(AlumnoDTO alumnoDTO) {
        // Aquí puedes implementar la lógica para actualizar el alumno en la base de datos
        Alumno alumnoExistente = alumnoRepository.findById(alumnoDTO.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado con ID: " + alumnoDTO.getId()));
        
        alumnoExistente.setNombre(alumnoDTO.getNombre());
        alumnoExistente.setEdad(alumnoDTO.getEdad());
        alumnoExistente.setEmail(alumnoDTO.getEmail());
        alumnoExistente.setNivelCinturon(alumnoDTO.getNivelCinturon());
        
        alumnoRepository.save(alumnoExistente);
    }
} 
