package com.cinturondorado.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cinturondorado.dto.AlumnoDTO;
import com.cinturondorado.model.Alumno;
import com.cinturondorado.model.enums.NivelCinturon;
import com.cinturondorado.service.AlumnoService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/alumnos")
public class AlumnoController {
    
    private final AlumnoService alumnoService;
    
    @Autowired
    public AlumnoController(AlumnoService alumnoService) {
        this.alumnoService = alumnoService;
    }
    
    @GetMapping
    public String listarAlumnos(Model model) {
        try {
            List<Alumno> alumnos = alumnoService.listarTodos();
            model.addAttribute("alumnos", alumnos);
            model.addAttribute("nivelesCinturon", NivelCinturon.values());
            model.addAttribute("alumno", new Alumno()); // Agregar un objeto Alumno vacío
            model.addAttribute("content", "alumnos/lista :: content"); // Asegúrate de que esto esté correcto
            return "alumnos/lista";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar los alumnos: " + e.getMessage());
            return "error";
        }
    }
    
    @PostMapping
    public String guardarAlumno(@Valid @ModelAttribute("alumnoDTO") AlumnoDTO alumnoDTO, 
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("alumnos", alumnoService.listarTodos());
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
        model.addAttribute("alumno", alumno);
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
    
    @GetMapping("/buscar")
    public String buscarAlumnos(@RequestParam(required = false) String nombre,
                               Model model) {
        List<Alumno> alumnos;
        if (nombre != null && !nombre.isEmpty()) {
            alumnos = alumnoService.buscarPorNombre(nombre);
        } else {
            alumnos = alumnoService.listarTodos();
        }
        model.addAttribute("alumnos", alumnos);
        return "alumnos/lista :: tablaAlumnos";
    }
} 