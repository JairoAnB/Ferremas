package com.proyects.ferremascategory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaUpdateDto {

    @NotBlank (message = "El nombre no puede estar vacío")
    private String nombre;

    @NotBlank (message = "La descripcion no puede estar vacía")
    private String descripcion;
    private String imagen;
}
