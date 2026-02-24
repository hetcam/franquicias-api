package com.franquicias.controller;

import com.franquicias.dto.CreateProductoRequest;
import com.franquicias.dto.ProductoResponse;
import com.franquicias.dto.UpdateProductoNameRequest;
import com.franquicias.dto.UpdateStockRequest;
import com.franquicias.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sucursales/{sucursalId}/productos")
@Tag(name = "Productos", description = "Endpoints for productos in sucursales")
public class SucursalController {

    private final ProductoService productoService;

    public SucursalController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PostMapping
    @Operation(summary = "Create product in sucursal")
    public ResponseEntity<ProductoResponse> addProducto(
            @PathVariable Long sucursalId,
            @Valid @RequestBody CreateProductoRequest request) {
        ProductoResponse producto = productoService.createProducto(
                sucursalId,
                request.getName(),
                request.getDescription(),
                request.getStock());
        return ResponseEntity.status(HttpStatus.CREATED).body(producto);
    }

    @DeleteMapping("/{productoId}")
    @Operation(summary = "Delete product from sucursal")
    public ResponseEntity<Void> deleteProducto(
            @PathVariable Long sucursalId,
            @PathVariable Long productoId) {
        productoService.deleteProductoFromSucursal(sucursalId, productoId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{productoId}/stock")
    @Operation(summary = "Update product stock")
    public ResponseEntity<ProductoResponse> updateStock(
            @PathVariable Long sucursalId,
            @PathVariable Long productoId,
            @Valid @RequestBody UpdateStockRequest request) {
        ProductoResponse producto = productoService.updateStock(sucursalId, productoId, request.getStock());
        return ResponseEntity.ok(producto);
    }

    @PatchMapping("/{productoId}/name")
    @Operation(summary = "Update product name")
    public ResponseEntity<ProductoResponse> updateProductoName(
            @PathVariable Long sucursalId,
            @PathVariable Long productoId,
            @Valid @RequestBody UpdateProductoNameRequest request) {
        ProductoResponse producto = productoService.updateProductoName(sucursalId, productoId, request.getName());
        return ResponseEntity.ok(producto);
    }
}
