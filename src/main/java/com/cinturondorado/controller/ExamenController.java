package com.cinturondorado.controller;

import com.cinturondorado.model.Alumno;
import com.cinturondorado.model.Examen;
import com.cinturondorado.model.enums.EstadoExamen;
import com.cinturondorado.model.enums.NivelCinturon;
import com.cinturondorado.service.ExamenService;
import com.cinturondorado.service.AlumnoService;
import com.cinturondorado.service.ProfesorService;
import com.cinturondorado.service.ClaseService;
import com.cinturondorado.dto.ExamenDTO;
import com.cinturondorado.exception.ResourceNotFoundException;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;

@Slf4j
@Controller
@RequestMapping("/examenes")
public class ExamenController {

    private final ExamenService examenService;
    private final AlumnoService alumnoService;
    private final ProfesorService profesorService;
    private final ClaseService claseService;

    public ExamenController(ExamenService examenService,
            AlumnoService alumnoService,
            ProfesorService profesorService,
            ClaseService claseService) {
        this.examenService = examenService;
        this.alumnoService = alumnoService;
        this.profesorService = profesorService;
        this.claseService = claseService;
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
    public String listarExamenes(
            @PageableDefault(size = 10, sort = "fecha", direction = Sort.Direction.DESC) Pageable pageableExamenes,
            Model model) {
        try {
            Pageable pageableAlumnos = PageRequest.of(0, Integer.MAX_VALUE);
            Page<Examen> paginaExamenes = examenService.listarTodosPaginados(pageableExamenes);

            // Obtener solo alumnos activos
            Page<Alumno> alumnosActivos = alumnoService.buscarPorActivo(true, pageableAlumnos);

            model.addAttribute("examenes", paginaExamenes.getContent());
            model.addAttribute("page", paginaExamenes);
            model.addAttribute("alumnos", alumnosActivos.getContent());
            model.addAttribute("profesores", profesorService.listarTodos());
            model.addAttribute("clases", claseService.listarClases()); // Add this line
            model.addAttribute("nivelesCinturon", NivelCinturon.values());

            return "examenes/lista";
        } catch (Exception e) {
            log.error("Error al listar exámenes: ", e);
            model.addAttribute("error", "Error al cargar los exámenes");
            return "error";
        }
    }

    @PostMapping
    public String registrarExamen(@ModelAttribute ExamenDTO examenDTO, RedirectAttributes redirectAttributes) {
        try {
            List<Examen> examenes = examenService.registrarExamenes(examenDTO);
            redirectAttributes.addFlashAttribute("mensaje",
                    "Se programaron " + examenes.size() + " exámenes exitosamente");
            return "redirect:/examenes";
        } catch (Exception e) {
            log.error("Error al registrar exámenes: ", e);
            redirectAttributes.addFlashAttribute("error",
                    "Error al registrar los exámenes: " + e.getMessage());
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

    @GetMapping("/{id}/evaluar")
    public String mostrarEvaluacionExamen(@PathVariable Long id, Model model) {
        Examen examen = examenService.obtenerPorId(id);
        if (examen == null) {
            throw new ResourceNotFoundException("Examen no encontrado");
        }

        // Convert Examen to ExamenDTO
        ExamenDTO evaluacion = new ExamenDTO();
        evaluacion.setAlumnoIds(List.of(examen.getAlumno().getId()));
        evaluacion.setFecha(examen.getFecha());
        evaluacion.setNivelActual(examen.getNivelActual());
        evaluacion.setNivelAspirante(examen.getNivelAspirante());
        evaluacion.setEstado(examen.getEstado());
        evaluacion.setObservaciones(examen.getObservaciones());

        model.addAttribute("evaluacion", evaluacion);
        return "examenes/modal-evaluacion :: modal";
    }

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

    @PostMapping("/{id}/observaciones")
    public String actualizarObservaciones(@PathVariable Long id,
            @RequestParam String observaciones,
            Model model) {
        try {
            examenService.actualizarObservaciones(id, observaciones);
            // Obtener el examen actualizado
            Examen examen = examenService.obtenerPorId(id);
            model.addAttribute("examen", examen);
            model.addAttribute("nivelesCinturon", NivelCinturon.values());
            // Devolver el contenido del modal actualizado
            return "examenes/modal-detalles :: modal";
        } catch (Exception e) {
            log.error("Error al actualizar observaciones: ", e);
            return "error :: mensaje";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarExamen(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            examenService.eliminarExamen(id);
            redirectAttributes.addFlashAttribute("mensaje", "Examen eliminado correctamente");
            return "redirect:/examenes";
        } catch (Exception e) {
            log.error("Error al eliminar examen: ", e);
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el examen: " + e.getMessage());
            return "redirect:/examenes";
        }
    }

    @GetMapping("/buscar")
    public String buscarExamenes(
            @RequestParam(required = false) NivelCinturon nivelAspirante,
            @RequestParam(required = false) EstadoExamen estado,
            @RequestParam(required = false) Long claseId, // Changed from String categoriaAlumno
            @RequestParam(required = false) Integer year,
            @PageableDefault(size = 10) Pageable pageable,
            Model model) {

        // Si se especifica un año, crear fechas de inicio y fin del año
        LocalDate fechaInicio = null;
        LocalDate fechaFin = null;
        if (year != null) {
            fechaInicio = LocalDate.of(year, 1, 1);
            fechaFin = LocalDate.of(year, 12, 31);
        }

        Page<Examen> paginaExamenes = examenService.buscarPorFiltros(
                nivelAspirante, 
                estado, 
                claseId,      // Changed from categoriaAlumno
                fechaInicio,
                fechaFin,
                pageable);

        model.addAttribute("examenes", paginaExamenes.getContent());
        model.addAttribute("page", paginaExamenes);
        model.addAttribute("nivelesCinturon", NivelCinturon.values());
        model.addAttribute("nivelAspiranteSeleccionado", nivelAspirante);
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("claseId", claseId); // Changed from categoriaSeleccionada
        model.addAttribute("yearSeleccionado", year);

        return "examenes/lista :: tablaExamenes";
    }

    @GetMapping("/fecha")
    public String obtenerExamenesPorFecha(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaDesdeExamenes,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaHastaExamenes,
            @PageableDefault(size = 10, sort = "id") Pageable pageable,
            Model model) {
        try {
            // If no dates provided, use current month
            if (fechaDesdeExamenes == null) {
                fechaDesdeExamenes = LocalDate.now().withDayOfMonth(1);
            }
            if (fechaHastaExamenes == null) {
                fechaHastaExamenes = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
            }

            Page<Examen> paginaExamenes = examenService.obtenerExamenesPorRangoFecha(fechaDesdeExamenes,
                    fechaHastaExamenes, pageable);

            model.addAttribute("examenes", paginaExamenes.getContent());
            model.addAttribute("page", paginaExamenes);
            model.addAttribute("fechaDesdeExamenes", fechaDesdeExamenes);
            model.addAttribute("fechaHastaExamenes", fechaHastaExamenes);

            return "examenes/lista :: tablaExamenes";
        } catch (Exception e) {
            log.error("Error al filtrar exámenes por fecha: {}. Fechas recibidas: {} - {}",
                    e.getMessage(), fechaDesdeExamenes, fechaHastaExamenes);
            model.addAttribute("error", "Error al filtrar exámenes: " + e.getMessage());
            model.addAttribute("examenes", new ArrayList<Examen>());
            model.addAttribute("page", Page.empty(pageable));
            return "examenes/lista :: tablaExamenes";
        }
    }
}
