package com.proyects.ferremasinventory.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductoUpdateStockDto {
    @NotNull @Min(value = 1, message = "El stock no puede ser inferior a 1") @Max(value = 9999, message = "El stock no puede ser mayor a 9999")
    private int stock;
}
