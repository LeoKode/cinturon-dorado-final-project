package com.cinturondorado.model;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnTransformer;

import com.cinturondorado.model.enums.TipoClase;

@Entity
@Table(name = "clases")
public class Clase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /*@ManyToOne
    @JoinColumn(name = "profesor_id", nullable = false)
    private Profesor profesor;  */
    @Column(nullable = false)
    private String titulo;
    
    @Column(nullable = false)
    private int duracion; // duracion en minutos
    
    @Column
    private LocalDateTime fechaHora;
    
    @Enumerated(EnumType.STRING)
    @ColumnTransformer(write = "?::tipo_clase_enum")
    private TipoClase tipo;
    
    // @ManyToMany(mappedBy = "clases")
    // private Set<Alumno> alumnos;
    
    // Getters
    public Long getId() {
        return id;
    }
    
    // public Profesor getProfesor() {
    //     return profesor;
    // }

    public String getTitulo() {
        return titulo;
    }

    public int getDuracion() {
        return duracion;
    }
    
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
    
    public TipoClase getTipo() {
        return tipo;
    }
    
    // public Set<Alumno> getAlumnos() {
    //     return alumnos;
    // }
    
    // Setters
    public void setId(Long id) {
        this.id = id;
    }
    
    // public void setProfesor(Profesor profesor) {
    //     this.profesor = profesor;
    // }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }
    
    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
    
    public void setTipo(TipoClase tipo) {
        this.tipo = tipo;
    }
    
    // public void setAlumnos(Set<Alumno> alumnos) {
    //     this.alumnos = alumnos;
    // }
} 