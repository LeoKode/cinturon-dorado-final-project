package com.cinturondorado.repository;

import com.cinturondorado.model.Examen;
import com.cinturondorado.model.enums.NivelCinturon;
import com.cinturondorado.model.enums.EstadoExamen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExamenRepository extends JpaRepository<Examen, Long> {
    
    // Búsquedas por nivel
    List<Examen> findByNivelActual(NivelCinturon nivelActual);
    List<Examen> findByNivelAspirante(NivelCinturon nivelAspirante);
    List<Examen> findByNivelActualAndNivelAspirante(NivelCinturon nivelActual, NivelCinturon nivelAspirante);
    long countByFechaGreaterThanEqualAndEstado(LocalDate fecha, EstadoExamen estado);
    Page<Examen> findByNivelAspirante(NivelCinturon nivelAspirante, Pageable pageable);
    Page<Examen> findByEstado(EstadoExamen estado, Pageable pageable);
    Page<Examen> findByNivelAspiranteAndEstado(NivelCinturon nivelAspirante, EstadoExamen estado, Pageable pageable);

    // Búsquedas por estado
    List<Examen> findByEstado(EstadoExamen estado);
    
    // Búsquedas por alumno
    @Query("SELECT e FROM Examen e WHERE e.alumno.id = :alumnoId")
    List<Examen> findByAlumnoId(@Param("alumnoId") Long alumnoId);

    // Búsquedas por evaluador
    @Query("SELECT e FROM Examen e WHERE e.evaluador.id = :evaluadorId")
    List<Examen> findByEvaluadorId(@Param("evaluadorId") Long evaluadorId);

    // Búsqueda por fecha
    Page<Examen> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin, Pageable pageable);

    // Búsqueda combinada
    @Query("SELECT e FROM Examen e WHERE " +
           "(:nivelActual IS NULL OR e.nivelActual = :nivelActual) AND " +
           "(:nivelAspirante IS NULL OR e.nivelAspirante = :nivelAspirante) AND " +
           "(:estado IS NULL OR e.estado = :estado)")
    List<Examen> buscarPorFiltros(
        @Param("nivelActual") NivelCinturon nivelActual,
        @Param("nivelAspirante") NivelCinturon nivelAspirante,
        @Param("estado") EstadoExamen estado
    );

    @Query("SELECT e FROM Examen e WHERE e.fecha BETWEEN :fechaDesde AND :fechaHasta")
    Page<Examen> findByFechaRange(
        @Param("fechaDesde") LocalDate fechaDesde,
        @Param("fechaHasta") LocalDate fechaHasta,
        Pageable pageable
    );

    @Query("SELECT e FROM Examen e WHERE " +
           "(:nivelAspirante IS NULL OR e.nivelAspirante = :nivelAspirante) AND " +
           "(:estado IS NULL OR e.estado = :estado) AND " +
           "e.fecha BETWEEN :fechaInicio AND :fechaFin")
    Page<Examen> findByFiltersAndDateRange(
        @Param("nivelAspirante") NivelCinturon nivelAspirante,
        @Param("estado") EstadoExamen estado,
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin,
        Pageable pageable);

    @Query("SELECT e FROM Examen e WHERE " +
           "(:claseId IS NULL OR e.alumno.id IN (SELECT a.id FROM Alumno a JOIN a.clases c WHERE c.id = :claseId)) AND " +
           "(:nivelAspirante IS NULL OR e.nivelAspirante = :nivelAspirante) AND " +
           "(:estado IS NULL OR e.estado = :estado) AND " +
           "e.fecha BETWEEN :fechaInicio AND :fechaFin")
    Page<Examen> findByAllFilters(
            @Param("claseId") Long claseId,
            @Param("nivelAspirante") NivelCinturon nivelAspirante,
            @Param("estado") EstadoExamen estado,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            Pageable pageable);

    @Query("SELECT e FROM Examen e WHERE " +
           "e.alumno.id IN (SELECT a.id FROM Alumno a JOIN a.clases c WHERE c.id = :claseId) AND " +
           "(:nivelAspirante IS NULL OR e.nivelAspirante = :nivelAspirante) AND " +
           "(:estado IS NULL OR e.estado = :estado)")
    Page<Examen> findByAlumnoClaseAndFilters(
            @Param("claseId") Long claseId,
            @Param("nivelAspirante") NivelCinturon nivelAspirante,
            @Param("estado") EstadoExamen estado,
            Pageable pageable);
}
