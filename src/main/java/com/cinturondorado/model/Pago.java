package com.cinturondorado.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.cinturondorado.model.enums.EstadoPago;

@Entity
@Table(name = "pagos")
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "alumno_id", nullable = false)
    private Alumno alumno;
    
    @Column(name = "fecha", nullable = false, columnDefinition = "DATE")
    private LocalDate fecha;
    
    @Column(nullable = false)
    private BigDecimal monto;
    
    @Column(nullable = false)
    private boolean pagado;
    
    @Column(nullable = false)
    private String concepto;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPago estado;
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public Alumno getAlumno() {
        return alumno;
    }
    
    public LocalDate getFecha() {
        return fecha;
    }
    
    public BigDecimal getMonto() {
        return monto;
    }
    
    public boolean isPagado() {
        return pagado;
    }
    
    public String getConcepto() {
        return concepto;
    }
    
    public EstadoPago getEstado() {
        return estado;
    }
    
    // Setters
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }
    
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    
    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
    
    public void setPagado(boolean pagado) {
        this.pagado = pagado;
    }
    
    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }
    
    public void setEstado(EstadoPago estado) {
        this.estado = estado;
    }
} 