package com.cinturondorado.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class InventarioRepositoryTest {
    
    @Autowired
    private InventarioRepository inventarioRepository;
    
    @Test
    void findByTipo_RetornaItemsCorrectos() {
        // Arrange
        Inventario item1 = new Inventario();
        item1.setNombre("Uniforme");
        item1.setCantidad(10);
        item1.setTipo(TipoEquipo.UNIFORME);
        
        Inventario item2 = new Inventario();
        item2.setNombre("Peto");
        item2.setCantidad(5);
        item2.setTipo(TipoEquipo.PROTECCION);
        
        inventarioRepository.saveAll(Arrays.asList(item1, item2));
        
        // Act
        List<Inventario> uniformes = inventarioRepository.findByTipo(TipoEquipo.UNIFORME);
        
        // Assert
        assertEquals(1, uniformes.size());
        assertEquals("Uniforme", uniformes.get(0).getNombre());
    }
    
    @Test
    void findByCantidadLessThanEqual_RetornaItemsBajoStock() {
        // Arrange
        Inventario item1 = new Inventario();
        item1.setNombre("Guantes");
        item1.setCantidad(3);
        item1.setStockMinimo(5);
        
        Inventario item2 = new Inventario();
        item2.setNombre("Cascos");
        item2.setCantidad(10);
        item2.setStockMinimo(5);
        
        inventarioRepository.saveAll(Arrays.asList(item1, item2));
        
        // Act
        List<Inventario> itemsBajoStock = 
            inventarioRepository.findByCantidadLessThanEqual(5);
        
        // Assert
        assertEquals(1, itemsBajoStock.size());
        assertEquals("Guantes", itemsBajoStock.get(0).getNombre());
    }
} 