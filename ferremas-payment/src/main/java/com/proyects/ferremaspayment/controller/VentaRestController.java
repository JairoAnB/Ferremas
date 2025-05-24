package com.proyects.ferremaspayment.controller;

import cl.transbank.common.IntegrationApiKeys;
import cl.transbank.common.IntegrationCommerceCodes;
import cl.transbank.common.IntegrationType;
import cl.transbank.webpay.common.WebpayOptions;
import cl.transbank.webpay.webpayplus.WebpayPlus;
import cl.transbank.webpay.webpayplus.responses.WebpayPlusTransactionCommitResponse;
import com.proyects.ferremaspayment.dto.TransaccionResponseDto;
import com.proyects.ferremaspayment.dto.VentaDto;
import com.proyects.ferremaspayment.dto.VentaRequestDto;
import com.proyects.ferremaspayment.dto.VentaResponseDto;
import com.proyects.ferremaspayment.model.Estado;
import com.proyects.ferremaspayment.model.Venta;
import com.proyects.ferremaspayment.repository.VentaRepository;
import com.proyects.ferremaspayment.service.PaymentGateway;
import com.proyects.ferremaspayment.service.VentaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/sales")
public class VentaRestController {

    private final VentaService ventaService;
    private final PaymentGateway paymentGateway;
    private final VentaRepository ventaRepository;

    @Autowired
    public VentaRestController(VentaService ventaService, PaymentGateway paymentGateway, VentaRepository ventaRepository) {
        this.ventaService = ventaService;
        this.paymentGateway = paymentGateway;
        this.ventaRepository = ventaRepository;
    }


    @Transactional
    @PostMapping("/create")
    public ResponseEntity<String> createVenta(@RequestBody VentaRequestDto ventaRequestDto){
        return ventaService.createVenta(ventaRequestDto);
    }

    @Transactional(readOnly = true)
    @GetMapping("/{id}")
    public ResponseEntity<VentaDto> getVentaById(@PathVariable Long id) {
        return ventaService.findVentaById(id);
    }

    @Transactional(readOnly = true)
    @GetMapping
    public ResponseEntity<List<VentaResponseDto>> getAllVentas() {
        return ventaService.findAllVentas();
    }

}
