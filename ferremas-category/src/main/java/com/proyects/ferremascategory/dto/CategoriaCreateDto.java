package com.proyects.ferremascategory.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaCreateDto {

    @NotNull(message = "El nombre no puede ser nulo")
    private String nombre;

    @NotNull(message = "La descripcion no puede ser nula")
    private String descripcion;
    private String imagen;
}
