package com.cinturondorado.controller;

import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cinturondorado.service.ClaseService;
import com.cinturondorado.service.ProfesorService;

import lombok.extern.slf4j.Slf4j;

import com.cinturondorado.dto.ApiResponse;
import com.cinturondorado.model.Clase;
import com.cinturondorado.model.enums.NivelCinturon;
import com.cinturondorado.model.enums.TipoClase;

@Slf4j
@Controller
@RequestMapping("/clases")
public class ClaseController {

    private final ClaseService claseService;

    public ClaseController(ClaseService claseService) {
        this.claseService = claseService;

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

    @GetMapping("/api/clases")
    @ResponseBody
    public List<Map<String, Object>> obtenerClasesParaCalendario() {
        List<Clase> clases = claseService.listarTodasClases();
        return clases.stream().map(clase -> {
            Map<String, Object> eventoCalendario = new HashMap<>();
            eventoCalendario.put("id", clase.getId());
            eventoCalendario.put("title", clase.getTitulo());
            eventoCalendario.put("start", clase.getFechaHora());
            eventoCalendario.put("duration", clase.getDuracion()); // Enviar la duración real
    
            // Calcular la fecha de fin sumando la duración correcta
            if (clase.getFechaHora() != null) {
                LocalDateTime fechaFin = clase.getFechaHora().plusMinutes(clase.getDuracion());
                eventoCalendario.put("end", fechaFin);
            }
            
            return eventoCalendario;
        }).collect(Collectors.toList());
    }

    @PostMapping
    public String guardarClase(@ModelAttribute Clase clase, RedirectAttributes redirectAttributes) {
        try {
            log.info("Recibiendo clase para guardar: {}", clase); // Log del objeto recibido
            log.info("Duración recibida: {}", clase.getDuracion()); // Log específico de la duración
    
            if (clase.getFechaHora() == null || clase.getTipo() == null ||
                    clase.getTitulo() == null || clase.getDuracion() == 0) {
                throw new IllegalArgumentException("La fecha, tipo, título y duración de la clase son requeridos");
            }
    
            claseService.guardarClase(clase);
            redirectAttributes.addFlashAttribute("mensaje", "Clase guardada exitosamente");
            return "redirect:/clases";
        } catch (Exception e) {
            log.error("Error al guardar la clase: ", e); // Log de error
            redirectAttributes.addFlashAttribute("error", "Error al guardar la clase: " + e.getMessage());
            return "redirect:/clases";
        }
    }

    @PostMapping("/plantilla")
    @ResponseBody
    public ResponseEntity<Clase> guardarPlantillaClase(@ModelAttribute Clase clase) {
        try {
            if (clase.getTitulo() == null || clase.getDuracion() == 0) {
                throw new IllegalArgumentException("El título y duración de la clase son requeridos");
            }

            clase.setTipo(
                    clase.getTitulo().toLowerCase().contains("infantil") ? TipoClase.PRINCIPIANTE : TipoClase.AVANZADO);
            clase.setFechaHora(null);

            Clase claseGuardada = claseService.guardarPlantillaClase(clase);
            return ResponseEntity.ok(claseGuardada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/actualizar-fecha")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> actualizarFechaClase(
            @PathVariable Long id,
            @RequestBody Map<String, String> fechaData) {
        try {
            LocalDateTime nuevaFecha = LocalDateTime.parse(fechaData.get("fechaHora"));
            claseService.actualizarFechaClase(id, nuevaFecha);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al actualizar la fecha: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarClase(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            claseService.eliminarClase(id);
            redirectAttributes.addFlashAttribute("mensaje", "Clase eliminada exitosamente");
            return "redirect:/clases";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la clase: " + e.getMessage());
            return "redirect:/clases";
        }
    }

    @GetMapping
    public String listarClases(Model model) {
        // Add required data to model
        // model.addAttribute("profesores", profesorService.listarTodos());
        model.addAttribute("niveles", NivelCinturon.values());
        model.addAttribute("clases", claseService.listarTodasClases());

        // Set page title
        model.addAttribute("pageTitle", "Gestión de Clases - Cinturón Dorado");

        return "clases/lista";
    }
}