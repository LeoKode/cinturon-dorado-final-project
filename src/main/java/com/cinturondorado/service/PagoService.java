package com.cinturondorado.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cinturondorado.dto.PagoDTO;
import com.cinturondorado.exception.ResourceNotFoundException;
import com.cinturondorado.model.Alumno;
import com.cinturondorado.model.Pago;
import com.cinturondorado.model.enums.EstadoPago;
import com.cinturondorado.repository.AlumnoRepository;
import com.cinturondorado.repository.PagoRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class PagoService {
    private final PagoRepository pagoRepository;
    private final AlumnoRepository alumnoRepository;
    private final NotificacionService notificacionService;

    @Autowired
    public PagoService(PagoRepository pagoRepository,
                      AlumnoRepository alumnoRepository,
                      NotificacionService notificacionService) {
        this.pagoRepository = pagoRepository;
        this.alumnoRepository = alumnoRepository;
        this.notificacionService = notificacionService;
    }

    public Pago registrarPago(PagoDTO pagoDTO) {
        Alumno alumno = alumnoRepository.findById(pagoDTO.getAlumnoId())
            .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado"));

        Pago pago = new Pago();
        pago.setAlumno(alumno);
        pago.setMonto(pagoDTO.getMonto());
        pago.setFecha(LocalDate.now());
        pago.setEstado(EstadoPago.PAGADO);

        Pago pagoGuardado = pagoRepository.save(pago);
        notificacionService.enviarConfirmacionPago(pagoGuardado);

        return pagoGuardado;
    }

    @Scheduled(cron = "0 0 0 * * *") // Ejecutar diariamente
    public void verificarPagosVencidos() {
        LocalDate fechaLimite = LocalDate.now().minusDays(30);
        List<Pago> pagosVencidos = pagoRepository.findByFechaBetween(
            fechaLimite, LocalDate.now());

        for (Pago pago : pagosVencidos) {
            if (pago.getEstado() == EstadoPago.PENDIENTE) {
                pago.setEstado(EstadoPago.VENCIDO);
                pagoRepository.save(pago);
                notificacionService.notificarPagoVencido(pago);
            }
        }
    }

    public List<Pago> obtenerPagosPorAlumno(Long alumnoId) {
        return pagoRepository.findByAlumnoId(alumnoId);
    }

    public List<Pago> obtenerPagosPorRangoFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        return pagoRepository.findByFechaBetween(fechaInicio, fechaFin);
    }
} 