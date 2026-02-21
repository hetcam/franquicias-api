package com.franquicias.service;

import com.franquicias.dto.ProductoResponse;
import com.franquicias.entity.Producto;
import com.franquicias.entity.Sucursal;
import com.franquicias.exception.NotFoundException;
import com.franquicias.repository.ProductoRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private SucursalService sucursalService;

    @InjectMocks
    private ProductoService productoService;

    @Nested
    @DisplayName("createProducto")
    class CreateProducto {

        @Test
        @DisplayName("creates producto when sucursal exists")
        void createsProductoWhenSucursalExists() {
            Long sucursalId = 1L;
            String name = "Producto A";
            String description = "Desc";
            Integer stock = 10;

            Sucursal sucursal = new Sucursal();
            sucursal.setId(sucursalId);
            sucursal.setProductos(new ArrayList<>());

            Producto saved = new Producto();
            saved.setId(1L);
            saved.setName(name);
            saved.setStock(stock);
            saved.setSucursal(sucursal);

            when(sucursalService.findById(sucursalId)).thenReturn(sucursal);
            when(productoRepository.save(any(Producto.class))).thenReturn(saved);

            ProductoResponse result = productoService.createProducto(sucursalId, name, description, stock);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo(name);
            assertThat(result.stock()).isEqualTo(stock);
            assertThat(result.sucursalId()).isEqualTo(sucursalId);
            assertThat(sucursal.getProductos()).contains(saved);
            verify(sucursalService).findById(sucursalId);
            verify(productoRepository).save(any(Producto.class));
        }

        @Test
        @DisplayName("defaults stock to 0 when null")
        void defaultsStockToZeroWhenNull() {
            Long sucursalId = 1L;
            Sucursal sucursal = new Sucursal();
            sucursal.setId(sucursalId);
            sucursal.setProductos(new ArrayList<>());

            Producto saved = new Producto();
            saved.setId(1L);
            saved.setName("P");
            saved.setStock(0);
            saved.setSucursal(sucursal);

            when(sucursalService.findById(sucursalId)).thenReturn(sucursal);
            when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> {
                Producto p = inv.getArgument(0);
                p.setId(1L);
                return p;
            });

            ProductoResponse result = productoService.createProducto(sucursalId, "P", null, null);

            assertThat(result.stock()).isEqualTo(0);
        }

        @Test
        @DisplayName("throws NotFoundException when sucursal does not exist")
        void throwsNotFoundExceptionWhenSucursalNotExists() {
            Long sucursalId = 999L;
            when(sucursalService.findById(sucursalId))
                    .thenThrow(new NotFoundException("Sucursal not found: 999"));

            assertThatThrownBy(() -> productoService.createProducto(sucursalId, "P", "D", 0))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Sucursal not found: 999");
        }
    }

    @Nested
    @DisplayName("deleteProductoFromSucursal")
    class DeleteProductoFromSucursal {

        @Test
        @DisplayName("deletes producto when it exists in sucursal")
        void deletesWhenExistsInSucursal() {
            Long sucursalId = 1L;
            Long productoId = 2L;
            when(productoRepository.existsByIdAndSucursalId(productoId, sucursalId)).thenReturn(true);

            productoService.deleteProductoFromSucursal(sucursalId, productoId);

            verify(productoRepository).existsByIdAndSucursalId(productoId, sucursalId);
            verify(productoRepository).deleteById(productoId);
        }

        @Test
        @DisplayName("throws NotFoundException when producto not in sucursal")
        void throwsNotFoundExceptionWhenNotInSucursal() {
            Long sucursalId = 1L;
            Long productoId = 999L;
            when(productoRepository.existsByIdAndSucursalId(productoId, sucursalId)).thenReturn(false);

            assertThatThrownBy(() -> productoService.deleteProductoFromSucursal(sucursalId, productoId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Producto not found in sucursal: 999");
            verify(productoRepository).existsByIdAndSucursalId(productoId, sucursalId);
            verify(productoRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("updateStock")
    class UpdateStock {

        @Test
        @DisplayName("updates stock when producto belongs to sucursal")
        void updatesStockWhenProductoInSucursal() {
            Long sucursalId = 1L;
            Long productoId = 2L;
            Integer newStock = 50;

            Sucursal sucursal = new Sucursal();
            sucursal.setId(sucursalId);
            Producto producto = new Producto();
            producto.setId(productoId);
            producto.setName("P");
            producto.setStock(10);
            producto.setSucursal(sucursal);

            when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));
            when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

            ProductoResponse result = productoService.updateStock(sucursalId, productoId, newStock);

            assertThat(result).isNotNull();
            assertThat(result.stock()).isEqualTo(newStock);
            assertThat(producto.getStock()).isEqualTo(newStock);
            verify(productoRepository).findById(productoId);
            verify(productoRepository).save(producto);
        }

        @Test
        @DisplayName("keeps current stock when new stock is null")
        void keepsCurrentStockWhenNull() {
            Long sucursalId = 1L;
            Long productoId = 2L;
            Sucursal sucursal = new Sucursal();
            sucursal.setId(sucursalId);
            Producto producto = new Producto();
            producto.setId(productoId);
            producto.setStock(25);
            producto.setSucursal(sucursal);

            when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));
            when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

            ProductoResponse result = productoService.updateStock(sucursalId, productoId, null);

            assertThat(result.stock()).isEqualTo(25);
            assertThat(producto.getStock()).isEqualTo(25);
        }

        @Test
        @DisplayName("throws NotFoundException when producto does not exist")
        void throwsNotFoundExceptionWhenProductoNotExists() {
            Long productoId = 999L;
            when(productoRepository.findById(productoId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productoService.updateStock(1L, productoId, 10))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Producto not found: 999");
        }

        @Test
        @DisplayName("throws NotFoundException when producto does not belong to sucursal")
        void throwsNotFoundExceptionWhenProductoNotInSucursal() {
            Long sucursalId = 1L;
            Long otherSucursalId = 2L;
            Long productoId = 2L;
            Sucursal otherSucursal = new Sucursal();
            otherSucursal.setId(otherSucursalId);
            Producto producto = new Producto();
            producto.setId(productoId);
            producto.setSucursal(otherSucursal);

            when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));

            assertThatThrownBy(() -> productoService.updateStock(sucursalId, productoId, 10))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Producto does not belong to sucursal: 1");
            verify(productoRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws IllegalArgumentException when stock is negative")
        void throwsIllegalArgumentExceptionWhenStockNegative() {
            Long sucursalId = 1L;
            Long productoId = 2L;
            Sucursal sucursal = new Sucursal();
            sucursal.setId(sucursalId);
            Producto producto = new Producto();
            producto.setId(productoId);
            producto.setSucursal(sucursal);

            when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));

            assertThatThrownBy(() -> productoService.updateStock(sucursalId, productoId, -5))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Stock cannot be negative");
            verify(productoRepository, never()).save(any());
        }
    }
}
