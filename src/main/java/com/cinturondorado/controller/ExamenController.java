package com.cinturondorado.controller;

import com.cinturondorado.model.Examen;
import com.cinturondorado.model.enums.EstadoExamen;
import com.cinturondorado.model.enums.NivelCinturon;
import com.cinturondorado.service.ExamenService;
import com.cinturondorado.service.AlumnoService;
import com.cinturondorado.service.ProfesorService;
import com.cinturondorado.dto.ExamenDTO;
import com.cinturondorado.exception.ResourceNotFoundException;

import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/examenes")
public class ExamenController {

    private final ExamenService examenService;
    private final AlumnoService alumnoService;
    private final ProfesorService profesorService;

    public ExamenController(ExamenService examenService,
            AlumnoService alumnoService,
            ProfesorService profesorService) {
        this.examenService = examenService;
        this.alumnoService = alumnoService;
        this.profesorService = profesorService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        binder.registerCustomEditor(LocalDateTime.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                setValue(text != null ? LocalDateTime.parse(text, formatter) : null);
            }
        });
    }

    @GetMapping
    public String listarExamenes(Model model) {
        try {
            List<Examen> examenes = examenService.listarTodos();
            // Filter out null examenes
            examenes = examenes.stream()
                    .filter(java.util.Objects::nonNull)
                    .collect(java.util.stream.Collectors.toList());

            model.addAttribute("examenes", examenes);
            model.addAttribute("alumnos", alumnoService.listarTodos());
            model.addAttribute("profesores", profesorService.listarTodos());
            model.addAttribute("nivelesCinturon", NivelCinturon.values());
            model.addAttribute("pageTitle", "Gestión de Exámenes - Cinturón Dorado");

            return "examenes/lista";

        } catch (Exception e) {
            // Log the error
            log.error("Error al cargar los exámenes: ", e);
            // Add error message to model instead of redirecting
            model.addAttribute("error", "Error al cargar los exámenes: " + e.getMessage());
            // Return to lista view with error message
            return "examenes/lista";
        }
    }

    @PostMapping
    public String registrarExamen(@ModelAttribute ExamenDTO examenDTO, RedirectAttributes redirectAttributes) {
        try {
            examenService.registrarExamen(examenDTO);
            redirectAttributes.addFlashAttribute("mensaje", "Examen registrado exitosamente");
            return "redirect:/examenes";
        } catch (Exception e) {
            log.error("Error al registrar examen: ", e);
            redirectAttributes.addFlashAttribute("error", "Error al registrar el examen: " + e.getMessage());
            return "redirect:/examenes";
        }
    }

    @PostMapping("/{id}/evaluar")
    public String guardarResultadoEvaluacion(@PathVariable Long id, @ModelAttribute ExamenDTO evaluacionDTO,
            RedirectAttributes redirectAttributes) {
        try {
            examenService.evaluarExamen(id, evaluacionDTO);
            redirectAttributes.addFlashAttribute("mensaje", "Evaluación guardada exitosamente");
        } catch (Exception e) {
            log.error("Error al guardar evaluación: ", e);
            redirectAttributes.addFlashAttribute("error", "Error al guardar la evaluación: " + e.getMessage());
        }
        return "redirect:/examenes";
    }

    @GetMapping("/buscar")
    public String buscarExamenes(
            @RequestParam(required = false) NivelCinturon nivelActual,
            @RequestParam(required = false) NivelCinturon nivelAspirante,
            Model model) {

        // Create new ExamenDTO with initialized values
        ExamenDTO evaluacion = new ExamenDTO();
        evaluacion.setId(null); // Explicitly set id to null for new evaluations
        evaluacion.setNivelActual(nivelActual);
        evaluacion.setNivelAspirante(nivelAspirante);
        model.addAttribute("evaluacion", evaluacion);

        // Add other required attributes
        model.addAttribute("examenes", examenService.buscarPorFiltros(nivelActual, nivelAspirante));
        model.addAttribute("nivelesCinturon", NivelCinturon.values());
        model.addAttribute("alumnos", alumnoService.listarTodos());
        model.addAttribute("profesores", profesorService.listarTodos());

        return "examenes/lista :: tablaExamenes";
    }

    @GetMapping("/{id}/evaluar")
    public String mostrarEvaluacionExamen(@PathVariable Long id, Model model) {
        Examen examen = examenService.obtenerPorId(id);
        if (examen == null) {
            throw new ResourceNotFoundException("Examen no encontrado");
        }

        // Convert Examen to ExamenDTO
        ExamenDTO evaluacion = new ExamenDTO();
        evaluacion.setId(examen.getId());
        evaluacion.setAlumno(examen.getAlumno());
        evaluacion.setFecha(examen.getFecha());
        evaluacion.setNivelActual(examen.getNivelActual());
        evaluacion.setNivelAspirante(examen.getNivelAspirante());
        evaluacion.setEstado(examen.getEstado());
        evaluacion.setObservaciones(examen.getObservaciones());

        model.addAttribute("evaluacion", evaluacion);
        return "examenes/modal-evaluacion :: modal";
    }

    // En ExamenController.java
    @GetMapping("/{id}")
    public String verDetallesExamen(@PathVariable Long id, Model model) {
        try {
            Examen examen = examenService.obtenerPorId(id);
            if (examen == null) {
                throw new ResourceNotFoundException("Examen no encontrado");
            }
            log.info("Estado del examen: " + examen.getEstado()); // Verifica el estado
            model.addAttribute("examen", examen); // This is crucial
            model.addAttribute("nivelesCinturon", NivelCinturon.values());
            return "examenes/modal-detalles :: modal";
        } catch (Exception e) {
            log.error("Error en verDetallesExamen: ", e);
            return "error :: mensaje";
        }
    }
}
