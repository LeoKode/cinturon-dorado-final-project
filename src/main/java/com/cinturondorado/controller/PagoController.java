package com.cinturondorado.controller;

import com.cinturondorado.dto.PagoDTO;
import com.cinturondorado.exception.ResourceNotFoundException;
import com.cinturondorado.model.Pago;
import com.cinturondorado.model.enums.EstadoPago;
import com.cinturondorado.service.PagoService;
import com.cinturondorado.service.AlumnoService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller
@RequestMapping("/pagos")
public class PagoController {
    private final PagoService pagoService;
    private final AlumnoService alumnoService;

    public PagoController(PagoService pagoService, AlumnoService alumnoService) {
        this.pagoService = pagoService;
        this.alumnoService = alumnoService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Add a custom editor for LocalDate
        binder.registerCustomEditor(LocalDate.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                try {
                    setValue(LocalDate.parse(text));
                } catch (Exception e) {
                    try {
                        // Try parsing with formatter as backup
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        setValue(LocalDate.parse(text, formatter));
                    } catch (Exception ex) {
                        throw new IllegalArgumentException("Invalid date format", ex);
                    }
                }
            }
        });
    }

    @GetMapping
    public String listarPagos(@PageableDefault(size = 10, sort = "id") Pageable pageable, Model model) {
        // Obtener el mes actual en formato YYYYMM
        LocalDate now = LocalDate.now();
        Integer yearMonth = now.getYear() * 100 + now.getMonthValue();

        Page<Pago> paginaPagos = pagoService.buscarPagos(null, null, yearMonth, pageable);

        model.addAttribute("pagos", paginaPagos.getContent());
        model.addAttribute("page", paginaPagos);
        model.addAttribute("alumnos", alumnoService.listarTodos(pageable));
        model.addAttribute("mesActual", now.format(DateTimeFormatter.ofPattern("yyyy-MM")));
        model.addAttribute("pagoDTO", new PagoDTO());

        return "pagos/lista";
    }

    @PostMapping("/registrar-pendiente")
    public String registrarPagoPendiente(@Valid @ModelAttribute PagoDTO pagoDTO,
            BindingResult result,
            Model model, Pageable pageable) {
        try {
            if (result.hasErrors()) {
                model.addAttribute("error", "Error en los datos del formulario");
                model.addAttribute("alumnos", alumnoService.listarTodos(pageable));
                return "pagos/lista :: tablaPagos";
            }

            // Asignar fecha actual si no viene
            if (pagoDTO.getFecha() == null) {
                pagoDTO.setFecha(LocalDate.now());
            }

            // Guardar el pago y verificar que se guardó correctamente
            Pago pagoGuardado = pagoService.registrarPagoPendiente(pagoDTO);
            if (pagoGuardado == null || pagoGuardado.getId() == null) {
                throw new RuntimeException("Error al guardar el pago");
            }

            // Actualizar modelo
            model.addAttribute("pagos", pagoService.obtenerTodosPagos(pageable));
            model.addAttribute("mensaje", "Pago registrado correctamente");
            model.addAttribute("alumnos", alumnoService.listarTodos(pageable));

            return "pagos/lista :: tablaPagos";
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar el pago: " + e.getMessage());
            model.addAttribute("alumnos", alumnoService.listarTodos(pageable));
            return "pagos/lista :: tablaPagos";
        }
    }

    @PostMapping("/{id}/registrar")
    public String registrarPago(@PathVariable Long id, Model model, 
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        try {
            // Obtener y actualizar el pago
            Optional<Pago> optionalPago = pagoService.obtenerPagoPorId(id);
            if (optionalPago.isPresent()) {
                Pago pago = optionalPago.get();
                pago.setEstado(EstadoPago.PAGADO);
                pago.setPagado(true);
                pagoService.actualizarPago(pago);
            }
    
            // Obtener la lista actualizada de pagos
            Page<Pago> paginaPagos = pagoService.obtenerTodosPagos(pageable);
            
            // Actualizar el modelo
            model.addAttribute("pagos", paginaPagos.getContent());
            model.addAttribute("page", paginaPagos);
            
            return "pagos/lista :: tablaPagos";
        } catch (Exception e) {
            // Log del error y retornar error
            return "error :: mensaje";
        }
    }

    @GetMapping({ "/alumno/{alumnoId}", "/todos", "/estado/{estado}" })
    public String obtenerPagosPorFiltros(
            @PathVariable(required = false) Long alumnoId,
            @PathVariable(required = false) EstadoPago estado,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaHasta,
            @PageableDefault(size = 10, sort = "id") Pageable pageable,
            Model model) {

        Page<Pago> paginaPagos;
        if (alumnoId != null) {
            paginaPagos = pagoService.obtenerPagosPorAlumno(alumnoId, pageable);
        } else {
            paginaPagos = pagoService.buscarPagos(estado, nombre, null, pageable);
        }

        model.addAttribute("pagos", paginaPagos.getContent());
        model.addAttribute("page", paginaPagos);
        model.addAttribute("fechaDesde", fechaDesde != null ? fechaDesde : LocalDate.now().withDayOfMonth(1));
        model.addAttribute("fechaHasta", fechaHasta != null ? fechaHasta : LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()));
        model.addAttribute("totalElementos", paginaPagos.getTotalElements());

        return "pagos/lista :: tablaPagos";
    }

    @GetMapping("/buscar")
    public String buscarPagos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) EstadoPago estado,
            @RequestParam(required = false) String mesPago,
            @PageableDefault(size = 10, sort = "id") Pageable pageable,
            Model model) {

        // Convertir mesPago (yyyy-MM) a formato YYYYMM
        Integer yearMonth = null;
        if (mesPago != null && !mesPago.isEmpty()) {
            LocalDate fecha = YearMonth.parse(mesPago).atDay(1);
            yearMonth = fecha.getYear() * 100 + fecha.getMonthValue();
        }

        Page<Pago> paginaPagos = pagoService.buscarPagos(estado, nombre, yearMonth, pageable);

        model.addAttribute("pagos", paginaPagos.getContent());
        model.addAttribute("page", paginaPagos);
        model.addAttribute("pagoDTO", new PagoDTO());

        return "pagos/lista :: tablaPagos";
    }

    @GetMapping("/{id}/detalles")
    public String verDetallesPago(@PathVariable Long id, Model model) {
        try {
            Pago pago = pagoService.obtenerPagoPorId(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado"));

            model.addAttribute("pago", pago);
            return "pagos/modal-detalles :: modal";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar detalles del pago: " + e.getMessage());
            return "pagos/modal-detalles :: modal";
        }
    }

    @PostMapping("/{id}/cancelar")
    public String cancelarPago(@PathVariable Long id, Model model, Pageable pageable) {
        try {
            Pago pago = pagoService.obtenerPagoPorId(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado"));

            if (pago.getEstado() != EstadoPago.PENDIENTE) {
                throw new RuntimeException("Solo se pueden cancelar pagos pendientes");
            }

            pago.setEstado(EstadoPago.CANCELADO);
            pagoService.actualizarPago(pago);

            model.addAttribute("pagos", pagoService.obtenerTodosPagos(pageable));
            return "pagos/lista :: tablaPagos";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cancelar el pago: " + e.getMessage());
            return "pagos/lista :: tablaPagos";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarPago(@PathVariable Long id, Model model, Pageable pageable) {
        try {
            Pago pago = pagoService.obtenerPagoPorId(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado"));

            pagoService.eliminarPago(pago.getId());

            model.addAttribute("pagos", pagoService.obtenerTodosPagos(pageable));
            return "pagos/lista :: tablaPagos";
        } catch (Exception e) {
            model.addAttribute("error", "Error al eliminar el pago: " + e.getMessage());
            return "pagos/lista :: tablaPagos";
        }
    }

    @GetMapping("/reporte")
    public String obtenerReportePagos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Model model, Pageable pageable) {
        Page<Pago> paginaPagos = pagoService.buscarPagos(null, null, null, pageable);
        model.addAttribute("pagos", paginaPagos);
        return "pagos/lista :: tablaPagos";
    }
}