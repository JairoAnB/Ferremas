package com.proyects.ferremaspayment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyects.ferremaspayment.dto.CreateSaleResponse;
import com.proyects.ferremaspayment.dto.ItemVentaDto;
import com.proyects.ferremaspayment.dto.VentaDto;
import com.proyects.ferremaspayment.dto.VentaRequestDto;
import com.proyects.ferremaspayment.dto.VentaResponseDto;
import com.proyects.ferremaspayment.service.VentaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VentaRestController.class)
class VentaRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VentaService ventaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Debería crear una venta y retornar estado 201 Created")
    void deberiaCrearUnaVenta() throws Exception {
        VentaRequestDto requestDto = new VentaRequestDto();
        requestDto.setUsuarioId(1L);
        requestDto.setProductos(Collections.singletonList(new ItemVentaDto(101L, 2)));
        requestDto.setMetodoPago("paypal");
        requestDto.setMoneda("dolar");

        CreateSaleResponse responseDto = new CreateSaleResponse();
        responseDto.setId(1L);
        responseDto.setMessage("Venta creada correctamente...");
        responseDto.setStatus(HttpStatus.CREATED);
        responseDto.setTimestamp(LocalDateTime.now());

        when(ventaService.createVenta(any(VentaRequestDto.class)))
                .thenReturn(new ResponseEntity<>(responseDto, HttpStatus.CREATED));

        mockMvc.perform(post("/api/v1/sales/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.message", is(responseDto.getMessage())));
    }

    @Test
    @DisplayName("Debería retornar una venta por su ID y estado 200 OK")
    void deberiaObtenerVentaPorId() throws Exception {
        long ventaId = 1L;
        VentaDto ventaDto = new VentaDto();
        ventaDto.setId(ventaId);
        ventaDto.setTotal(50000);

        when(ventaService.findVentaById(ventaId))
                .thenReturn(ResponseEntity.ok(ventaDto));

        mockMvc.perform(get("/api/v1/sales/{id}", ventaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.total", is(50000)));
    }

    @Test
    @DisplayName("Debería retornar una lista de todas las ventas y estado 200 OK")
    void deberiaObtenerTodasLasVentas() throws Exception {
        VentaResponseDto ventaResponse = new VentaResponseDto();
        ventaResponse.setId(1L);
        List<VentaResponseDto> allVentas = Collections.singletonList(ventaResponse);

        when(ventaService.findAllVentas())
                .thenReturn(ResponseEntity.ok(allVentas));

        mockMvc.perform(get("/api/v1/sales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }
}