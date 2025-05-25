package com.proyects.ferremasinventory.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductoInventoryDto {

    private Long id;
    private String marca;
    private String codigo;
    private String nombre;
    private int precio;

    @JsonProperty("stock_bodega")
    private int stockBodega;

    private int stock;
    private String descripcion;

    @JsonProperty("categoria_id")
    private Long categoriaId;

    @JsonProperty("fecha_creacion")
    private LocalDate fechaCreacion;
}
