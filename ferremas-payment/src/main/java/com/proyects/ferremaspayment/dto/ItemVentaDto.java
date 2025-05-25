package com.proyects.ferremaspayment.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemVentaDto {

    private Long productoId;

    @NotNull @Min(value = 1, message = "La cantidad no puede ser menor a 1")
    private int cantidad;
}
