package com.franquicias.dto;

import com.franquicias.entity.Sucursal;

public record SucursalResponse(Long id, String name, Long franquiciaId) {

    public static SucursalResponse from(Sucursal sucursal) {
        return new SucursalResponse(
                sucursal.getId(),
                sucursal.getName(),
                sucursal.getFranquicia().getId());
    }
}
