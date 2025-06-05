package com.cinturondorado.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.cinturondorado.model.CalendarioClase;

@Repository
public interface CalendarioClaseRepository extends JpaRepository<CalendarioClase, Long> {
    boolean existsByDiaAndHora(String dia, String hora);
    Optional<CalendarioClase> findByClaseIdAndDiaAndHora(Long claseId, String dia, String hora);
    List<CalendarioClase> findByClaseIdOrderByHora(Long claseId);
}