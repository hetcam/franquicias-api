package com.franquicias.service;

import com.franquicias.dto.FranquiciaResponse;
import com.franquicias.entity.Franquicia;
import com.franquicias.exception.NotFoundException;
import com.franquicias.repository.FranquiciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranquiciaServiceTest {

    @Mock
    private FranquiciaRepository franquiciaRepository;

    @InjectMocks
    private FranquiciaService franquiciaService;

    @Nested
    @DisplayName("createFranquicia")
    class CreateFranquicia {

        @Test
        @DisplayName("creates franquicia and returns response")
        void createsFranquiciaAndReturnsResponse() {
            String name = "Franquicia Test";
            Franquicia saved = new Franquicia();
            saved.setId(1L);
            saved.setName(name);

            when(franquiciaRepository.save(any(Franquicia.class))).thenReturn(saved);

            FranquiciaResponse result = franquiciaService.createFranquicia(name);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo(name);
            verify(franquiciaRepository).save(any(Franquicia.class));
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("returns franquicia when exists")
        void returnsFranquiciaWhenExists() {
            Long id = 1L;
            Franquicia franquicia = new Franquicia();
            franquicia.setId(id);
            franquicia.setName("Test");

            when(franquiciaRepository.findById(id)).thenReturn(Optional.of(franquicia));

            Franquicia result = franquiciaService.findById(id);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(id);
            assertThat(result.getName()).isEqualTo("Test");
            verify(franquiciaRepository).findById(id);
        }

        @Test
        @DisplayName("throws NotFoundException when franquicia does not exist")
        void throwsNotFoundExceptionWhenNotExists() {
            Long id = 999L;
            when(franquiciaRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> franquiciaService.findById(id))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Franquicia not found: 999");
            verify(franquiciaRepository).findById(id);
        }
    }
}
