package com.cinturondorado.dto;

import com.cinturondorado.model.enums.NivelCinturon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoDTO {
    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @Min(value = 4, message = "La edad mínima es 4 años")
    private Integer edad;
    
    private NivelCinturon nivelCinturon;

    private String email;

    private boolean pagoPendiente;
} 