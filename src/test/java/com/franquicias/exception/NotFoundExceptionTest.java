package com.franquicias.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NotFoundExceptionTest {

    @Test
    @DisplayName("creates exception with message")
    void createsWithMessage() {
        String message = "Resource not found: 123";
        NotFoundException ex = new NotFoundException(message);

        assertThat(ex).hasMessage(message);
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }
}
