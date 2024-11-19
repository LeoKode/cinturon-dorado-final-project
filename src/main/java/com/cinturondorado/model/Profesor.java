package com.cinturondorado.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.cinturondorado.model.enums.NivelCinturon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profesores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profesor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String especialidad;
    
    @Enumerated(EnumType.STRING)
    private NivelCinturon nivelCinturon;
    
    @OneToMany(mappedBy = "profesor")
    private List<Clase> clases;
    
    private boolean activo = true;
    
    @Column(nullable = false)
    private String telefono;
    
    @Column(nullable = false)
    private String email;
} 