package com.franquicias.dto;

import com.franquicias.entity.Producto;

public record ProductoResponse(Long id, String name, Integer stock, Long sucursalId) {

    public static ProductoResponse from(Producto producto) {
        return new ProductoResponse(
                producto.getId(),
                producto.getName(),
                producto.getStock(),
                producto.getSucursal().getId());
    }
}
