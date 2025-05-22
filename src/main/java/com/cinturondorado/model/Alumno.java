package com.cinturondorado.model;

import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnTransformer;

import com.cinturondorado.model.enums.CategoriaAlumno;
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
    
    @OneToMany(mappedBy = "alumno")
    private List<Pago> pagos;
    
    private String email;

    private String telefono;
    
    private LocalDate ultimoPago;

    @Column(nullable = false)
    private boolean activo = true;
    
    @Column(nullable = false)
    private BigDecimal cuotaMensual;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaAlumno categoria;

    @PrePersist
    @PreUpdate
    private void setCuotaBasadaEnCategoria() {
        if (this.categoria != null) {
            this.cuotaMensual = this.categoria.getCuotaMensual();
        }
    }

    public boolean isPagoPendiente() {
        return pagos.stream()
                .anyMatch(pago -> !pago.isPagado());
    }
} 