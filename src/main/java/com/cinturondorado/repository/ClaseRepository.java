package com.cinturondorado.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cinturondorado.model.Clase;
import java.util.List;

public interface ClaseRepository extends JpaRepository<Clase, Long> {
    boolean existsByNombre(String nombre);
    
    @Query("SELECT c FROM Clase c JOIN c.calendarioClases h WHERE h.dia = :dia ORDER BY h.hora")
    List<Clase> findByDiaOrderByHora(@Param("dia") String dia);
}
