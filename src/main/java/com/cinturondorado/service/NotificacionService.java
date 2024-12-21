package com.cinturondorado.service;

import com.cinturondorado.model.Alumno;
import com.cinturondorado.model.Inventario;
import com.cinturondorado.model.Pago;
import com.cinturondorado.model.enums.NivelCinturon;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final JavaMailSender emailSender;
    private static final String FIRMA = "\n\nSaludos cordiales,\nAcademia Cinturón Dorado";

    @Async
    public void enviarBienvenida(Alumno alumno) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(alumno.getEmail());
        mensaje.setSubject("¡Bienvenido a Cinturón Dorado!");
        mensaje.setText(String.format(
            "Estimado %s,\n\n" +
            "¡Bienvenido a la academia Cinturón Dorado!\n" +
            "Tu registro ha sido completado exitosamente.\n\n" +
            "Nivel actual: %s" +
            FIRMA,
            alumno.getNombre(),
            alumno.getNivelCinturon()
        ));
        emailSender.send(mensaje);
    }

    @Async
    public void enviarConfirmacionPago(Pago pago) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(pago.getAlumno().getEmail());
        mensaje.setSubject("Confirmación de Pago - Cinturón Dorado");
        mensaje.setText(String.format(
            "Estimado %s,\n\n" +
            "Confirmamos la recepción de tu pago:\n" +
            "Monto: $%.2f\n" +
            "Fecha: %s\n\n" +
            "Gracias por tu puntualidad." +
            FIRMA,
            pago.getAlumno().getNombre(),
            pago.getMonto(),
            pago.getFecha()
        ));
        emailSender.send(mensaje);
    }

    @Async
    public void notificarPagoVencido(Pago pago) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(pago.getAlumno().getEmail());
        mensaje.setSubject("Aviso de Pago Vencido - Cinturón Dorado");
        mensaje.setText(String.format(
            "Estimado %s,\n\n" +
            "Te recordamos que tienes un pago vencido:\n" +
            "Monto pendiente: $%.2f\n" +
            "Fecha de vencimiento: %s\n\n" +
            "Por favor, regulariza tu situación lo antes posible." +
            FIRMA,
            pago.getAlumno().getNombre(),
            pago.getMonto(),
            pago.getFecha()
        ));
        emailSender.send(mensaje);
    }

    @Async
    public void notificarCambioNivel(Alumno alumno, NivelCinturon nuevoNivel) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(alumno.getEmail());
        mensaje.setSubject("¡Felicitaciones por tu nuevo nivel!");
        mensaje.setText(String.format(
            "Estimado %s,\n\n" +
            "¡Felicitaciones! Has alcanzado el nivel de cinturón %s.\n" +
            "Sigue esforzándote y mejorando cada día." +
            FIRMA,
            alumno.getNombre(),
            nuevoNivel
        ));
        emailSender.send(mensaje);
    }

    public void notificarStockBajo(Inventario item) {
        // Implementa aquí la lógica de notificación
        // Por ejemplo: enviar email, notificación push, etc.
    }

    public void enviarReporteInventarioBajo(List<Inventario> itemsBajoStock) {
        // Implementa aquí la lógica para enviar el reporte
        // Por ejemplo, enviar un email o generar una notificación
        itemsBajoStock.forEach(item -> 
            notificarStockBajo(item)
        );
    }
} 
