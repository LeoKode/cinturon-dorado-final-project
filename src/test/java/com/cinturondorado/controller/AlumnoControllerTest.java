package com.cinturondorado.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlumnoControllerTest {
    
    @Mock
    private AlumnoService alumnoService;
    
    @InjectMocks
    private AlumnoController alumnoController;
    
    @Test
    void crearAlumno_ConDatosValidos_RetornaAlumnoCreado() {
        // Arrange
        AlumnoDTO alumnoDTO = new AlumnoDTO();
        alumnoDTO.setNombre("Juan Pérez");
        alumnoDTO.setEdad(15);
        
        Alumno alumnoCreado = new Alumno();
        alumnoCreado.setId(1L);
        alumnoCreado.setNombre("Juan Pérez");
        alumnoCreado.setEdad(15);
        alumnoCreado.setNivelCinturon(NivelCinturon.BLANCO);
        
        when(alumnoService.registrarAlumno(any(AlumnoDTO.class)))
            .thenReturn(alumnoCreado);
            
        // Act
        ResponseEntity<ApiResponse<AlumnoDTO>> response = 
            alumnoController.crearAlumno(alumnoDTO);
            
        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Juan Pérez", response.getBody().getData().getNombre());
    }
    
    @Test
    void obtenerAlumnosConPagosPendientes_RetornaListaDeAlumnos() {
        // Arrange
        List<Alumno> alumnos = Arrays.asList(
            new Alumno(1L, "Juan", 15, NivelCinturon.BLANCO),
            new Alumno(2L, "María", 16, NivelCinturon.AMARILLO)
        );
        
        when(alumnoService.obtenerAlumnosConPagosPendientes())
            .thenReturn(alumnos);
            
        // Act
        ResponseEntity<ApiResponse<List<AlumnoDTO>>> response = 
            alumnoController.obtenerAlumnosConPagosPendientes();
            
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getData().size());
    }
} 