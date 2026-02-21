package com.franquicias.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateSucursalRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
