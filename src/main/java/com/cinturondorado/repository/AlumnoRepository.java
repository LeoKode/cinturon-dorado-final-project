package com.cinturondorado.repository;

import com.cinturondorado.model.Alumno;
import com.cinturondorado.model.enums.CategoriaAlumno;
import com.cinturondorado.model.enums.NivelCinturon;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {

    Page<Alumno> findByNivelCinturon(NivelCinturon nivelCinturon, Pageable pageable);

    Page<Alumno> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    List<Alumno> findByNombreContainingIgnoreCaseAndNivelCinturon(String nombre, NivelCinturon nivelCinturon);

    Page<Alumno> findByNombreContainingIgnoreCaseAndNivelCinturon(String nombre, NivelCinturon nivelCinturon,
            Pageable pageable);

    Page<Alumno> findByCategoria(CategoriaAlumno categoria, Pageable pageable);

    Page<Alumno> findByNombreContainingIgnoreCaseAndCategoria(String nombre,
            CategoriaAlumno categoria, Pageable pageable);

    Page<Alumno> findByNivelCinturonAndCategoria(NivelCinturon nivelCinturon,
            CategoriaAlumno categoria, Pageable pageable);

    Page<Alumno> findByActivo(boolean activo, Pageable pageable);

    @Query("SELECT a FROM Alumno a WHERE EXISTS (SELECT p FROM Pago p WHERE p.alumno = a AND p.pagado = false)")
    List<Alumno> findAlumnosConPagosPendientes();
}