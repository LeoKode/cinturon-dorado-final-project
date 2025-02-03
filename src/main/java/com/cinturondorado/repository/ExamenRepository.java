package com.cinturondorado.repository;

import com.cinturondorado.model.Examen;
import com.cinturondorado.model.enums.NivelCinturon;
import com.cinturondorado.model.enums.EstadoExamen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExamenRepository extends JpaRepository<Examen, Long> {
    
    // Búsquedas por nivel
    List<Examen> findByNivelActual(NivelCinturon nivelActual);
    List<Examen> findByNivelAspirante(NivelCinturon nivelAspirante);
    List<Examen> findByNivelActualAndNivelAspirante(NivelCinturon nivelActual, NivelCinturon nivelAspirante);

    // Búsquedas por estado
    List<Examen> findByEstado(EstadoExamen estado);
    
    // Búsquedas por alumno
    @Query("SELECT e FROM Examen e WHERE e.alumno.id = :alumnoId")
    List<Examen> findByAlumnoId(@Param("alumnoId") Long alumnoId);

    // Búsquedas por evaluador
    @Query("SELECT e FROM Examen e WHERE e.evaluador.id = :evaluadorId")
    List<Examen> findByEvaluadorId(@Param("evaluadorId") Long evaluadorId);

    // Búsqueda por fecha
    List<Examen> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

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
}
