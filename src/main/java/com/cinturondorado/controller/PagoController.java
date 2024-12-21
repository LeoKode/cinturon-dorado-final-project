package com.cinturondorado.controller;

import com.cinturondorado.dto.PagoDTO;
import com.cinturondorado.model.Pago;
import com.cinturondorado.service.PagoService;
import com.cinturondorado.service.AlumnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/pagos")
public class PagoController {
    private final PagoService pagoService;
    private final AlumnoService alumnoService;
    
    @Autowired
    public PagoController(PagoService pagoService, AlumnoService alumnoService) {
        this.pagoService = pagoService;
        this.alumnoService = alumnoService;
    }
    
    @GetMapping
    public String listarPagos(Model model) {
        model.addAttribute("pagos", pagoService.obtenerTodosPagos());
        model.addAttribute("alumnos", alumnoService.listarTodos());
        model.addAttribute("pagoDTO", new PagoDTO());
        return "pagos/lista";
    }
    
    @PostMapping
    public String registrarPago(@Valid @ModelAttribute("pagoDTO") PagoDTO pagoDTO,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "pagos/lista";
        }
        
        try {
            pagoService.registrarPago(pagoDTO);
            redirectAttributes.addFlashAttribute("mensaje", "Pago registrado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar el pago: " + e.getMessage());
        }
        
        return "redirect:/pagos";
    }
    
    @GetMapping("/alumno/{alumnoId}")
    public String obtenerPagosPorAlumno(@PathVariable Long alumnoId, Model model) {
        List<Pago> pagos = pagoService.obtenerPagosPorAlumno(alumnoId);
        model.addAttribute("pagos", pagos);
        return "pagos/lista :: tablaPagos";
    }
    
    @GetMapping("/reporte")
    public String obtenerReportePagos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Model model) {
        List<Pago> pagos = pagoService.obtenerPagosPorRangoFecha(fechaInicio, fechaFin);
        model.addAttribute("pagos", pagos);
        return "pagos/lista :: tablaPagos";
    }
} 