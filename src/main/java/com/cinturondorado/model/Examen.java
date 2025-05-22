package com.cinturondorado.model;

import com.cinturondorado.model.enums.EstadoExamen;
import com.cinturondorado.model.enums.NivelCinturon;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import org.hibernate.annotations.ColumnTransformer;

import java.time.LocalDate;

@Entity
@Table(name = "examenes")
@Getter
@Setter
public class Examen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "alumno_id", nullable = false)
    private Alumno alumno;

    @ManyToOne
    @JoinColumn(name = "evaluador_id")
    private Profesor evaluador;

    @Column(nullable = false)
    private LocalDate fecha;

    @ColumnTransformer(write = "?::nivel_cinturon_enum")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelCinturon nivelActual;

    @ColumnTransformer(write = "?::nivel_cinturon_enum")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelCinturon nivelAspirante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoExamen estado;

    @Column(length = 1000)
    private String observaciones;

    private String resultado;

    @PrePersist
    protected void onCreate() {
        if (estado == null) {
            estado = EstadoExamen.PENDIENTE;
        }
    }
}