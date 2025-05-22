package com.cinturondorado.model;

import com.cinturondorado.model.enums.TipoEquipo;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private Integer cantidad;
    
    private String descripcion;
    
    @Enumerated(EnumType.STRING)
    private TipoEquipo tipo;
    
    @Column(nullable = false)
    private Integer stockMinimo;
    
    private LocalDate fechaUltimaActualizacion;
} 