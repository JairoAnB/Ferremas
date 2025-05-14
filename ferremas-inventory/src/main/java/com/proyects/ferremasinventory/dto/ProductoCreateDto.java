package com.proyects.ferremasinventory.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoCreateDto {

    private String marca;
    private String codigo;
    private String nombre;
    private int precio;
    private int stock;
    private String descripcion;

    @JsonProperty("categoria_id")
    private Long categoriaId;
}
