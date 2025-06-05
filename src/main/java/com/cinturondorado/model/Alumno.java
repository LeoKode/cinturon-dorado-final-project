package com.cinturondorado.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnTransformer;

import com.cinturondorado.model.enums.NivelCinturon;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "alumnos")
@Getter
@Setter
public class Alumno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @ColumnTransformer(write = "?::nivel_cinturon_enum")
    private NivelCinturon nivelCinturon;

    @OneToMany(mappedBy = "alumno", cascade = CascadeType.ALL)
    private List<Pago> pagos = new ArrayList<>();

    private String email;

    private String telefono;

    private LocalDate ultimoPago;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(nullable = false)
    private BigDecimal cuotaMensual;

    @ManyToMany(mappedBy = "alumnos")
    private Set<Clase> clases = new HashSet<>();

    public boolean isPagoPendiente() {
        return pagos.stream()
                .anyMatch(pago -> !pago.isPagado());
    }
}