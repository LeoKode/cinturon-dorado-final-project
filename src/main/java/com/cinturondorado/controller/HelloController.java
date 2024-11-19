package com.cinturondorado.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("/")
    public String index(Model model) {
        // Aquí puedes agregar los datos para las estadísticas
        model.addAttribute("totalAlumnos", 0);
        model.addAttribute("clasesHoy", 0);
        model.addAttribute("pagosPendientes", 0);
        model.addAttribute("proximosExamenes", 0);
        return "index";
    }
}
