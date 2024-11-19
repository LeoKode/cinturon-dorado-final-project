package com.cinturondorado.repository;

import com.cinturondorado.model.Profesor;
import com.cinturondorado.model.enums.NivelCinturon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfesorRepository extends JpaRepository<Profesor, Long> {
    List<Profesor> findByActivo(boolean activo);
    Optional<Profesor> findByEmail(String email);
    List<Profesor> findByNivelCinturon(NivelCinturon nivelCinturon);
} 