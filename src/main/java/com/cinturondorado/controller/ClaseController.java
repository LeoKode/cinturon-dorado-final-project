package com.cinturondorado.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cinturondorado.model.Clase;
import com.cinturondorado.model.HorarioDisponible;
import com.cinturondorado.service.ClaseService;
import com.cinturondorado.service.HorarioDisponibleService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/clases")
public class ClaseController {
    private final ClaseService claseService;
    private final HorarioDisponibleService horarioService;

    public ClaseController(ClaseService claseService, HorarioDisponibleService horarioService) {
        this.claseService = claseService;
        this.horarioService = horarioService;
    }

    @GetMapping
    public String listarClases(Model model) {
        try {
            List<Clase> clases = claseService.listarTodasClases();
            List<HorarioDisponible> horarios = horarioService.listarHorariosActivos();
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
            if (clase.getId() != null) {
                if (!claseService.esHorarioDisponibleParaClase(clase.getId(), clase.getDia(), clase.getHoraInicio())) {
                    model.addAttribute("error", "Ya existe una clase programada para " +
                            clase.getDia() + " a las " + clase.getHoraInicio());
                    model.addAttribute("clases", claseService.listarTodasClases());
                    model.addAttribute("horarios", horarioService.listarHorariosActivos());
                    return "clases/lista :: tablaClases";
                }
            } else if (claseService.existeClaseEnHorario(clase.getDia(), clase.getHoraInicio())) {
                model.addAttribute("error", "Ya existe una clase programada para " +
                        clase.getDia() + " a las " + clase.getHoraInicio());
                model.addAttribute("clases", claseService.listarTodasClases());
                model.addAttribute("horarios", horarioService.listarHorariosActivos());
                return "clases/lista :: tablaClases";
            }

            claseService.guardarClase(clase);
            model.addAttribute("clases", claseService.listarTodasClases());
            model.addAttribute("horarios", horarioService.listarHorariosActivos());
            model.addAttribute("mensaje", "Clase guardada correctamente");
            return "clases/lista :: tablaClases";
        } catch (Exception e) {
            log.error("Error al guardar la clase: ", e);
            model.addAttribute("error", "Error al guardar la clase");
            model.addAttribute("clases", claseService.listarTodasClases());
            model.addAttribute("horarios", horarioService.listarHorariosActivos());
            return "clases/lista :: tablaClases";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarClase(@PathVariable Long id, Model model) {
        try {
            claseService.eliminarClase(id);
            model.addAttribute("clases", claseService.listarTodasClases());
            model.addAttribute("horarios", horarioService.listarHorariosActivos());
            model.addAttribute("mensaje", "Clase eliminada correctamente");
            return "clases/lista :: tablaClases";
        } catch (Exception e) {
            log.error("Error al eliminar la clase: ", e);
            model.addAttribute("error", "Error al eliminar la clase");
            model.addAttribute("clases", claseService.listarTodasClases());
            model.addAttribute("horarios", horarioService.listarHorariosActivos());
            return "clases/lista :: tablaClases";
        }
    }

    @PostMapping("/horario/agregar")
    public String agregarHorario(@RequestParam String hora, Model model) {
        try {
            horarioService.agregarHorario(hora);
            model.addAttribute("clases", claseService.listarTodasClases());
            model.addAttribute("horarios", horarioService.listarHorariosActivos());
            return "clases/lista :: tablaClases";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "clases/lista :: message";
        } catch (Exception e) {
            model.addAttribute("error", "Error al agregar el horario");
            return "clases/lista :: message";
        }
    }

    @PostMapping("/horario/{id}/actualizar")
    public String actualizarHorario(@PathVariable Long id, @RequestParam String hora, Model model) {
        try {
            horarioService.actualizarHorario(id, hora);
            model.addAttribute("clases", claseService.listarTodasClases());
            model.addAttribute("horarios", horarioService.listarHorariosActivos());
            model.addAttribute("mensaje", "Horario actualizado correctamente");
            return "clases/lista :: tablaClases";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "clases/lista :: message";
        } catch (Exception e) {
            log.error("Error al actualizar el horario: ", e);
            model.addAttribute("error", "Error al actualizar el horario");
            return "clases/lista :: message";
        }
    }

    @PostMapping("/horario/{id}/eliminar")
    public String eliminarHorario(@PathVariable Long id, Model model) {
        try {
            horarioService.eliminarHorario(id);
            model.addAttribute("clases", claseService.listarTodasClases());
            model.addAttribute("horarios", horarioService.listarHorariosActivos());
            model.addAttribute("mensaje", "Horario eliminado correctamente");
            return "clases/lista :: tablaClases";
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "clases/lista :: message";
        } catch (Exception e) {
            log.error("Error al eliminar el horario: ", e);
            model.addAttribute("error", "Error al eliminar el horario");
            return "clases/lista :: message";
        }
    }
}