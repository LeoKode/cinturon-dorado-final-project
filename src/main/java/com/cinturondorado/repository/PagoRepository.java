package com.cinturondorado.repository;

import com.cinturondorado.model.Pago;
import com.cinturondorado.model.enums.EstadoPago;
import com.cinturondorado.model.Alumno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    
    // Consultas básicas
    Page<Pago> findByAlumnoId(Long alumnoId, Pageable pageable);
    Page<Pago> findByEstado(EstadoPago estado, Pageable pageable);
    long countByEstado(EstadoPago estado);
    
    // Consultas por nombre de alumno
    Page<Pago> findByAlumnoNombreContainingIgnoreCase(String nombre, Pageable pageable);
    Page<Pago> findByEstadoAndAlumnoNombreContainingIgnoreCase(EstadoPago estado, String nombre, Pageable pageable);

    // Consultas para gestión de pagos mensuales
    @Query(value = "SELECT EXISTS(SELECT 1 FROM pagos p " +
           "WHERE p.alumno_id = :alumnoId " +
           "AND EXTRACT(YEAR FROM p.fecha) * 100 + EXTRACT(MONTH FROM p.fecha) = :yearMonth)", 
           nativeQuery = true)
    boolean existsByAlumnoIdAndFechaYearMonth(
            @Param("alumnoId") Long alumnoId, 
            @Param("yearMonth") Integer yearMonth);

    @Query(value = "SELECT p.* FROM pagos p " +
           "LEFT JOIN alumnos a ON p.alumno_id = a.id " +
           "WHERE (:estado IS NULL OR p.estado = :estado) " +
           "AND (:nombre IS NULL OR LOWER(a.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "AND (EXTRACT(YEAR FROM p.fecha) * 100 + EXTRACT(MONTH FROM p.fecha)) = :yearMonth " +
           "ORDER BY p.id ASC", 
           countQuery = "SELECT COUNT(*) FROM pagos p " +
                       "LEFT JOIN alumnos a ON p.alumno_id = a.id " +
                       "WHERE (:estado IS NULL OR p.estado = :estado) " +
                       "AND (:nombre IS NULL OR LOWER(a.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
                       "AND (EXTRACT(YEAR FROM p.fecha) * 100 + EXTRACT(MONTH FROM p.fecha)) = :yearMonth",
           nativeQuery = true)
    Page<Pago> findByFiltersAndYearMonth(
            @Param("estado") String estado,
            @Param("nombre") String nombre,
            @Param("yearMonth") Integer yearMonth,
            Pageable pageable);

    // Consultas para notificaciones y recordatorios
    List<Pago> findByAlumnoAndEstado(Alumno alumno, EstadoPago estado);
    Page<Pago> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin, Pageable pageable);
}