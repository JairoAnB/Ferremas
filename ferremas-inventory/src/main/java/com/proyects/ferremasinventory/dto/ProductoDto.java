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

    @JsonProperty("Codigo del producto")
    @NotBlank(message = "El codigo del producto no puede estar vacio")
    private String codigoProducto;

    @JsonProperty("Marca")
    @NotBlank
    private String marca;

    @JsonProperty("Codigo")
    @NotBlank(message = "El codigo no puede estar vacio")
    private String codigo;

    @NotBlank(message = "El nombre no puede estar vacio")
    @JsonProperty("Nombre")
    private String nombre;

    @NotNull(message = "El precio no puede ser nulo")
    @JsonProperty("Precio del producto")
    private int precio;

    @NotNull(message = "El stock no puede ser nulo")
    @JsonProperty("Stock")
    private int stock;

    @JsonProperty("Descripcion")
    private String descripcion;

    @NotNull(message = "El id de la categoria no puede ser nulo")
    @JsonProperty("Id de la categoria")
    private Long categoriaId;

    @NotNull(message = "La fecha de creacion no puede ser nula")
    @JsonProperty("Fecha de creacion")
    private LocalDate fechaCreacion;

}
