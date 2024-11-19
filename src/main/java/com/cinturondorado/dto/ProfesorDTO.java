package com.cinturondorado.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.cinturondorado.model.enums.NivelCinturon;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfesorDTO {
    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @NotBlank(message = "La especialidad es obligatoria")
    private String especialidad;
    
    @NotNull(message = "El nivel de cinturón es obligatorio")
    private NivelCinturon nivelCinturon;
    
    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;
    
    @Email(message = "El email debe ser válido")
    @NotBlank(message = "El email es obligatorio")
    private String email;
} 