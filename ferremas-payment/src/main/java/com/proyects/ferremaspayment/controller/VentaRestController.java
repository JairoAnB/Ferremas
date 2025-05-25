package com.proyects.ferremaspayment.controller;

import com.proyects.ferremaspayment.dto.VentaDto;
import com.proyects.ferremaspayment.dto.VentaRequestDto;
import com.proyects.ferremaspayment.dto.VentaResponseDto;
import com.proyects.ferremaspayment.service.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sales")
public class VentaRestController {

    @Autowired
    private final VentaService ventaService;

    @Transactional
    @PostMapping("/create")
    public ResponseEntity<String> createVenta(@Valid @RequestBody VentaRequestDto ventaRequestDto){
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
