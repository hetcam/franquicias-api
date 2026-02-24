package com.franquicias.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.franquicias.dto.CreateFranquiciaRequest;
import com.franquicias.dto.CreateSucursalRequest;
import com.franquicias.dto.FranquiciaResponse;
import com.franquicias.dto.ProductoMaxStockPorSucursalResponse;
import com.franquicias.dto.SucursalResponse;
import com.franquicias.dto.UpdateFranquiciaNameRequest;
import com.franquicias.dto.UpdateSucursalNameRequest;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

            when(sucursalService.createSucursal(anyLong(), anyString()))
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

    @Nested
    @DisplayName("PATCH /api/franquicias/{franquiciaId}/name")
    class UpdateFranquiciaName {

        @Test
        @DisplayName("returns 200 and franquicia when valid request")
        void returns200AndFranquiciaWhenValid() throws Exception {
            Long franquiciaId = 1L;
            UpdateFranquiciaNameRequest request = new UpdateFranquiciaNameRequest();
            request.setName("Nueva Franquicia");
            FranquiciaResponse response = new FranquiciaResponse(franquiciaId, "Nueva Franquicia");

            when(franquiciaService.updateFranquiciaName(franquiciaId, "Nueva Franquicia")).thenReturn(response);

            mockMvc.perform(patch("/api/franquicias/" + franquiciaId + "/name")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Nueva Franquicia"));

            verify(franquiciaService).updateFranquiciaName(franquiciaId, "Nueva Franquicia");
        }

        @Test
        @DisplayName("returns 404 when franquicia not found")
        void returns404WhenFranquiciaNotFound() throws Exception {
            UpdateFranquiciaNameRequest request = new UpdateFranquiciaNameRequest();
            request.setName("Nueva Franquicia");

            when(franquiciaService.updateFranquiciaName(999L, "Nueva Franquicia"))
                    .thenThrow(new NotFoundException("Franquicia not found: 999"));

            mockMvc.perform(patch("/api/franquicias/999/name")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Franquicia not found: 999"));
        }

        @Test
        @DisplayName("returns 400 when name is blank")
        void returns400WhenNameBlank() throws Exception {
            UpdateFranquiciaNameRequest request = new UpdateFranquiciaNameRequest();
            request.setName("");

            mockMvc.perform(patch("/api/franquicias/1/name")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PATCH /api/franquicias/{franquiciaId}/sucursales/{sucursalId}/name")
    class UpdateSucursalName {

        @Test
        @DisplayName("returns 200 and sucursal when valid request")
        void returns200AndSucursalWhenValid() throws Exception {
            Long franquiciaId = 1L;
            Long sucursalId = 2L;
            UpdateSucursalNameRequest request = new UpdateSucursalNameRequest();
            request.setName("Sucursal Norte");
            SucursalResponse response = new SucursalResponse(sucursalId, "Sucursal Norte", franquiciaId);

            when(sucursalService.updateSucursalName(franquiciaId, sucursalId, "Sucursal Norte")).thenReturn(response);

            mockMvc.perform(patch("/api/franquicias/" + franquiciaId + "/sucursales/" + sucursalId + "/name")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(2))
                    .andExpect(jsonPath("$.name").value("Sucursal Norte"))
                    .andExpect(jsonPath("$.franquiciaId").value(1));

            verify(sucursalService).updateSucursalName(franquiciaId, sucursalId, "Sucursal Norte");
        }

        @Test
        @DisplayName("returns 404 when sucursal does not belong to franquicia")
        void returns404WhenSucursalNotInFranquicia() throws Exception {
            UpdateSucursalNameRequest request = new UpdateSucursalNameRequest();
            request.setName("Sucursal Norte");

            when(sucursalService.updateSucursalName(1L, 999L, "Sucursal Norte"))
                    .thenThrow(new NotFoundException("Sucursal does not belong to franquicia: 1"));

            mockMvc.perform(patch("/api/franquicias/1/sucursales/999/name")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Sucursal does not belong to franquicia: 1"));
        }

        @Test
        @DisplayName("returns 400 when name is missing")
        void returns400WhenNameMissing() throws Exception {
            mockMvc.perform(patch("/api/franquicias/1/sucursales/1/name")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/franquicias/{franquiciaId}/productos-max-stock")
    class GetProductosConMaxStockPorSucursal {

        @Test
        @DisplayName("returns 200 and product list when franquicia exists")
        void returns200AndProductListWhenFranquiciaExists() throws Exception {
            Long franquiciaId = 1L;
            ProductoMaxStockPorSucursalResponse p1 =
                    new ProductoMaxStockPorSucursalResponse(10L, "A", 100, 1L, "Centro");
            ProductoMaxStockPorSucursalResponse p2 =
                    new ProductoMaxStockPorSucursalResponse(11L, "B", 50, 2L, "Norte");

            when(productoService.getProductosConMaxStockPorSucursal(franquiciaId)).thenReturn(List.of(p1, p2));

            mockMvc.perform(get("/api/franquicias/" + franquiciaId + "/productos-max-stock"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].productId").value(10))
                    .andExpect(jsonPath("$[0].productName").value("A"))
                    .andExpect(jsonPath("$[0].stock").value(100))
                    .andExpect(jsonPath("$[0].sucursalId").value(1))
                    .andExpect(jsonPath("$[0].sucursalName").value("Centro"))
                    .andExpect(jsonPath("$[1].productId").value(11));

            verify(productoService).getProductosConMaxStockPorSucursal(franquiciaId);
        }

        @Test
        @DisplayName("returns 404 when franquicia does not exist")
        void returns404WhenFranquiciaNotFound() throws Exception {
            when(productoService.getProductosConMaxStockPorSucursal(999L))
                    .thenThrow(new NotFoundException("Franquicia not found: 999"));

            mockMvc.perform(get("/api/franquicias/999/productos-max-stock"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Franquicia not found: 999"));
        }
    }
}
