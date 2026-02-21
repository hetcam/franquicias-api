package com.franquicias.controller;

import com.franquicias.dto.CreateSucursalRequest;
import com.franquicias.dto.CreateFranquiciaRequest;
import com.franquicias.dto.FranquiciaResponse;
import com.franquicias.dto.ProductoMaxStockPorSucursalResponse;
import com.franquicias.dto.SucursalResponse;
import com.franquicias.service.FranquiciaService;
import com.franquicias.service.ProductoService;
import com.franquicias.service.SucursalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/franquicias")
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
    public ResponseEntity<FranquiciaResponse> addFranquicia(@Valid @RequestBody CreateFranquiciaRequest request) {
        FranquiciaResponse franquicia = franquiciaService.createFranquicia(request.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(franquicia);
    }

    @PostMapping("/{franquiciaId}/sucursales")
    public ResponseEntity<SucursalResponse> addSucursal(
            @PathVariable Long franquiciaId,
            @Valid @RequestBody CreateSucursalRequest request) {
        SucursalResponse sucursal = sucursalService.createSucursal(
                franquiciaId,
                request.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(sucursal);
    }

    @GetMapping("/{franquiciaId}/productos-max-stock")
    public ResponseEntity<List<ProductoMaxStockPorSucursalResponse>> getProductosConMaxStockPorSucursal(
            @PathVariable Long franquiciaId) {
        List<ProductoMaxStockPorSucursalResponse> productos = productoService.getProductosConMaxStockPorSucursal(franquiciaId);
        return ResponseEntity.ok(productos);
    }
}
