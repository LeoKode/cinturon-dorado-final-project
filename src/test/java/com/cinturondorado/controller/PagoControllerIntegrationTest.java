package com.cinturondorado.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PagoControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private PagoService pagoService;
    
    @Test
    void registrarPago_ConDatosValidos_RetornaPagoCreado() throws Exception {
        // Arrange
        PagoDTO pagoDTO = new PagoDTO();
        pagoDTO.setAlumnoId(1L);
        pagoDTO.setMonto(new BigDecimal("100.00"));
        
        Pago pagoCreado = new Pago();
        pagoCreado.setId(1L);
        pagoCreado.setMonto(new BigDecimal("100.00"));
        pagoCreado.setEstado(EstadoPago.PAGADO);
        
        when(pagoService.registrarPago(any(PagoDTO.class)))
            .thenReturn(pagoCreado);
            
        // Act & Assert
        mockMvc.perform(post("/api/pagos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pagoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.monto").value("100.00"));
    }
    
    @Test
    void obtenerPagosPorAlumno_AlumnoExistente_RetornaListaPagos() throws Exception {
        // Arrange
        Long alumnoId = 1L;
        List<Pago> pagos = Arrays.asList(
            new Pago(1L, new BigDecimal("100.00"), EstadoPago.PAGADO),
            new Pago(2L, new BigDecimal("150.00"), EstadoPago.PENDIENTE)
        );
        
        when(pagoService.obtenerPagosPorAlumno(alumnoId))
            .thenReturn(pagos);
            
        // Act & Assert
        mockMvc.perform(get("/api/pagos/alumno/{alumnoId}", alumnoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].estado").value("PAGADO"));
    }
} 