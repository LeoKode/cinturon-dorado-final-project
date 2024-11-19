package com.cinturondorado.controller;

import com.cinturondorado.dto.InventarioDTO;
import com.cinturondorado.model.Inventario;
import com.cinturondorado.model.enums.TipoEquipo;
import com.cinturondorado.service.InventarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InventarioControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private InventarioService inventarioService;
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void agregarItem_ConDatosValidos_RetornaItemCreado() throws Exception {
        // Arrange
        InventarioDTO dto = new InventarioDTO();
        dto.setNombre("Peto de protección");
        dto.setCantidad(10);
        dto.setTipo(TipoEquipo.PROTECCION);
        dto.setStockMinimo(5);
        
        Inventario itemCreado = new Inventario();
        itemCreado.setId(1L);
        itemCreado.setNombre(dto.getNombre());
        itemCreado.setCantidad(dto.getCantidad());
        itemCreado.setTipo(dto.getTipo());
        
        when(inventarioService.agregarItem(any(InventarioDTO.class)))
            .thenReturn(itemCreado);
            
        // Act & Assert
        mockMvc.perform(post("/api/inventario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nombre").value(dto.getNombre()))
                .andExpect(jsonPath("$.data.cantidad").value(dto.getCantidad()));
    }
    
    @Test
    @WithMockUser(roles = "STAFF")
    void obtenerItemsBajoStock_RetornaListaItems() throws Exception {
        // Arrange
        List<Inventario> items = Arrays.asList(
            new Inventario(1L, "Uniforme", 3, "Talla M", TipoEquipo.UNIFORME, 5, LocalDate.now()),
            new Inventario(2L, "Guantes", 2, "Talla L", TipoEquipo.PROTECCION, 4, LocalDate.now())
        );
        
        when(inventarioService.obtenerItemsBajoStock())
            .thenReturn(items);
            
        // Act & Assert
        mockMvc.perform(get("/api/inventario/bajo-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].nombre").value("Uniforme"))
                .andExpect(jsonPath("$.data[1].nombre").value("Guantes"));
    }
    
    @Test
    void accederSinAutenticacion_RetornaUnauthorized() throws Exception {
        mockMvc.perform(get("/api/inventario/bajo-stock"))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @WithMockUser(roles = "ALUMNO")
    void accederConRolInvalido_RetornaForbidden() throws Exception {
        mockMvc.perform(get("/api/inventario/bajo-stock"))
                .andExpect(status().isForbidden());
    }
} 