package com.cinturondorado.controller;

import com.cinturondorado.dto.ApiResponse;
import com.cinturondorado.dto.PagoDTO;
import com.cinturondorado.model.Pago;
import com.cinturondorado.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/pagos")
@Validated
public class PagoController {
    private final PagoService pagoService;
    
    @Autowired
    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<PagoDTO>> registrarPago(
            @Valid @RequestBody PagoDTO pagoDTO) {
        Pago pago = pagoService.registrarPago(pagoDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(convertirADTO(pago)));
    }
    
    @GetMapping("/alumno/{alumnoId}")
    public ResponseEntity<ApiResponse<List<PagoDTO>>> obtenerPagosPorAlumno(
            @PathVariable Long alumnoId) {
        List<Pago> pagos = pagoService.obtenerPagosPorAlumno(alumnoId);
        List<PagoDTO> pagosDTO = pagos.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(pagosDTO));
    }
    
    @GetMapping("/reporte")
    public ResponseEntity<ApiResponse<List<PagoDTO>>> obtenerReportePagos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<Pago> pagos = pagoService.obtenerPagosPorRangoFecha(fechaInicio, fechaFin);
        List<PagoDTO> pagosDTO = pagos.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(pagosDTO));
    }

    private PagoDTO convertirADTO(Pago pago) {
        PagoDTO dto = new PagoDTO();
        dto.setId(pago.getId());
        dto.setMonto(pago.getMonto());
        dto.setFecha(pago.getFecha());
        dto.setAlumnoId(pago.getAlumno().getId());
        dto.setConcepto(pago.getConcepto());
        return dto;
    }
} 