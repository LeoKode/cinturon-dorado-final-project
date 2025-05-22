package com.cinturondorado.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cinturondorado.model.HorarioDisponible;

public interface HorarioDisponibleRepository extends JpaRepository<HorarioDisponible, Long> {
    Optional<HorarioDisponible> findByHora(String hora);
    boolean existsByHora(String hora);
    List<HorarioDisponible> findByActivoOrderByHoraAsc(boolean activo);
}