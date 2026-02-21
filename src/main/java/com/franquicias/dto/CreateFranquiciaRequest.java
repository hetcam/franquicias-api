package com.franquicias.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateFranquiciaRequest {

    @NotBlank(message = "Name is required")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
