package com.cinturondorado.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.cinturondorado.model.Clase;

@Repository
public interface ClaseRepository extends JpaRepository<Clase, Long> {
    // Add custom queries if needed
}