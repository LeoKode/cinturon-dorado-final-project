package com.cinturondorado.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cinturondorado.dto.AlumnoDTO;
import com.cinturondorado.model.Alumno;
import com.cinturondorado.model.enums.NivelCinturon;
import com.cinturondorado.service.AlumnoService;
import com.cinturondorado.service.ClaseService;

import javax.validation.Valid;

import java.time.LocalDate;

@Slf4j
@Controller
@RequestMapping("/alumnos")
public class AlumnoController {

    private final AlumnoService alumnoService;
    private final ClaseService claseService;

    public AlumnoController(AlumnoService alumnoService, ClaseService claseService) {
        this.alumnoService = alumnoService;
        this.claseService = claseService;
    }

    @GetMapping
    public String listarAlumnos(
            @RequestParam(required = false) NivelCinturon nivelCinturon,
            @PageableDefault(size = 10, sort = "id") Pageable pageable,
            Model model) {

        // Dejar que Spring Data JPA maneje la paginación
        Page<Alumno> paginaAlumnos = nivelCinturon != null
                ? alumnoService.buscarPorNivelCinturon(nivelCinturon, pageable)
                : alumnoService.listarTodos(pageable);

        // Inicializar DTO para el formulario de nuevo alumno
        AlumnoDTO alumnoDTO = new AlumnoDTO();
        alumnoDTO.setFechaNacimiento(LocalDate.of(2005, 1, 1));

        // Agregar atributos al modelo
        model.addAttribute("alumnos", paginaAlumnos.getContent());
        model.addAttribute("clases", claseService.listarClases());
        model.addAttribute("page", paginaAlumnos);
        model.addAttribute("nivelesCinturon", NivelCinturon.values());
        model.addAttribute("alumno", alumnoDTO);

        // La vista manejará la paginación usando los datos del objeto Page
        return "alumnos/lista";
    }

    @PostMapping
    public String guardarAlumno(@Valid @ModelAttribute AlumnoDTO alumnoDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model, Pageable pageable) {
        if (result.hasErrors()) {
            model.addAttribute("alumnos", alumnoService.listarTodos(pageable));
            model.addAttribute("nivelesCinturon", NivelCinturon.values());
            return "alumnos/lista :: modal";
        }

        if (alumnoDTO.getId() != null) {
            alumnoService.actualizarAlumno(alumnoDTO);
            redirectAttributes.addFlashAttribute("mensaje", "Alumno actualizado exitosamente");
        } else {
            alumnoService.registrarAlumno(alumnoDTO);
            redirectAttributes.addFlashAttribute("mensaje", "Alumno guardado exitosamente");
        }

        return "redirect:/alumnos";
    }

    @GetMapping("/{id}")
    public String editarAlumno(@PathVariable Long id, Model model) {
        Alumno alumno = alumnoService.obtenerPorId(id);
        AlumnoDTO alumnoDTO = new AlumnoDTO(alumno);
        model.addAttribute("alumno", alumnoDTO);
        model.addAttribute("clases", claseService.listarClases());
        model.addAttribute("nivelesCinturon", NivelCinturon.values());
        return "alumnos/modal-alumno :: modal";
    }

    @PostMapping("/{id}/nivel-cinturon")
    public String actualizarNivelCinturon(@PathVariable Long id,
            @RequestParam NivelCinturon nivelCinturon,
            RedirectAttributes redirectAttributes) {
        alumnoService.actualizarNivelCinturon(id, nivelCinturon);
        redirectAttributes.addFlashAttribute("mensaje", "Nivel de cinturón actualizado");
        return "redirect:/alumnos";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarAlumno(@PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        alumnoService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Alumno eliminado exitosamente");
        return "redirect:/alumnos";
    }

    @PostMapping("/{id}/toggle-activo")
    public String toggleActivo(@PathVariable Long id, Model model,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        boolean nuevoEstado = alumnoService.toggleActivo(id);

        // Obtener la lista actualizada de alumnos
        Page<Alumno> paginaAlumnos = alumnoService.listarTodos(pageable);

        // Actualizar el modelo
        model.addAttribute("alumnos", paginaAlumnos.getContent());
        model.addAttribute("page", paginaAlumnos);
        model.addAttribute("mensaje",
                nuevoEstado ? "Alumno activado correctamente" : "Alumno desactivado correctamente");
        model.addAttribute("nivelesCinturon", NivelCinturon.values());

        // Retornar el fragmento de la tabla actualizada
        return "alumnos/lista :: tablaAlumnos";
    }

    @GetMapping("/buscar")
    public String buscarAlumnos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long claseId,
            @RequestParam(required = false) NivelCinturon nivelCinturon,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            Model model) {

        try {
            Pageable pageable = PageRequest.of(page, size);

            Page<Alumno> paginaAlumnos = buscarAlumnosFiltrados(
                    nombre, claseId, nivelCinturon, pageable);

            // Add required model attributes
            model.addAttribute("alumnos", paginaAlumnos.getContent());
            model.addAttribute("page", paginaAlumnos);
            model.addAttribute("nivelesCinturon", NivelCinturon.values());
            model.addAttribute("clases", claseService.listarClases()); // Agregar lista de clases

            // Add empty AlumnoDTO for form binding
            AlumnoDTO alumnoDTO = new AlumnoDTO();
            alumnoDTO.setFechaNacimiento(LocalDate.of(2005, 1, 1));
            model.addAttribute("alumno", alumnoDTO);

            return "alumnos/lista :: tablaAlumnos";
        } catch (Exception e) {
            log.error("Error en búsqueda de alumnos: {}", e.getMessage());
            model.addAttribute("error", "Error al realizar la búsqueda");
            model.addAttribute("alumno", new AlumnoDTO());
            return "fragments/error :: message";
        }
    }

    // Helper method to handle search logic
    private Page<Alumno> buscarAlumnosFiltrados(
            String nombre,
            Long claseId,
            NivelCinturon nivel,
            Pageable pageable) {

        if (nombre != null && !nombre.isEmpty()) {
            if (claseId != null) {
                return alumnoService.buscarPorNombreYClase(nombre, claseId, pageable);
            } else if (nivel != null) {
                return alumnoService.buscarPorNombreYNivel(nombre, nivel, pageable);
            }
            return alumnoService.buscarPorNombre(nombre, pageable);
        } else if (claseId != null) {
            if (nivel != null) {
                return alumnoService.buscarPorNivelYClase(nivel, claseId, pageable);
            }
            return alumnoService.buscarPorClaseId(claseId, pageable);
        } else if (nivel != null) {
            return alumnoService.buscarPorNivelCinturon(nivel, pageable);
        }
        return alumnoService.listarTodos(pageable);
    }
}