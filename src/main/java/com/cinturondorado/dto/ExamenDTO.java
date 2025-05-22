package com.cinturondorado.dto;

import com.cinturondorado.model.enums.NivelCinturon;
import com.cinturondorado.model.Alumno;
import com.cinturondorado.model.enums.EstadoExamen;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Future;
import javax.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExamenDTO {

    private Long id;

    @NotNull(message = "El alumno es requerido")
    private List<Long> alumnoIds;

    //@NotNull(message = "El evaluador es requerido")
    private Long evaluadorId;

    @NotNull(message = "La fecha es requerida")
    @Future(message = "La fecha debe ser futura")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDate fecha;

    @NotNull(message = "El nivel actual es requerido")
    private NivelCinturon nivelActual;

    @NotNull(message = "El nivel aspirante es requerido")
    private NivelCinturon nivelAspirante;

    private EstadoExamen estado;

    private String resultado; 

    @Size(max = 1000, message = "Las observaciones no pueden exceder los 1000 caracteres")
    private String observaciones;

    private boolean aprobado;
    
    private LocalDate fechaEvaluacion;
}
