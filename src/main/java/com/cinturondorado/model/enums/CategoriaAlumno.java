package com.cinturondorado.model.enums;

import java.math.BigDecimal;

public enum CategoriaAlumno {
    ADULTOS(new BigDecimal("45.00")),
    MENORES(new BigDecimal("35.00"));

    private final BigDecimal cuotaMensual;

    CategoriaAlumno(BigDecimal cuotaMensual) {
        this.cuotaMensual = cuotaMensual;
    }

    public BigDecimal getCuotaMensual() {
        return cuotaMensual;
    }
}
