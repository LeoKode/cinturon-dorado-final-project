package com.cinturondorado.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cinturondorado.model.Clase;
import com.cinturondorado.model.HorarioDisponible;

@Repository
public interface ClaseRepository extends JpaRepository<Clase, Long> {
    Optional<Clase> findByDiaAndHorario(String dia, HorarioDisponible horario);
    List<Clase> findByHorario(HorarioDisponible horario);
    boolean existsByDiaAndHorario(String dia, HorarioDisponible horario);
}