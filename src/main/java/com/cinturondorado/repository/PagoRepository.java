package com.cinturondorado.repository;

import com.cinturondorado.model.Pago;
import com.cinturondorado.model.enums.EstadoPago;
import com.cinturondorado.model.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByAlumnoAndEstado(Alumno alumno, EstadoPago estado);
    List<Pago> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);
    List<Pago> findByAlumnoId(Long alumnoId);
} 