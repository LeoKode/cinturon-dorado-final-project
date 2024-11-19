package com.cinturondorado.repository;

import com.cinturondorado.model.Alumno;
import com.cinturondorado.model.enums.NivelCinturon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {
    List<Alumno> findByNivelCinturon(NivelCinturon nivelCinturon);
    @Query("SELECT a FROM Alumno a WHERE EXISTS (SELECT p FROM a.pagos p WHERE p.pagado = false)")
    List<Alumno> findByPagoPendienteTrue();
    List<Alumno> findByNombreContainingIgnoreCase(String nombre);
} 