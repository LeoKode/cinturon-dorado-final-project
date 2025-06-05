package com.cinturondorado.dto;

import com.cinturondorado.model.Alumno;
import com.cinturondorado.model.Clase;
import com.cinturondorado.model.enums.NivelCinturon;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

@Data
public class AlumnoDTO {
    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;
    
    private NivelCinturon nivelCinturon;
    private String email;
    private String telefono;
    private boolean pagoPendiente;
    private Long claseId; // For single class selection
    private Set<Long> claseIds = new HashSet<>(); // For multiple classes

    public AlumnoDTO() {}

    public AlumnoDTO(Alumno alumno) {
        this.id = alumno.getId();
        this.nombre = alumno.getNombre();
        this.fechaNacimiento = alumno.getFechaNacimiento();
        this.nivelCinturon = alumno.getNivelCinturon();
        this.email = alumno.getEmail();
        this.telefono = alumno.getTelefono();
        
        // Get the first class ID if there are any classes
        if (alumno.getClases() != null && !alumno.getClases().isEmpty()) {
            this.claseId = alumno.getClases().iterator().next().getId();
        }
        
        // Store all class IDs
        if (alumno.getClases() != null) {
            this.claseIds = alumno.getClases().stream()
                .map(Clase::getId)
                .collect(Collectors.toSet());
        }
    }

    // Helper method to get the first class ID
    public Long getClaseId() {
        if (claseIds != null && !claseIds.isEmpty()) {
            return claseIds.iterator().next();
        }
        return null;
    }
}