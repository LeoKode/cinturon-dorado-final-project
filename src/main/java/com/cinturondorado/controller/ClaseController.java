package com.cinturondorado.controller;

import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.cinturondorado.exception.ResourceNotFoundException;
import com.cinturondorado.model.Alumno;
import com.cinturondorado.model.Clase;
import com.cinturondorado.model.CalendarioClase;
import com.cinturondorado.service.ClaseService;
import com.cinturondorado.service.AlumnoService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/clases")
public class ClaseController {
    private final ClaseService claseservice;
    private final AlumnoService alumnoService;

    public ClaseController(ClaseService claseservice, AlumnoService alumnoService) {
        this.claseservice = claseservice;
        this.alumnoService = alumnoService;
    }

    @GetMapping
    public String listarClases(Model model) {
        try {
            List<Clase> clases = claseservice.listarClases();
            List<CalendarioClase> horarios = claseservice.listarHorarios();
            model.addAttribute("clases", clases);
            model.addAttribute("horarios", horarios);
            return "clases/lista";
        } catch (Exception e) {
            log.error("Error al cargar las clases: ", e);
            model.addAttribute("error", "Error al cargar las clases");
            return "error";
        }
    }

    @PostMapping("/guardar")
    public String guardarClase(@ModelAttribute Clase clase, Model model) {
        try {
            claseservice.guardarClase(clase);
            actualizarModeloClases(model, "Clase guardada correctamente");
            return "clases/lista :: #clasesContainer";
        } catch (IllegalStateException e) {
            actualizarModeloClases(model, null, e.getMessage());
            return "clases/lista :: #clasesContainer";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarClase(@PathVariable Long id, Model model) {
        try {
            claseservice.eliminarClase(id);
            actualizarModeloClases(model, "Clase eliminada correctamente");
            return "clases/lista :: tablaClases";
        } catch (IllegalStateException e) {
            actualizarModeloClases(model, null, e.getMessage());
            return "clases/lista :: tablaClases";
        }
    }

    @PostMapping("/horario/agregar")
    public String agregarHorario(@RequestParam String hora, Model model) {
        try {
            validarFormatoHora(hora);
            // Agregar el horario para todos los días disponibles
            List<String> dias = Arrays.asList("LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO");
            for (String dia : dias) {
                claseservice.agregarHorario(hora, dia);
            }
            actualizarModeloClases(model, "Horario agregado correctamente");
            return "clases/lista :: tablaClases";
        } catch (Exception e) {
            actualizarModeloClases(model, null, e.getMessage());
            return "clases/lista :: tablaClases";
        }
    }

    @PostMapping("/asignar")
    public String asignarHorarioClase(
            @RequestParam Long claseId,
            @RequestParam String dia,
            @RequestParam String hora,
            Model model) {
        try {
            claseservice.asignarHorario(claseId, dia, hora);
            actualizarModeloClases(model);
            return "clases/lista :: tablaClases";
        } catch (IllegalStateException e) {
            actualizarModeloClases(model, null, e.getMessage());
            return "clases/lista :: tablaClases";
        }
    }

    @PostMapping("/{id}/desasignar")
    public String desasignarHorario(
            @PathVariable Long id,
            @RequestParam String dia,
            @RequestParam String hora,
            Model model) {
        try {
            claseservice.desasignarHorario(id, dia, hora);
            actualizarModeloClases(model);
            return "clases/lista :: tablaClases";
        } catch (IllegalStateException e) {
            actualizarModeloClases(model, null, e.getMessage());
            return "clases/lista :: tablaClases";
        }
    }

    @GetMapping("/editar/{id}")
    public String editarClase(@PathVariable Long id, Model model) {
        try {
            Clase clase = claseservice.obtenerClase(id);
            if (clase == null) {
                throw new ResourceNotFoundException("Clase no encontrada");
            }
            model.addAttribute("clase", clase);
            return "clases/modal-editar-clase :: modal";
        } catch (Exception e) {
            log.error("Error al cargar la clase para editar: ", e);
            model.addAttribute("error", "Error al cargar la clase: " + e.getMessage());
            return "error";
        }
    }

    // Add these new endpoints to handle student management in classes
    @GetMapping("/{id}/gestionar")
    public String gestionarAlumnos(@PathVariable Long id, Model model) {
        try {
            Clase clase = claseservice.obtenerClase(id);
            List<Alumno> alumnosDisponibles = alumnoService.obtenerAlumnosNoInscritosEnClase(id);
            
            model.addAttribute("clase", clase);
            model.addAttribute("alumnosDisponibles", alumnosDisponibles);
            return "clases/modal-gestionar-alumnos :: modal";
        } catch (Exception e) {
            log.error("Error al cargar la gestión de alumnos: ", e);
            model.addAttribute("error", "Error al cargar la gestión de alumnos: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/{claseId}/inscribir")
    public String inscribirAlumno(
            @PathVariable Long claseId,
            @RequestParam Long alumnoId,
            Model model) {
        try {
            claseservice.inscribirAlumno(claseId, alumnoId);
            Clase clase = claseservice.obtenerClase(claseId);
            List<Alumno> alumnosDisponibles = alumnoService.obtenerAlumnosNoInscritosEnClase(claseId);
            
            model.addAttribute("clase", clase);
            model.addAttribute("alumnosDisponibles", alumnosDisponibles);
            model.addAttribute("mensaje", "Alumno inscrito correctamente");
            return "clases/modal-gestionar-alumnos :: modal";
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "clases/modal-gestionar-alumnos :: modal";
        }
    }

    @PostMapping("/{claseId}/desinscribir/{alumnoId}")
    public String desinscribirAlumno(
            @PathVariable Long claseId,
            @PathVariable Long alumnoId,
            Model model) {
        try {
            claseservice.desinscribirAlumno(claseId, alumnoId);
            Clase clase = claseservice.obtenerClase(claseId);
            List<Alumno> alumnosDisponibles = alumnoService.obtenerAlumnosNoInscritosEnClase(claseId);
            
            model.addAttribute("clase", clase);
            model.addAttribute("alumnosDisponibles", alumnosDisponibles);
            model.addAttribute("mensaje", "Alumno desinscrito correctamente");
            return "clases/modal-gestionar-alumnos :: modal";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "clases/modal-gestionar-alumnos :: modal";
        }
    }

    private void validarFormatoHora(String hora) {
        if (!hora.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            throw new IllegalArgumentException("Formato de hora inválido");
        }
    }

    private void actualizarModeloClases(Model model, String mensaje, String error) {
        model.addAttribute("clases", claseservice.listarClases());
        model.addAttribute("horarios", claseservice.listarHorarios());
        if (mensaje != null) {
            model.addAttribute("mensaje", mensaje);
        }
        if (error != null) {
            model.addAttribute("error", error);
        }
    }

    private void actualizarModeloClases(Model model) {
        actualizarModeloClases(model, null, null);
    }

    private void actualizarModeloClases(Model model, String mensaje) {
        actualizarModeloClases(model, mensaje, null);
    }
}