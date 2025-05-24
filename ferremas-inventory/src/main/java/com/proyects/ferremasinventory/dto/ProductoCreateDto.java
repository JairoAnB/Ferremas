package com.proyects.ferremasinventory.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoCreateDto {

    private String marca;

    @NotBlank(message = "El campo de codigo es obligatorio")
    private String codigo;

    @NotBlank(message = "El campo de nombre es obligatorio")
    private String nombre;

    @NotNull @Min(value = 1000, message = "La cantidad debe ser al menos de $1000") @Max(value = 9999999, message = "El precio no puede ser mayor a $9999999")
    private int precio;

    @NotNull @Min(value = 1, message = "El stock no puede ser inferior a 1") @Max(value = 9999, message = "El stock no puede ser mayor a 9999")
    private int stock;

    @NotNull @Min(value = 1, message = "El stock de bodega no puede ser inferior a 1") @Max(value = 9999, message = "El stock de bodega no puede ser mayor a 9999")
    @JsonProperty("stock_bodega")
    private int StockBodega;

    private String descripcion;

    @NotNull(message = "El campo de categoria es obligatorio")
    @JsonProperty("categoria_id")
    private Long categoriaId;
}
