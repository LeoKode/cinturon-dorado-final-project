package com.cinturondorado.dto;

import com.cinturondorado.model.enums.CategoriaAlumno;
import com.cinturondorado.model.enums.NivelCinturon;
import lombok.Data;

import java.time.LocalDate;

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

    @NotNull(message = "La categoría es obligatoria")
    private CategoriaAlumno categoria;
} 