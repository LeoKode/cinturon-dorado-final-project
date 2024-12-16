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
    @Column(name = "nivel_cinturon", nullable = true)
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
    
    // private boolean activo;
    
    private LocalDate ultimoPago;
    
    // Getters
    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Integer getEdad() {
        return edad;
    }

    public NivelCinturon getNivelCinturon() {
        return nivelCinturon;
    }

    public List<Pago> getPagos() {
        return pagos;
    }

    public Set<Clase> getClases() {
        return clases;
    }

    public boolean isPagoPendiente() {
        return pagos.stream()
                .anyMatch(pago -> !pago.isPagado());
    }

    public String getEmail() {
        return email;
    }

    //public boolean isActivo() { return activo; }

    public LocalDate getUltimoPago() { return ultimoPago; }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public void setNivelCinturon(NivelCinturon nivelCinturon) {
        this.nivelCinturon = nivelCinturon;
    }

    public void setPagos(List<Pago> pagos) {
        this.pagos = pagos;
    }

    public void setClases(Set<Clase> clases) {
        this.clases = clases;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    //public void setActivo(boolean activo) { this.activo = activo; }

    public void setUltimoPago(LocalDate ultimoPago) { this.ultimoPago = ultimoPago; }
} 