package com.franquicias.service;

import com.franquicias.dto.SucursalResponse;
import com.franquicias.entity.Franquicia;
import com.franquicias.entity.Sucursal;
import com.franquicias.exception.NotFoundException;
import com.franquicias.repository.SucursalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SucursalServiceTest {

    @Mock
    private SucursalRepository sucursalRepository;

    @Mock
    private FranquiciaService franquiciaService;

    @InjectMocks
    private SucursalService sucursalService;

    @Nested
    @DisplayName("createSucursal")
    class CreateSucursal {

        @Test
        @DisplayName("creates sucursal when franquicia exists")
        void createsSucursalWhenFranquiciaExists() {
            Long franquiciaId = 1L;
            String name = "Sucursal Centro";
            String address = "Calle Principal 123";

            Franquicia franquicia = new Franquicia();
            franquicia.setId(franquiciaId);
            franquicia.setName("Franquicia");
            franquicia.setSucursales(new ArrayList<>());

            Sucursal saved = new Sucursal();
            saved.setId(1L);
            saved.setName(name);
            saved.setFranquicia(franquicia);

            when(franquiciaService.findById(franquiciaId)).thenReturn(franquicia);
            when(sucursalRepository.save(any(Sucursal.class))).thenReturn(saved);

            SucursalResponse result = sucursalService.createSucursal(franquiciaId, name, address);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo(name);
            assertThat(result.franquiciaId()).isEqualTo(franquiciaId);
            assertThat(franquicia.getSucursales()).contains(saved);
            verify(franquiciaService).findById(franquiciaId);
            verify(sucursalRepository).save(any(Sucursal.class));
        }

        @Test
        @DisplayName("throws NotFoundException when franquicia does not exist")
        void throwsNotFoundExceptionWhenFranquiciaNotExists() {
            Long franquiciaId = 999L;
            when(franquiciaService.findById(franquiciaId))
                    .thenThrow(new NotFoundException("Franquicia not found: 999"));

            assertThatThrownBy(() -> sucursalService.createSucursal(franquiciaId, "Name", "Address"))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Franquicia not found: 999");
            verify(franquiciaService).findById(franquiciaId);
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("returns sucursal when exists")
        void returnsSucursalWhenExists() {
            Long id = 1L;
            Sucursal sucursal = new Sucursal();
            sucursal.setId(id);
            sucursal.setName("Sucursal");

            when(sucursalRepository.findById(id)).thenReturn(Optional.of(sucursal));

            Sucursal result = sucursalService.findById(id);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(id);
            assertThat(result.getName()).isEqualTo("Sucursal");
            verify(sucursalRepository).findById(id);
        }

        @Test
        @DisplayName("throws NotFoundException when sucursal does not exist")
        void throwsNotFoundExceptionWhenNotExists() {
            Long id = 999L;
            when(sucursalRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sucursalService.findById(id))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Sucursal not found: 999");
            verify(sucursalRepository).findById(id);
        }
    }
}
