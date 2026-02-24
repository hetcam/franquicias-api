package com.franquicias.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.franquicias.dto.CreateProductoRequest;
import com.franquicias.dto.ProductoResponse;
import com.franquicias.dto.UpdateProductoNameRequest;
import com.franquicias.dto.UpdateStockRequest;
import com.franquicias.exception.GlobalExceptionHandler;
import com.franquicias.exception.NotFoundException;
import com.franquicias.service.ProductoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SucursalController.class)
@Import(GlobalExceptionHandler.class)
class SucursalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductoService productoService;

    @Nested
    @DisplayName("POST /api/sucursales/{sucursalId}/productos")
    class AddProducto {

        @Test
        @DisplayName("returns 201 and producto when valid request")
        void returns201AndProductoWhenValid() throws Exception {
            Long sucursalId = 1L;
            CreateProductoRequest request = new CreateProductoRequest();
            request.setName("Producto A");
            request.setDescription("Desc");
            request.setStock(10);
            ProductoResponse response = new ProductoResponse(1L, "Producto A", 10, sucursalId);

            when(productoService.createProducto(eq(sucursalId), eq("Producto A"), eq("Desc"), eq(10)))
                    .thenReturn(response);

            mockMvc.perform(post("/api/sucursales/" + sucursalId + "/productos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Producto A"))
                    .andExpect(jsonPath("$.stock").value(10))
                    .andExpect(jsonPath("$.sucursalId").value(1));

            verify(productoService).createProducto(sucursalId, "Producto A", "Desc", 10);
        }

        @Test
        @DisplayName("returns 404 when sucursal not found")
        void returns404WhenSucursalNotFound() throws Exception {
            CreateProductoRequest request = new CreateProductoRequest();
            request.setName("P");
            request.setStock(0);

            when(productoService.createProducto(anyLong(), anyString(), any(), anyInt()))
                    .thenThrow(new NotFoundException("Sucursal not found: 999"));

            mockMvc.perform(post("/api/sucursales/999/productos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("returns 400 when name is blank or stock missing")
        void returns400WhenValidationFails() throws Exception {
            CreateProductoRequest request = new CreateProductoRequest();
            request.setName("");
            request.setStock(0);

            mockMvc.perform(post("/api/sucursales/1/productos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /api/sucursales/{sucursalId}/productos/{productoId}")
    class DeleteProducto {

        @Test
        @DisplayName("returns 204 when producto deleted")
        void returns204WhenDeleted() throws Exception {
            Long sucursalId = 1L;
            Long productoId = 2L;
            doNothing().when(productoService).deleteProductoFromSucursal(sucursalId, productoId);

            mockMvc.perform(delete("/api/sucursales/" + sucursalId + "/productos/" + productoId))
                    .andExpect(status().isNoContent());

            verify(productoService).deleteProductoFromSucursal(sucursalId, productoId);
        }

        @Test
        @DisplayName("returns 404 when producto not in sucursal")
        void returns404WhenNotFound() throws Exception {
            Long sucursalId = 1L;
            Long productoId = 999L;
            doThrow(new NotFoundException("Producto not found in sucursal: 999"))
                    .when(productoService).deleteProductoFromSucursal(sucursalId, productoId);

            mockMvc.perform(delete("/api/sucursales/" + sucursalId + "/productos/" + productoId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Producto not found in sucursal: 999"));
        }
    }

    @Nested
    @DisplayName("PATCH /api/sucursales/{sucursalId}/productos/{productoId}/stock")
    class UpdateStock {

        @Test
        @DisplayName("returns 200 and updated producto when valid")
        void returns200AndProductoWhenValid() throws Exception {
            Long sucursalId = 1L;
            Long productoId = 2L;
            UpdateStockRequest request = new UpdateStockRequest();
            request.setStock(50);
            ProductoResponse response = new ProductoResponse(productoId, "P", 50, sucursalId);

            when(productoService.updateStock(sucursalId, productoId, 50)).thenReturn(response);

            mockMvc.perform(patch("/api/sucursales/" + sucursalId + "/productos/" + productoId + "/stock")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.stock").value(50));

            verify(productoService).updateStock(sucursalId, productoId, 50);
        }

        @Test
        @DisplayName("returns 404 when producto not found")
        void returns404WhenProductoNotFound() throws Exception {
            UpdateStockRequest request = new UpdateStockRequest();
            request.setStock(10);

            when(productoService.updateStock(1L, 999L, 10))
                    .thenThrow(new NotFoundException("Producto not found: 999"));

            mockMvc.perform(patch("/api/sucursales/1/productos/999/stock")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("returns 400 when stock is negative")
        void returns400WhenStockNegative() throws Exception {
            UpdateStockRequest request = new UpdateStockRequest();
            request.setStock(-1);

            mockMvc.perform(patch("/api/sucursales/1/productos/1/stock")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 400 when stock is null")
        void returns400WhenStockNull() throws Exception {
            mockMvc.perform(patch("/api/sucursales/1/productos/1/stock")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PATCH /api/sucursales/{sucursalId}/productos/{productoId}/name")
    class UpdateProductoName {

        @Test
        @DisplayName("returns 200 and updated producto when valid")
        void returns200AndProductoWhenValid() throws Exception {
            Long sucursalId = 1L;
            Long productoId = 2L;
            UpdateProductoNameRequest request = new UpdateProductoNameRequest();
            request.setName("Producto Renombrado");
            ProductoResponse response = new ProductoResponse(productoId, "Producto Renombrado", 10, sucursalId);

            when(productoService.updateProductoName(sucursalId, productoId, "Producto Renombrado"))
                    .thenReturn(response);

            mockMvc.perform(patch("/api/sucursales/" + sucursalId + "/productos/" + productoId + "/name")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(2))
                    .andExpect(jsonPath("$.name").value("Producto Renombrado"))
                    .andExpect(jsonPath("$.stock").value(10))
                    .andExpect(jsonPath("$.sucursalId").value(1));

            verify(productoService).updateProductoName(sucursalId, productoId, "Producto Renombrado");
        }

        @Test
        @DisplayName("returns 404 when producto does not exist")
        void returns404WhenProductoNotFound() throws Exception {
            UpdateProductoNameRequest request = new UpdateProductoNameRequest();
            request.setName("Producto Renombrado");

            when(productoService.updateProductoName(1L, 999L, "Producto Renombrado"))
                    .thenThrow(new NotFoundException("Producto not found: 999"));

            mockMvc.perform(patch("/api/sucursales/1/productos/999/name")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Producto not found: 999"));
        }

        @Test
        @DisplayName("returns 400 when name is blank")
        void returns400WhenNameBlank() throws Exception {
            UpdateProductoNameRequest request = new UpdateProductoNameRequest();
            request.setName("");

            mockMvc.perform(patch("/api/sucursales/1/productos/1/name")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }
}
