package com.proyects.ferremaspayment.dto;

import com.proyects.ferremaspayment.model.Venta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VentaResponseDto {

    public Venta venta;
    public String token;
    public String url;
}
