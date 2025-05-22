package com.cinturondorado.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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

    @Column(name = "dia_semana")
    private String dia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "horario_id", nullable = false)
    private HorarioDisponible horario;
    
    @ManyToOne
    @JoinColumn(name = "alumno_id")
    private Alumno alumno;

    
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

    public String getHoraInicio() {
        return horario != null ? horario.getHora() : null;
    }

    public String getDia() {
        return dia;
    }

    public HorarioDisponible getHorario() {
        return horario;
    }
    
    public Alumno getAlumno() {
        return alumno;
    }

    
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

    public void setDia(String dia) {
        this.dia = dia;
    }

    public void setHorario(HorarioDisponible horario) {
        this.horario = horario;
    }
    
    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }
} 