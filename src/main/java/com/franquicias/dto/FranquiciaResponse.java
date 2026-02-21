package com.franquicias.dto;

import com.franquicias.entity.Franquicia;

public record FranquiciaResponse(Long id, String name) {

    public static FranquiciaResponse from(Franquicia franquicia) {
        return new FranquiciaResponse(franquicia.getId(), franquicia.getName());
    }
}
