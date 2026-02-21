package com.franquicias.dto;

import com.franquicias.entity.Producto;
import com.franquicias.entity.Sucursal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductoResponseTest {

    @Test
    @DisplayName("from maps entity to response")
    void fromMapsEntityToResponse() {
        Sucursal sucursal = new Sucursal();
        sucursal.setId(5L);
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setName("Producto A");
        producto.setStock(100);
        producto.setSucursal(sucursal);

        ProductoResponse response = ProductoResponse.from(producto);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Producto A");
        assertThat(response.stock()).isEqualTo(100);
        assertThat(response.sucursalId()).isEqualTo(5L);
    }
}
