package com.proyects.ferremaspayment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VentaRequestDto {

    private Long usuarioId;
    private List<ItemVentaDto> productos;
    private String metodoPago;
    private String moneda;
}
