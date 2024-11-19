package com.cinturondorado.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cinturondorado.dto.InventarioDTO;
import com.cinturondorado.exception.ResourceNotFoundException;
import com.cinturondorado.model.Inventario;
import com.cinturondorado.model.enums.TipoEquipo;
import com.cinturondorado.repository.InventarioRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InventarioService {
    private final InventarioRepository inventarioRepository;
    private final NotificacionService notificacionService;

    @Autowired
    public InventarioService(InventarioRepository inventarioRepository,
                           NotificacionService notificacionService) {
        this.inventarioRepository = inventarioRepository;
        this.notificacionService = notificacionService;
    }

    public Inventario agregarItem(InventarioDTO inventarioDTO) {
        Inventario item = new Inventario();
        item.setNombre(inventarioDTO.getNombre());
        item.setCantidad(inventarioDTO.getCantidad());
        item.setDescripcion(inventarioDTO.getDescripcion());
        item.setTipo(inventarioDTO.getTipo());
        item.setStockMinimo(inventarioDTO.getStockMinimo());
        item.setFechaUltimaActualizacion(LocalDate.now());

        Inventario itemGuardado = inventarioRepository.save(item);
        verificarStockMinimo(itemGuardado);
        return itemGuardado;
    }

    public void actualizarCantidad(Long id, Integer cantidad) {
        Inventario item = inventarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado"));
        
        item.setCantidad(cantidad);
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
        return inventarioRepository.findByTipo(tipo);
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'obtenerPorId'");
    }
} 