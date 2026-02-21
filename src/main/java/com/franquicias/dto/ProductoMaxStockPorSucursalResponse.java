package com.franquicias.dto;

import com.franquicias.entity.Producto;

/**
 * Response for the product with highest stock in a branch (sucursal),
 * including branch identification.
 */
public record ProductoMaxStockPorSucursalResponse(
        Long productId,
        String productName,
        Integer stock,
        Long sucursalId,
        String sucursalName
) {
    public static ProductoMaxStockPorSucursalResponse from(Producto producto) {
        return new ProductoMaxStockPorSucursalResponse(
                producto.getId(),
                producto.getName(),
                producto.getStock(),
                producto.getSucursal().getId(),
                producto.getSucursal().getName()
        );
    }
}
