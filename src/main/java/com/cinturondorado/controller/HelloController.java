package com.cinturondorado.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("/")
    public String index(Model model) {
        // Simular datos dinámicos que provienen de la base de datos
        int totalAlumnos = obtenerTotalAlumnos(); // Método simulado
        int clasesHoy = obtenerClasesHoy();
        int pagosPendientes = obtenerPagosPendientes();
        int proximosExamenes = obtenerProximosExamenes();

        // Cargar datos en el modelo
        model.addAttribute("pageTitle", "Inicio - Cinturón Dorado");
        model.addAttribute("totalAlumnos", totalAlumnos);
        model.addAttribute("clasesHoy", clasesHoy);
        model.addAttribute("pagosPendientes", pagosPendientes);
        model.addAttribute("proximosExamenes", proximosExamenes);

        // Devuelve la vista 'index.html'
        return "index";
    }

    private int obtenerTotalAlumnos() {
        return 120; // Reemplaza con una consulta real
    }

    private int obtenerClasesHoy() {
        return 5; // Reemplaza con una consulta real
    }

    private int obtenerPagosPendientes() {
        return 10; // Reemplaza con una consulta real
    }

    private int obtenerProximosExamenes() {
        return 2; // Reemplaza con una consulta real
    }
}
