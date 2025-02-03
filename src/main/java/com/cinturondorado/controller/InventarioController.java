package com.cinturondorado.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

import com.cinturondorado.dto.InventarioDTO;
import com.cinturondorado.model.Inventario;
import com.cinturondorado.model.enums.TipoEquipo;
import com.cinturondorado.service.InventarioService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/inventario")
public class InventarioController {
    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }
    
    @GetMapping
    public String listarInventario(Model model) {
        model.addAttribute("items", inventarioService.obtenerTodoItems());
        model.addAttribute("inventarioDTO", new InventarioDTO());
        model.addAttribute("tiposEquipo", TipoEquipo.values());
        return "inventario/lista";
    }

    @PostMapping
    public String agregarItem(@Valid @ModelAttribute("inventarioDTO") InventarioDTO inventarioDTO,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        log.info("Datos recibidos en el controlador: {}", inventarioDTO);
        
        if (result.hasErrors()) {
            log.error("Errores de validación encontrados: {}", result.getAllErrors());
            model.addAttribute("tiposEquipo", TipoEquipo.values());
            model.addAttribute("items", inventarioService.obtenerTodoItems());
            return "inventario/lista :: modal";
        }
        
        try {
            inventarioService.agregarItem(inventarioDTO);
            log.info("Item agregado exitosamente");
            redirectAttributes.addFlashAttribute("mensaje", "Item agregado exitosamente");
        } catch (Exception e) {
            log.error("Error al guardar item: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error al agregar item: " + e.getMessage());
        }
        return "redirect:/inventario";
    }

    @PostMapping("/{id}/cantidad")
    public String actualizarCantidad(@PathVariable Long id,
                                   @RequestParam Integer cantidad,
                                   RedirectAttributes redirectAttributes) {
        try {
            inventarioService.actualizarCantidad(id, cantidad);
            redirectAttributes.addFlashAttribute("mensaje", "Cantidad actualizada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar cantidad: " + e.getMessage());
        }
        return "redirect:/inventario";
    }

    @GetMapping("/bajo-stock")
    public String obtenerItemsBajoStock(Model model) {
        List<Inventario> items = inventarioService.obtenerItemsBajoStock();
        model.addAttribute("items", items);
        return "inventario/lista :: tablaInventario";
    }

    @GetMapping("/tipo/{tipo}")
    public String obtenerPorTipo(@PathVariable TipoEquipo tipo, Model model) {
        List<Inventario> items = inventarioService.obtenerPorTipo(tipo);
        model.addAttribute("items", items);
        return "inventario/lista :: tablaInventario";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            inventarioService.eliminarItem(id);
            redirectAttributes.addFlashAttribute("mensaje", "Item eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar item: " + e.getMessage());
        }
        return "redirect:/inventario";
    }
} 