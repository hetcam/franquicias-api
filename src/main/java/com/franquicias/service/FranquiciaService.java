package com.franquicias.service;

import com.franquicias.dto.FranquiciaResponse;
import com.franquicias.entity.Franquicia;
import com.franquicias.exception.NotFoundException;
import com.franquicias.repository.FranquiciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FranquiciaService {

    private final FranquiciaRepository franquiciaRepository;

    public FranquiciaService(FranquiciaRepository franquiciaRepository) {
        this.franquiciaRepository = franquiciaRepository;
    }

    @Transactional
    public FranquiciaResponse createFranquicia(String name) {
        Franquicia franquicia = new Franquicia();
        franquicia.setName(name);
        franquicia = franquiciaRepository.save(franquicia);
        return FranquiciaResponse.from(franquicia);
    }

    public Franquicia findById(Long id) {
        return franquiciaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Franquicia not found: " + id));
    }
}
