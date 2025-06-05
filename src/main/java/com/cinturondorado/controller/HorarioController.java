package com.cinturondorado.controller;

import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.cinturondorado.model.CalendarioClase;
import com.cinturondorado.service.CalendarioClaseService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/horarios")
public class HorarioController {
    private final CalendarioClaseService horarioService;

    public HorarioController(CalendarioClaseService horarioService) {
        this.horarioService = horarioService;
    }

    @GetMapping
    public String listarHorarios(Model model) {
        try {
            List<CalendarioClase> horarios = horarioService.listarHorarios();
            model.addAttribute("horarios", horarios);
            return "horarios/lista";
        } catch (Exception e) {
            log.error("Error al listar horarios: ", e);
            model.addAttribute("error", "Error al cargar los horarios");
            return "error";
        }
    }

    @PostMapping("/guardar")
    public String guardarHorario(@ModelAttribute CalendarioClase horario, Model model) {
        try {
            validarFormatoHora(horario.getHora());
            validarDia(horario.getDia());
            horarioService.guardarHorario(horario);
            model.addAttribute("mensaje", "Horario guardado correctamente");
            return "redirect:/horarios";
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "horarios/lista";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarHorario(@PathVariable Long id, Model model) {
        try {
            horarioService.eliminarHorario(id);
            model.addAttribute("mensaje", "Horario eliminado correctamente");
            return "redirect:/horarios";
        } catch (Exception e) {
            model.addAttribute("error", "Error al eliminar el horario");
            return "horarios/lista";
        }
    }

    @GetMapping("/{id}/editar")
    public String editarHorario(@PathVariable Long id, Model model) {
        try {
            CalendarioClase horario = horarioService.obtenerHorario(id);
            model.addAttribute("horario", horario);
            return "horarios/editar";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el horario");
            return "error";
        }
    }

    private void validarFormatoHora(String hora) {
        if (!hora.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            throw new IllegalArgumentException("Formato de hora inválido");
        }
    }

    private void validarDia(String dia) {
        List<String> diasValidos = Arrays.asList("LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO");
        if (!diasValidos.contains(dia.toUpperCase())) {
            throw new IllegalArgumentException("Día inválido. Debe ser uno de: " + String.join(", ", diasValidos));
        }
    }
}