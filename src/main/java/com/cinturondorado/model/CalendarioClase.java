package com.cinturondorado.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;

@Entity
@Table(name = "calendario_clases",
       uniqueConstraints = @UniqueConstraint(columnNames = {"dia", "hora"}))
@Data
public class CalendarioClase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String hora;

    @Column(nullable = false)
    private String dia;

    @ManyToOne
    @JoinColumn(name = "clase_id")
    private Clase clase;
}