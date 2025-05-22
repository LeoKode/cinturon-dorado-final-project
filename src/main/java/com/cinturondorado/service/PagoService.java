package com.cinturondorado.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
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
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@Transactional
public class PagoService {
    private final PagoRepository pagoRepository;
    private final AlumnoRepository alumnoRepository;
    private final NotificacionService notificacionService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public PagoService(PagoRepository pagoRepository,
            AlumnoRepository alumnoRepository,
            NotificacionService notificacionService) {
        this.pagoRepository = pagoRepository;
        this.alumnoRepository = alumnoRepository;
        this.notificacionService = notificacionService;
    }

    public Pago registrarPagoPendiente(PagoDTO pagoDTO) {
        try {
            Alumno alumno = alumnoRepository.findById(pagoDTO.getAlumnoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado"));

            Pago pago = new Pago();
            pago.setAlumno(alumno);
            pago.setMonto(pagoDTO.getMonto());
            // Si la fecha es null, usar la fecha actual
            pago.setFecha(pagoDTO.getFecha() != null ? pagoDTO.getFecha() : LocalDate.now());
            pago.setConcepto(pagoDTO.getConcepto());
            pago.setEstado(EstadoPago.PENDIENTE);
            pago.setPagado(false);

            // Update the student's last payment date
            alumno.setUltimoPago(pago.getFecha());
            alumnoRepository.save(alumno);

            // Guardar y retornar
            Pago pagoGuardado = pagoRepository.save(pago);
            pagoRepository.flush(); // Forzar la escritura en la base de datos

            return pagoGuardado;
        } catch (Exception e) {
            throw new RuntimeException("Error al registrar el pago: " + e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 0 * * *") // Ejecutar diariamente
    public void verificarPagosVencidos(Pageable pageable) {
        LocalDate fechaLimite = LocalDate.now().minusDays(30);
        Page<Pago> pagosVencidos = pagoRepository.findByFechaBetween(
                fechaLimite, LocalDate.now(), pageable);

        for (Pago pago : pagosVencidos) {
            if (pago.getEstado() == EstadoPago.PENDIENTE) {
                pago.setEstado(EstadoPago.VENCIDO);
                pagoRepository.save(pago);
                notificacionService.notificarPagoVencido(pago);
            }
        }
    }

    public Page<Pago> obtenerTodosPagos(Pageable pageable) {
        return pagoRepository.findAll(pageable);
    }

    public Optional<Pago> obtenerPagoPorId(Long id) {
        return pagoRepository.findById(id);
    }

    public Page<Pago> obtenerPagosPorAlumno(Long alumnoId, Pageable pageable) {
        return pagoRepository.findByAlumnoId(alumnoId, pageable);
    }

    public Page<Pago> obtenerPagosPorEstado(EstadoPago estado, Pageable pageable) {
        return pagoRepository.findByEstado(estado, pageable);
    }

    public Page<Pago> buscarPagos(EstadoPago estado, String nombre, Integer yearMonth, Pageable pageable) {
        if (yearMonth == null) {
            LocalDate now = LocalDate.now();
            yearMonth = now.getYear() * 100 + now.getMonthValue();
        }

        // Solo crear pagos pendientes si estamos consultando el mes actual
        LocalDate now = LocalDate.now();
        int mesActual = now.getYear() * 100 + now.getMonthValue();

        if (yearMonth == mesActual) {
            // Verificar si ya existen pagos para este mes antes de crearlos
            boolean existenPagos = jdbcTemplate.queryForObject(
                    "SELECT EXISTS(SELECT 1 FROM pagos WHERE EXTRACT(YEAR FROM fecha) * 100 + EXTRACT(MONTH FROM fecha) = ?)",
                    Boolean.class,
                    yearMonth);

            if (!existenPagos) {
                crearPagosPendientesMes(yearMonth);
            }
        }

        return pagoRepository.findByFiltersAndYearMonth(
                estado != null ? estado.name() : null,
                nombre,
                yearMonth,
                pageable);
    }

    private void crearPagosPendientesMes(Integer yearMonth) {
        // Obtener todos los alumnos activos con paginación
        Pageable pageable = Pageable.unpaged(); // Para obtener todos los resultados
        Page<Alumno> alumnosPage = alumnoRepository.findByActivo(true, pageable);
        List<Alumno> alumnos = alumnosPage.getContent();
        
        LocalDate fechaPago = LocalDate.of(yearMonth / 100, yearMonth % 100, 1);

        for (Alumno alumno : alumnos) {
            // Verificar si ya existe un pago para este alumno en este mes
            if (!pagoRepository.existsByAlumnoIdAndFechaYearMonth(alumno.getId(), yearMonth)) {
                // Crear pago pendiente
                Pago pago = new Pago();
                pago.setAlumno(alumno);
                pago.setFecha(fechaPago);
                pago.setEstado(EstadoPago.PENDIENTE);
                pago.setMonto(alumno.getCuotaMensual());
                pago.setPagado(false);

                // Agregar el concepto por defecto usando el mes y año
                String mes = fechaPago.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
                String concepto = String.format("Cuota mensual - %s %d",
                        mes.substring(0, 1).toUpperCase() + mes.substring(1),
                        fechaPago.getYear());
                pago.setConcepto(concepto);

                pagoRepository.save(pago);
            }
        }
    }

    // También podemos agregar un método programado para crear pagos al inicio de
    // cada mes
    @Scheduled(cron = "0 0 1 1 * *") // Ejecutar el primer día de cada mes a las 00:00
    public void crearPagosMensualesAutomaticos() {
        LocalDate now = LocalDate.now();
        Integer yearMonth = now.getYear() * 100 + now.getMonthValue();
        crearPagosPendientesMes(yearMonth);
    }

    public Page<Pago> buscarPagosPorNombreAlumno(String nombre, Pageable pageable) {
        return pagoRepository.findByAlumnoNombreContainingIgnoreCase(nombre, pageable);
    }

    public Page<Pago> buscarPagosPorEstadoYNombre(EstadoPago estado, String nombre, Pageable pageable) {
        return pagoRepository.findByEstadoAndAlumnoNombreContainingIgnoreCase(estado, nombre, pageable);
    }

    public Pago actualizarPago(Pago pago) {
        return pagoRepository.save(pago);
    }

    public void eliminarPago(Long id) {
        pagoRepository.deleteById(id);
    }

    public long contarPagosPendientes() {
        return pagoRepository.countByEstado(EstadoPago.PENDIENTE);
    }

}
