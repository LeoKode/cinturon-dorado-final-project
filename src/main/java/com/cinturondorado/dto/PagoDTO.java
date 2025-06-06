package com.cinturondorado.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.springframework.format.annotation.DateTimeFormat;

import com.cinturondorado.model.enums.EstadoPago;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoDTO {
    private Long id;
    
    @NotNull(message = "El ID del alumno es obligatorio")
    private Long alumnoId;
    
    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser positivo")
    private BigDecimal monto;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;

    private EstadoPago estado;

    private String concepto;

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }
} 