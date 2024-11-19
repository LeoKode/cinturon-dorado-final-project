package com.cinturondorado.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {
    
    @Mock
    private InventarioRepository inventarioRepository;
    
    @Mock
    private NotificacionService notificacionService;
    
    @InjectMocks
    private InventarioService inventarioService;
    
    @Test
    void agregarItem_ConDatosValidos_GuardaYRetornaItem() {
        // Arrange
        InventarioDTO dto = new InventarioDTO();
        dto.setNombre("Peto de protección");
        dto.setCantidad(10);
        dto.setTipo(TipoEquipo.PROTECCION);
        dto.setStockMinimo(5);
        
        Inventario itemEsperado = new Inventario();
        itemEsperado.setId(1L);
        itemEsperado.setNombre(dto.getNombre());
        itemEsperado.setCantidad(dto.getCantidad());
        itemEsperado.setTipo(dto.getTipo());
        
        when(inventarioRepository.save(any(Inventario.class)))
            .thenReturn(itemEsperado);
            
        // Act
        Inventario resultado = inventarioService.agregarItem(dto);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(dto.getNombre(), resultado.getNombre());
        assertEquals(dto.getCantidad(), resultado.getCantidad());
        verify(inventarioRepository).save(any(Inventario.class));
    }
    
    @Test
    void verificarStockMinimo_CuandoStockEsBajo_EnviaNotificacion() {
        // Arrange
        Inventario item = new Inventario();
        item.setId(1L);
        item.setNombre("Uniforme");
        item.setCantidad(3);
        item.setStockMinimo(5);
        
        List<Inventario> itemsBajoStock = Collections.singletonList(item);
        
        when(inventarioRepository.findAll())
            .thenReturn(itemsBajoStock);
            
        // Act
        List<Inventario> resultado = inventarioService.obtenerItemsBajoStock();
        
        // Assert
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(notificacionService).notificarStockBajo(item);
    }
    
    @Test
    void actualizarCantidad_ItemNoExistente_LanzaExcepcion() {
        // Arrange
        Long id = 1L;
        when(inventarioRepository.findById(id))
            .thenReturn(Optional.empty());
            
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, 
            () -> inventarioService.actualizarCantidad(id, 10));
    }
} 