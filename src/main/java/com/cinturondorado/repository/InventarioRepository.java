package com.cinturondorado.repository;

import com.cinturondorado.model.Inventario;
import com.cinturondorado.model.enums.TipoEquipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    List<Inventario> findByTipo(TipoEquipo tipo);
    List<Inventario> findByCantidadLessThanEqual(Integer cantidad);
    List<Inventario> findByStockMinimo(Integer stockMinimo);
    Page<Inventario> findByTipo(TipoEquipo tipo, Pageable pageable);

    @Query("SELECT i FROM Inventario i WHERE i.cantidad <= i.stockMinimo")
    Page<Inventario> findByStockMenorQueMinimo(Pageable pageable);
} 