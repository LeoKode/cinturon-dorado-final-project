package com.cinturondorado.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import com.cinturondorado.dto.ApiResponse;
import com.cinturondorado.dto.InventarioDTO;
import com.cinturondorado.model.Inventario;
import com.cinturondorado.model.enums.TipoEquipo;
import com.cinturondorado.service.InventarioService;

@RestController
@RequestMapping("/api/inventario")
@PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
public class InventarioController {
    private final InventarioService inventarioService;

    @Autowired
    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InventarioDTO>> agregarItem(
            @Valid @RequestBody InventarioDTO inventarioDTO) {
        Inventario item = inventarioService.agregarItem(inventarioDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(convertirADTO(item)));
    }

    @PutMapping("/{id}/cantidad")
    public ResponseEntity<ApiResponse<InventarioDTO>> actualizarCantidad(
            @PathVariable Long id,
            @RequestParam @Min(0) Integer cantidad) {
        inventarioService.actualizarCantidad(id, cantidad);
        return ResponseEntity.ok(ApiResponse.success(
            convertirADTO(inventarioService.obtenerPorId(id))));
    }

    @GetMapping("/bajo-stock")
    public ResponseEntity<ApiResponse<List<InventarioDTO>>> obtenerItemsBajoStock() {
        List<Inventario> items = inventarioService.obtenerItemsBajoStock();
        List<InventarioDTO> itemsDTO = items.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(itemsDTO));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<ApiResponse<List<InventarioDTO>>> obtenerPorTipo(
            @PathVariable TipoEquipo tipo) {
        List<Inventario> items = inventarioService.obtenerPorTipo(tipo);
        List<InventarioDTO> itemsDTO = items.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(itemsDTO));
    }

    private InventarioDTO convertirADTO(Inventario item) {
            return new InventarioDTO(
                item.getId(),
                item.getNombre(),
                item.getCantidad(),
                item.getDescripcion(),
                item.getTipo(),
                item.getStockMinimo()
        );
    }
} 