package com.proyects.ferremaspayment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleVentaDto {

    private Long productoId;
    private String nombre;
    private int cantidad;
    private int precioUnitario;
    private int subTotal;

}
