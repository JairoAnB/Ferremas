package com.proyects.ferremaspayment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoDto {

    private String marca;
    private String codigo;
    private String nombre;
    private int precio;
    private int stock;
    private String descripcion;
    private Long categoriaId;
    private LocalDate fechaCreacion;

}
