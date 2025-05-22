package com.cinturondorado.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

import com.cinturondorado.dto.InventarioDTO;
import com.cinturondorado.model.Inventario;
import com.cinturondorado.model.enums.TipoEquipo;
import com.cinturondorado.service.InventarioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.web.PageableDefault;

@Slf4j
@Controller
@RequestMapping("/inventario")
public class InventarioController {
    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping
    public String listarInventario(@PageableDefault(size = 10, sort = "id") Pageable pageable, Model model) {
        Page<Inventario> paginaItems = inventarioService.obtenerTodoItemsPaginados(pageable);
        
        model.addAttribute("items", paginaItems.getContent());
        model.addAttribute("page", paginaItems);
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

    @PostMapping("/{id}/item")
    public String actualizarItem(@PathVariable Long id,
            @RequestParam String nombre,
            @RequestParam String tipo,
            @RequestParam Integer cantidad,
            @RequestParam Integer stockMinimo,
            @RequestParam(required = false) String descripcion,
            @PageableDefault(size = 10, sort = "id") Pageable pageable,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            inventarioService.actualizarItem(id, nombre, TipoEquipo.valueOf(tipo), cantidad, stockMinimo, descripcion);
            
            Page<Inventario> paginaItems = inventarioService.obtenerTodoItemsPaginados(pageable);
            model.addAttribute("items", paginaItems.getContent());
            model.addAttribute("page", paginaItems);
            model.addAttribute("mensaje", "Item actualizado exitosamente");
            
            return "inventario/lista :: tablaInventario";
        } catch (Exception e) {
            Page<Inventario> paginaItems = inventarioService.obtenerTodoItemsPaginados(pageable);
            model.addAttribute("items", paginaItems.getContent());
            model.addAttribute("page", paginaItems);
            model.addAttribute("error", "Error al actualizar item: " + e.getMessage());
            return "inventario/lista :: tablaInventario";
        }
    }

    @GetMapping("/bajo-stock")
    public String obtenerItemsBajoStock(@PageableDefault(size = 10, sort = "id") Pageable pageable, Model model) {
        Page<Inventario> paginaItems = inventarioService.obtenerItemsBajoStockPaginados(pageable);
        model.addAttribute("items", paginaItems.getContent());
        model.addAttribute("page", paginaItems);
        return "inventario/lista :: tablaInventario";
    }

    @GetMapping({ "/tipo/{tipo}", "/todos" })
    public String obtenerPorTipo(
            @PathVariable(required = false) TipoEquipo tipo, 
            @PageableDefault(size = 10, sort = "id") Pageable pageable,
            Model model) {
        try {
            Page<Inventario> paginaItems;
            
            if (tipo == null) {
                paginaItems = inventarioService.obtenerTodoItemsPaginados(pageable);
            } else {
                paginaItems = inventarioService.obtenerPorTipoPaginado(tipo, pageable);
            }

            model.addAttribute("items", paginaItems.getContent());
            model.addAttribute("page", paginaItems);
            
            return "inventario/lista :: tablaInventario";
        } catch (Exception e) {
            log.error("Error al filtrar por tipo: {}", e.getMessage());
            model.addAttribute("error", "Error al filtrar items");
            return "inventario/lista :: tablaInventario";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarItem(@PathVariable Long id, 
                              @PageableDefault(size = 10, sort = "id") Pageable pageable,  
                              Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            inventarioService.eliminarItem(id);
            
            Page<Inventario> paginaItems = inventarioService.obtenerTodoItemsPaginados(pageable);
            model.addAttribute("items", paginaItems.getContent());
            model.addAttribute("page", paginaItems);
            model.addAttribute("mensaje", "Item eliminado exitosamente");
            
            return "inventario/lista :: tablaInventario";
        } catch (Exception e) {
            Page<Inventario> paginaItems = inventarioService.obtenerTodoItemsPaginados(pageable);
            model.addAttribute("items", paginaItems.getContent());
            model.addAttribute("page", paginaItems);
            model.addAttribute("error", "Error al eliminar item: " + e.getMessage());
            return "inventario/lista :: tablaInventario";
        }
    }
}