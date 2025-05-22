package com.proyects.ferremaspayment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagoResponseDto {

    private Boolean exitoso;
    private String codigoAutorizacion;
    private String mensaje;
    private int montoPagado;
    private String metodoPago;
    private String fechaPago;
}
