package com.franquicias.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public class UpdateStockRequest {

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
