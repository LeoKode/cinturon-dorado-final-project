package com.cinturondorado.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "clases")
public class Clase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @Column(name = "cupo_maximo", nullable = false)
    private Integer cupoMaximo = 20; // Valor por defecto

    @ManyToMany
    @JoinTable(name = "alumno_clase", joinColumns = @JoinColumn(name = "clase_id"), inverseJoinColumns = @JoinColumn(name = "alumno_id"))
    private Set<Alumno> alumnos = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "profesor_id")
    private Profesor profesor;

    @OneToMany(mappedBy = "clase", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CalendarioClase> calendarioClases = new HashSet<>();

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    // Getter para cantidad de alumnos
    public int getCantidadAlumnos() {
        return alumnos != null ? alumnos.size() : 0;
    }

    // Método para verificar si hay cupo disponible
    public boolean tieneCupoDisponible() {
        return getCantidadAlumnos() < cupoMaximo;
    }

    public Set<CalendarioClase> getHorarios() {
        return calendarioClases;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Set<Alumno> getAlumnos() {
        return alumnos;
    }

    public void setAlumnos(Set<Alumno> alumnos) {
        this.alumnos = alumnos;
    }

    public void setHorarios(Set<CalendarioClase> horarios) {
        this.calendarioClases = horarios;
    }

    public Integer getCupoMaximo() {
        return cupoMaximo;
    }

    public void setCupoMaximo(Integer cupoMaximo) {
        this.cupoMaximo = cupoMaximo;
    }

    // Helper methods
    public void addAlumno(Alumno alumno) {
        alumnos.add(alumno);
        alumno.getClases().add(this);
    }

    public void removeAlumno(Alumno alumno) {
        alumnos.remove(alumno);
        alumno.getClases().remove(this);
    }

    public void addCalendarioClase(CalendarioClase calendarioClase) {
        calendarioClases.add(calendarioClase);
        calendarioClase.setClase(this);
    }

    public void removeCalendarioClase(CalendarioClase calendarioClase) {
        calendarioClases.remove(calendarioClase);
        calendarioClase.setClase(null);
    }
}