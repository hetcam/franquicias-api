package com.franquicias.service;

import com.franquicias.dto.SucursalResponse;
import com.franquicias.entity.Franquicia;
import com.franquicias.entity.Sucursal;
import com.franquicias.exception.NotFoundException;
import com.franquicias.repository.SucursalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SucursalService {

    private final SucursalRepository sucursalRepository;
    private final FranquiciaService franquiciaService;

    public SucursalService(SucursalRepository sucursalRepository, FranquiciaService franquiciaService) {
        this.sucursalRepository = sucursalRepository;
        this.franquiciaService = franquiciaService;
    }

    @Transactional
    public SucursalResponse createSucursal(Long franquiciaId, String name) {
        Franquicia franquicia = franquiciaService.findById(franquiciaId);
        Sucursal sucursal = new Sucursal();
        sucursal.setName(name);
        sucursal.setFranquicia(franquicia);
        sucursal = sucursalRepository.save(sucursal);
        franquicia.getSucursales().add(sucursal);
        return SucursalResponse.from(sucursal);
    }

    @Transactional
    public SucursalResponse updateSucursalName(Long franquiciaId, Long sucursalId, String name) {
        franquiciaService.findById(franquiciaId);
        Sucursal sucursal = findById(sucursalId);
        if (!sucursal.getFranquicia().getId().equals(franquiciaId)) {
            throw new NotFoundException("Sucursal does not belong to franquicia: " + franquiciaId);
        }
        sucursal.setName(name);
        sucursal = sucursalRepository.save(sucursal);
        return SucursalResponse.from(sucursal);
    }

    public Sucursal findById(Long id) {
        return sucursalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sucursal not found: " + id));
    }

    public List<Sucursal> findByFranquiciaId(Long franquiciaId) {
        return sucursalRepository.findByFranquiciaId(franquiciaId);
    }
}
