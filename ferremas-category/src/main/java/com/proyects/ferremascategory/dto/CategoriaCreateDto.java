package com.proyects.ferremascategory.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaCreateDto {

    @NotNull
    private String nombre;
    @NotNull
    private String descripcion;
    private String imagen;
}
