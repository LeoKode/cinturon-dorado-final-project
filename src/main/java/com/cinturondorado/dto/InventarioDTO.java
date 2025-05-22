package com.cinturondorado.dto;

import com.cinturondorado.model.enums.TipoEquipo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarioDTO {
    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer cantidad;
    
    private String descripcion;
    
    @NotNull(message = "El tipo de equipo es obligatorio")
    private TipoEquipo tipo;
    
    @Min(value = 1, message = "El stock mínimo debe ser mayor a 0")
    private Integer stockMinimo;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaUltimaActualizacion;
} 