package com.franquicias.service;

import com.franquicias.dto.ProductoMaxStockPorSucursalResponse;
import com.franquicias.dto.ProductoResponse;
import com.franquicias.entity.Producto;
import com.franquicias.entity.Sucursal;
import com.franquicias.exception.NotFoundException;
import com.franquicias.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final SucursalService sucursalService;
    private final FranquiciaService franquiciaService;

    public ProductoService(ProductoRepository productoRepository,
                           SucursalService sucursalService,
                           FranquiciaService franquiciaService) {
        this.productoRepository = productoRepository;
        this.sucursalService = sucursalService;
        this.franquiciaService = franquiciaService;
    }

    @Transactional
    public ProductoResponse createProducto(Long sucursalId, String name, String description, Integer stock) {
        Sucursal sucursal = sucursalService.findById(sucursalId);
        Producto producto = new Producto();
        producto.setName(name);
        producto.setStock(stock != null ? stock : 0);
        producto.setSucursal(sucursal);
        producto = productoRepository.save(producto);
        sucursal.getProductos().add(producto);
        return ProductoResponse.from(producto);
    }

    @Transactional
    public void deleteProductoFromSucursal(Long sucursalId, Long productoId) {
        if (!productoRepository.existsByIdAndSucursalId(productoId, sucursalId)) {
            throw new NotFoundException("Producto not found in sucursal: " + productoId);
        }
        productoRepository.deleteById(productoId);
    }

    @Transactional
    public ProductoResponse updateStock(Long sucursalId, Long productoId, Integer stock) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new NotFoundException("Producto not found: " + productoId));
        if (!producto.getSucursal().getId().equals(sucursalId)) {
            throw new NotFoundException("Producto does not belong to sucursal: " + sucursalId);
        }
        if (stock != null && stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        producto.setStock(stock != null ? stock : producto.getStock());
        producto = productoRepository.save(producto);
        return ProductoResponse.from(producto);
    }

    @Transactional
    public ProductoResponse updateProductoName(Long sucursalId, Long productoId, String name) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new NotFoundException("Producto not found: " + productoId));
        if (!producto.getSucursal().getId().equals(sucursalId)) {
            throw new NotFoundException("Producto does not belong to sucursal: " + sucursalId);
        }
        producto.setName(name);
        producto = productoRepository.save(producto);
        return ProductoResponse.from(producto);
    }

    /**
     * Returns the product with the highest stock per branch for the given franchise.
     * Branches with no products are omitted from the result.
     */
    @Transactional(readOnly = true)
    public List<ProductoMaxStockPorSucursalResponse> getProductosConMaxStockPorSucursal(Long franquiciaId) {
        franquiciaService.findById(franquiciaId);
        List<Sucursal> sucursales = sucursalService.findByFranquiciaId(franquiciaId);
        return sucursales.stream()
                .map(sucursal -> productoRepository.findFirstBySucursalIdOrderByStockDesc(sucursal.getId()))
                .filter(java.util.Optional::isPresent)
                .map(opt -> ProductoMaxStockPorSucursalResponse.from(opt.get()))
                .toList();
    }
}
