package com.cinturondorado.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cinturondorado.model.enums.NivelCinturon;

@Controller
@RequestMapping("/examenes")
public class ExamenViewController {
    
    @GetMapping
    public String listarExamenes(Model model) {
        // Aquí agregarías la lógica para obtener los datos
        model.addAttribute("nivelesCinturon", NivelCinturon.values());
        return "examenes/lista";
    }
}