package com.franquicias.controller;

import com.franquicias.dto.CreateSucursalRequest;
import com.franquicias.dto.CreateFranquiciaRequest;
import com.franquicias.dto.FranquiciaResponse;
import com.franquicias.dto.ProductoMaxStockPorSucursalResponse;
import com.franquicias.dto.SucursalResponse;
import com.franquicias.dto.UpdateFranquiciaNameRequest;
import com.franquicias.dto.UpdateSucursalNameRequest;
import com.franquicias.service.FranquiciaService;
import com.franquicias.service.ProductoService;
import com.franquicias.service.SucursalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/franquicias")
@Tag(name = "Franquicias", description = "Endpoints for franquicias and related sucursales")
public class FranquiciaController {

    private final FranquiciaService franquiciaService;
    private final SucursalService sucursalService;
    private final ProductoService productoService;

    public FranquiciaController(FranquiciaService franquiciaService,
                                SucursalService sucursalService,
                                ProductoService productoService) {
        this.franquiciaService = franquiciaService;
        this.sucursalService = sucursalService;
        this.productoService = productoService;
    }

    @PostMapping
    @Operation(summary = "Create a franquicia")
    public ResponseEntity<FranquiciaResponse> addFranquicia(@Valid @RequestBody CreateFranquiciaRequest request) {
        FranquiciaResponse franquicia = franquiciaService.createFranquicia(request.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(franquicia);
    }

    @PostMapping("/{franquiciaId}/sucursales")
    @Operation(summary = "Create a sucursal in a franquicia")
    public ResponseEntity<SucursalResponse> addSucursal(
            @PathVariable Long franquiciaId,
            @Valid @RequestBody CreateSucursalRequest request) {
        SucursalResponse sucursal = sucursalService.createSucursal(
                franquiciaId,
                request.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(sucursal);
    }

    @PatchMapping("/{franquiciaId}/name")
    @Operation(summary = "Update franquicia name")
    public ResponseEntity<FranquiciaResponse> updateFranquiciaName(
            @PathVariable Long franquiciaId,
            @Valid @RequestBody UpdateFranquiciaNameRequest request) {
        FranquiciaResponse franquicia = franquiciaService.updateFranquiciaName(franquiciaId, request.getName());
        return ResponseEntity.ok(franquicia);
    }

    @PatchMapping("/{franquiciaId}/sucursales/{sucursalId}/name")
    @Operation(summary = "Update sucursal name in a franquicia")
    public ResponseEntity<SucursalResponse> updateSucursalName(
            @PathVariable Long franquiciaId,
            @PathVariable Long sucursalId,
            @Valid @RequestBody UpdateSucursalNameRequest request) {
        SucursalResponse sucursal = sucursalService.updateSucursalName(franquiciaId, sucursalId, request.getName());
        return ResponseEntity.ok(sucursal);
    }

    @GetMapping("/{franquiciaId}/productos-max-stock")
    @Operation(summary = "Get products with max stock per sucursal")
    public ResponseEntity<List<ProductoMaxStockPorSucursalResponse>> getProductosConMaxStockPorSucursal(
            @PathVariable Long franquiciaId) {
        List<ProductoMaxStockPorSucursalResponse> productos = productoService.getProductosConMaxStockPorSucursal(franquiciaId);
        return ResponseEntity.ok(productos);
    }
}
