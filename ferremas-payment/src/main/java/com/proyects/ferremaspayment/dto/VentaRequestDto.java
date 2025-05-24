package com.proyects.ferremaspayment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VentaRequestDto {

    @NotNull (message = "El id del usuario no puede ser nulo y tiene que estar registrado en la base de datos")
    private Long usuarioId;
    @NotNull (message = "El id del carrito no puede ser nulo ")
    private List<ItemVentaDto> productos;

    @NotBlank(message = "El metodo de pago no puede estar vacio")
    private String metodoPago;

    @NotBlank (message = "El tipo de moneda no puede ser nulo, los tipos de moneda disponibles son: 'dolar', 'euro', 'pesos'")
    private String moneda;
}
