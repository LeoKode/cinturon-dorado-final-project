package com.cinturondorado.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cinturondorado.dto.InventarioDTO;
import com.cinturondorado.exception.ResourceNotFoundException;
import com.cinturondorado.model.Inventario;
import com.cinturondorado.model.enums.TipoEquipo;
import com.cinturondorado.repository.InventarioRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class InventarioService {
    private final InventarioRepository inventarioRepository;
    private final NotificacionService notificacionService;

    public InventarioService(InventarioRepository inventarioRepository,
                           NotificacionService notificacionService) {
        this.inventarioRepository = inventarioRepository;
        this.notificacionService = notificacionService;
    }

    public Inventario agregarItem(InventarioDTO inventarioDTO) {
        try {
            log.debug("Iniciando conversión de DTO a entidad: {}", inventarioDTO);
            Inventario item = new Inventario();
            item.setNombre(inventarioDTO.getNombre());
            item.setCantidad(inventarioDTO.getCantidad());
            item.setDescripcion(inventarioDTO.getDescripcion());
            item.setTipo(inventarioDTO.getTipo());
            item.setStockMinimo(inventarioDTO.getStockMinimo());
            item.setFechaUltimaActualizacion(LocalDate.now());

            log.debug("Intentando guardar item en base de datos");
            Inventario itemGuardado = inventarioRepository.save(item);
            log.info("Item guardado exitosamente con ID: {}", itemGuardado.getId());

            verificarStockMinimo(itemGuardado);
            return itemGuardado;
        } catch (Exception e) {
            log.error("Error al guardar item: {}", e.getMessage(), e);
            throw e;
        }
    }

    public void actualizarItem(Long id, String nombre, TipoEquipo tipo, Integer cantidad, 
                          Integer stockMinimo, String descripcion) {
    Inventario item = inventarioRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado"));
    
    item.setNombre(nombre);
    item.setTipo(tipo);
    item.setCantidad(cantidad);
    item.setStockMinimo(stockMinimo);
    item.setDescripcion(descripcion);
    item.setFechaUltimaActualizacion(LocalDate.now());
    
    Inventario itemActualizado = inventarioRepository.save(item);
    verificarStockMinimo(itemActualizado);
}

    public List<Inventario> obtenerItemsBajoStock() {
        return inventarioRepository.findAll().stream()
            .filter(item -> item.getCantidad() <= item.getStockMinimo())
            .collect(Collectors.toList());
    }

    public List<Inventario> obtenerPorTipo(TipoEquipo tipo) {
        try {
            log.debug("Intentando obtener items por tipo: {}", tipo);
            // Usar una query nativa para asegurar la conversión correcta del enum
            return inventarioRepository.findByTipo(tipo);
        } catch (Exception e) {
            log.error("Error al obtener items por tipo {}: {}", tipo, e.getMessage());
            e.printStackTrace(); // Para ver el stack trace completo
            return Collections.emptyList();
        }
    }

    private void verificarStockMinimo(Inventario item) {
        if (item.getCantidad() <= item.getStockMinimo()) {
            notificacionService.notificarStockBajo(item);
        }
    }

    @Scheduled(cron = "0 0 9 * * *") // Ejecutar todos los días a las 9:00 AM
    public void verificarInventarioBajo() {
        List<Inventario> itemsBajoStock = obtenerItemsBajoStock();
        if (!itemsBajoStock.isEmpty()) {
            notificacionService.enviarReporteInventarioBajo(itemsBajoStock);
        }
    }

    public Inventario obtenerPorId(Long id) {
        return inventarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado"));
    }

    public List<Inventario> obtenerTodoItems() {
        return inventarioRepository.findAll();
    }

    public void eliminarItem(Long id) {
        Inventario item = inventarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado"));
        inventarioRepository.delete(item);
    }

    public Page<Inventario> obtenerTodoItemsPaginados(Pageable pageable) {
        return inventarioRepository.findAll(pageable);
    }

    public Page<Inventario> obtenerPorTipoPaginado(TipoEquipo tipo, Pageable pageable) {
        return inventarioRepository.findByTipo(tipo, pageable);
    }

    public Page<Inventario> obtenerItemsBajoStockPaginados(Pageable pageable) {
        return inventarioRepository.findByStockMenorQueMinimo(pageable);
    }
} 
