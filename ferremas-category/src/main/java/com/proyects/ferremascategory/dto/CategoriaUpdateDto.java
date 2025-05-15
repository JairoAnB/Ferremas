package com.proyects.ferremascategory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaUpdateDto {

    private String nombre;
    private String descripcion;
    private String imagen;
}
