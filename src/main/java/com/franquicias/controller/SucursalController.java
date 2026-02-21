package com.franquicias.controller;

import com.franquicias.dto.CreateProductoRequest;
import com.franquicias.dto.ProductoResponse;
import com.franquicias.dto.UpdateStockRequest;
import com.franquicias.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sucursales/{sucursalId}/productos")
public class SucursalController {

    private final ProductoService productoService;

    public SucursalController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PostMapping
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
    public ResponseEntity<Void> deleteProducto(
            @PathVariable Long sucursalId,
            @PathVariable Long productoId) {
        productoService.deleteProductoFromSucursal(sucursalId, productoId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{productoId}/stock")
    public ResponseEntity<ProductoResponse> updateStock(
            @PathVariable Long sucursalId,
            @PathVariable Long productoId,
            @Valid @RequestBody UpdateStockRequest request) {
        ProductoResponse producto = productoService.updateStock(sucursalId, productoId, request.getStock());
        return ResponseEntity.ok(producto);
    }
}
