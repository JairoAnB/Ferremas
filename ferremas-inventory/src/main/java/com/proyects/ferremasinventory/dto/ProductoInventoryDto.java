package com.proyects.ferremasinventory.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductoInventoryDto {

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
