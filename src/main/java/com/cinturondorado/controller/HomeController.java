package com.cinturondorado.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.cinturondorado.dto.AlumnoDTO;
import com.cinturondorado.dto.ClaseDTO;
import com.cinturondorado.dto.ExamenDTO;
import com.cinturondorado.dto.PagoDTO;
import com.cinturondorado.model.enums.EstadoPago;
import com.cinturondorado.model.enums.NivelCinturon;
import com.cinturondorado.model.enums.TipoClase;
import com.cinturondorado.service.AlumnoService;
import com.cinturondorado.service.ExamenService;
import com.cinturondorado.service.PagoService;

@Controller
public class HomeController {

    @Autowired
    private AlumnoService alumnoService;

    @Autowired
    private PagoService pagoService;

    @Autowired
    private ExamenService examenService;

    @GetMapping("/")
    public String home(Model model, Pageable pageable) {
        // Datos para los contadores
        model.addAttribute("totalAlumnos", alumnoService.contarAlumnos());
        model.addAttribute("pagosPendientes", pagoService.contarPagosPendientes());
        model.addAttribute("proximosExamenes", examenService.contarProximosExamenes());

        // Datos necesarios para los modales
        AlumnoDTO alumnoDTO = new AlumnoDTO();
        alumnoDTO.setFechaNacimiento(LocalDate.of(2005, 1, 1)); // Establece una fecha por defecto
        model.addAttribute("alumno", alumnoDTO); // Para el modal de nuevo alumno
        
        model.addAttribute("clase", new ClaseDTO()); // Para el modal de nueva clase

        PagoDTO pagoDTO = new PagoDTO();
        pagoDTO.setFecha(LocalDate.now()); // Establece la fecha actual
        model.addAttribute("pagoDTO", pagoDTO); // Para el modal de nuevo pago

        model.addAttribute("examen", new ExamenDTO()); // Para el modal de nuevo examen

        // Datos para los selects en los modales
        model.addAttribute("alumnos", alumnoService.listarTodos(pageable));
        model.addAttribute("tiposClase", TipoClase.values());
        model.addAttribute("tiposPago", EstadoPago.values());
        model.addAttribute("nivelesCinturon", NivelCinturon.values());

        return "index";
    }
}
