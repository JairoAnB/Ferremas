package com.proyects.ferremasinventory.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoDto {

    //JSONPROPETY es el nombre que se va a usar en el json, y las propiedades de
    //Notblank, NotNull son para validar los datos que se van a recibir
    //Hay que tener cuenta la redudancia entre las validaciones


    @NotBlank
    private String marca;

    @NotBlank
    private String codigo;

    @NotBlank
    private String nombre;

    @NotNull
    private int precio;

    @NotNull
    private int stock;

    private String descripcion;

    @NotNull
    @JsonProperty("categoria_id")
    private Long categoriaId;

    @JsonProperty("fecha_creacion")
    private LocalDate fechaCreacion;
}
