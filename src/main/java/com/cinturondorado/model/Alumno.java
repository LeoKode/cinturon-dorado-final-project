package com.cinturondorado.model;

import java.util.List;
import java.util.Set;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
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
    
    private Integer edad;
    
    @Enumerated(EnumType.STRING)
    // exclusivo para postgresql
    @ColumnTransformer(write = "?::nivel_cinturon_enum")
    private NivelCinturon nivelCinturon;
    
    @OneToMany(mappedBy = "alumno")
    private List<Pago> pagos;
    
    @ManyToMany
    @JoinTable(
        name = "alumno_clase",
        joinColumns = @JoinColumn(name = "alumno_id"),
        inverseJoinColumns = @JoinColumn(name = "clase_id")
    )
    private Set<Clase> clases;
    
    private String email;
    
    private LocalDate ultimoPago;

    public boolean isPagoPendiente() {
        return pagos.stream()
                .anyMatch(pago -> !pago.isPagado());
    }
} 