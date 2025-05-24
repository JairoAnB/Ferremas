package com.proyects.ferremaspayment.controller;

import com.proyects.ferremaspayment.dto.CurrencyDto;
import com.proyects.ferremaspayment.dto.VentaDto;
import com.proyects.ferremaspayment.dto.VentaResponseDto;
import com.proyects.ferremaspayment.repository.VentaRepository;
import com.proyects.ferremaspayment.service.PaymentClient;
import com.proyects.ferremaspayment.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor
public class CambioController {

    private final PaymentClient paymentClient;
    private final VentaRepository ventaRepository;
    private final VentaService ventaService;


    public String realizarCambio(Long id, Long idVenta, String moneda, Model model) {
        ResponseEntity<VentaDto> ventaResponseDto = ventaService.findVentaById(id);
        VentaDto ventaDto = ventaResponseDto.getBody();

        BigDecimal totalOriginal = BigDecimal.valueOf(ventaDto.getTotal());
        moneda = ventaDto.getMoneda();

        CurrencyDto cambio = paymentClient.obtenerCambio(moneda, LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        if (cambio.getSerie().isEmpty()) {
            cambio = paymentClient.obtenerCambio(moneda, LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        }

        BigDecimal valorMoneda = cambio.getSerie().get(0).getValor();
        BigDecimal totalCLP;

        if ("dolar".equalsIgnoreCase(moneda) || "usd".equalsIgnoreCase(moneda)) {
            totalCLP = totalOriginal.multiply(valorMoneda);
            model.addAttribute("moneda", "USD");
        } else if ("euro".equalsIgnoreCase(moneda) || "eur".equalsIgnoreCase(moneda)) {
            totalCLP = totalOriginal.multiply(valorMoneda);
            model.addAttribute("moneda", "EUR");
        } else {
            totalCLP = totalOriginal;
            valorMoneda = BigDecimal.ONE;
            model.addAttribute("moneda", "CLP");
        }

        model.addAttribute("totalOriginal", totalOriginal);
        model.addAttribute("valorMoneda", valorMoneda);
        model.addAttribute("totalConvertido", totalCLP);

        return "pago";
    }


}
