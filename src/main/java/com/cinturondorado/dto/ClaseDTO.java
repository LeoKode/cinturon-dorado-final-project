package com.cinturondorado.dto;

import com.cinturondorado.model.enums.TipoClase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaseDTO {
    
    private Long id;
    
    @NotBlank(message = "El título es obligatorio")
    private String titulo;
    
    @Min(value = 1, message = "La duración debe ser mayor a 0")
    private int duracion;
    
    @NotNull(message = "La fecha y hora son obligatorias")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime fechaHora;
    
    @NotNull(message = "El tipo de clase es obligatorio")
    private TipoClase tipo;
}
