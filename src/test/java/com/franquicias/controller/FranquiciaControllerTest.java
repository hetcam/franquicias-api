package com.franquicias.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.franquicias.dto.CreateFranquiciaRequest;
import com.franquicias.dto.CreateSucursalRequest;
import com.franquicias.dto.FranquiciaResponse;
import com.franquicias.dto.SucursalResponse;
import com.franquicias.exception.NotFoundException;
import com.franquicias.exception.GlobalExceptionHandler;
import com.franquicias.service.FranquiciaService;
import com.franquicias.service.ProductoService;
import com.franquicias.service.SucursalService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = FranquiciaController.class)
@Import(GlobalExceptionHandler.class)
class FranquiciaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FranquiciaService franquiciaService;

    @MockBean
    private SucursalService sucursalService;

    @MockBean
    private ProductoService productoService;

    @Nested
    @DisplayName("POST /api/franquicias")
    class AddFranquicia {

        @Test
        @DisplayName("returns 201 and franquicia when valid request")
        void returns201AndFranquiciaWhenValid() throws Exception {
            CreateFranquiciaRequest request = new CreateFranquiciaRequest();
            request.setName("Mi Franquicia");
            FranquiciaResponse response = new FranquiciaResponse(1L, "Mi Franquicia");

            when(franquiciaService.createFranquicia("Mi Franquicia")).thenReturn(response);

            mockMvc.perform(post("/api/franquicias")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Mi Franquicia"));

            verify(franquiciaService).createFranquicia("Mi Franquicia");
        }

        @Test
        @DisplayName("returns 400 when name is blank")
        void returns400WhenNameBlank() throws Exception {
            CreateFranquiciaRequest request = new CreateFranquiciaRequest();
            request.setName("");

            mockMvc.perform(post("/api/franquicias")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 400 when name is missing")
        void returns400WhenNameMissing() throws Exception {
            mockMvc.perform(post("/api/franquicias")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/franquicias/{franquiciaId}/sucursales")
    class AddSucursal {

        @Test
        @DisplayName("returns 201 and sucursal when valid request")
        void returns201AndSucursalWhenValid() throws Exception {
            Long franquiciaId = 1L;
            CreateSucursalRequest request = new CreateSucursalRequest();
            request.setName("Sucursal Centro");
            request.setAddress("Calle 123");
            SucursalResponse response = new SucursalResponse(1L, "Sucursal Centro", franquiciaId);

            when(sucursalService.createSucursal(eq(franquiciaId), eq("Sucursal Centro")))
                    .thenReturn(response);

            mockMvc.perform(post("/api/franquicias/" + franquiciaId + "/sucursales")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Sucursal Centro"))
                    .andExpect(jsonPath("$.franquiciaId").value(1));

            verify(sucursalService).createSucursal(franquiciaId, "Sucursal Centro");
        }

        @Test
        @DisplayName("returns 404 when franquicia not found")
        void returns404WhenFranquiciaNotFound() throws Exception {
            Long franquiciaId = 999L;
            CreateSucursalRequest request = new CreateSucursalRequest();
            request.setName("Sucursal");
            request.setAddress("Address");

            when(sucursalService.createSucursal(anyLong(), anyString(), anyString()))
                    .thenThrow(new NotFoundException("Franquicia not found: 999"));

            mockMvc.perform(post("/api/franquicias/" + franquiciaId + "/sucursales")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Franquicia not found: 999"));
        }

        @Test
        @DisplayName("returns 400 when sucursal name is blank")
        void returns400WhenSucursalNameBlank() throws Exception {
            CreateSucursalRequest request = new CreateSucursalRequest();
            request.setName("");
            request.setAddress("Address");

            mockMvc.perform(post("/api/franquicias/1/sucursales")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }
}
