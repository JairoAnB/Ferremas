package com.proyects.ferremaspayment.dto;

import com.proyects.ferremaspayment.model.Estado;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VentaDto {

    private Long id;
    private Long usuarioId;
    private String buyOrder;
    private String metodoPago;
    private String moneda;
    private Estado estado;
    private int total;
    private LocalDateTime fechaVenta;
    private List<DetalleVentaDto> detalle;


}
