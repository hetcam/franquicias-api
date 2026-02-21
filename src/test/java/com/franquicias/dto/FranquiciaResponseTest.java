package com.franquicias.dto;

import com.franquicias.entity.Franquicia;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FranquiciaResponseTest {

    @Test
    @DisplayName("from maps entity to response")
    void fromMapsEntityToResponse() {
        Franquicia franquicia = new Franquicia();
        franquicia.setId(1L);
        franquicia.setName("Test Franquicia");

        FranquiciaResponse response = FranquiciaResponse.from(franquicia);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Test Franquicia");
    }
}
