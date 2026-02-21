package com.franquicias.repository;

import com.franquicias.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findBySucursalId(Long sucursalId);

    boolean existsByIdAndSucursalId(Long productoId, Long sucursalId);

    Optional<Producto> findFirstBySucursalIdOrderByStockDesc(Long sucursalId);
}
