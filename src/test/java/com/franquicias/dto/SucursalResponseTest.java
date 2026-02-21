package com.franquicias.dto;

import com.franquicias.entity.Franquicia;
import com.franquicias.entity.Sucursal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SucursalResponseTest {

    @Test
    @DisplayName("from maps entity to response")
    void fromMapsEntityToResponse() {
        Franquicia franquicia = new Franquicia();
        franquicia.setId(10L);
        Sucursal sucursal = new Sucursal();
        sucursal.setId(1L);
        sucursal.setName("Sucursal Centro");
        sucursal.setFranquicia(franquicia);

        SucursalResponse response = SucursalResponse.from(sucursal);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Sucursal Centro");
        assertThat(response.franquiciaId()).isEqualTo(10L);
    }
}
