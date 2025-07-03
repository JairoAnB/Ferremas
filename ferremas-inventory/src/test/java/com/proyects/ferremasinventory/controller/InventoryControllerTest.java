package com.proyects.ferremasinventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyects.ferremasinventory.dto.ProductResponseDto;
import com.proyects.ferremasinventory.dto.ProductoInventoryDto;
import com.proyects.ferremasinventory.dto.RequestStockDto;
import com.proyects.ferremasinventory.service.InventoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryController.class)
public class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InventoryService inventoryService;

    @Test
    @DisplayName("Debería devolver todos los inventarios de productos y estado 200 OK")
    void deberiaDevolverTodosLosInventarios() throws Exception {
        ProductoInventoryDto inventoryDto = new ProductoInventoryDto();
        inventoryDto.setId(1L);
        inventoryDto.setNombre("Martillo");

        when(inventoryService.findAllProductosInventory()).thenReturn(ResponseEntity.ok(List.of(inventoryDto)));

        mockMvc.perform(get("/api/v1/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("Martillo"));
    }

    @Test
    @DisplayName("Debería devolver estado 204 No Content si no hay inventarios")
    void deberiaDevolverNoContentSiNoHayInventarios() throws Exception {
        when(inventoryService.findAllProductosInventory()).thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(get("/api/v1/inventory"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Debería devolver un inventario por su ID y estado 200 OK")
    void deberiaDevolverInventarioPorId() throws Exception {
        Long productoId = 1L;
        ProductoInventoryDto inventoryDto = new ProductoInventoryDto();
        inventoryDto.setId(productoId);

        when(inventoryService.findProductoInventorybyId(productoId)).thenReturn(ResponseEntity.ok(inventoryDto));

        mockMvc.perform(get("/api/v1/inventory/{id}", productoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productoId));
    }

    @Test
    @DisplayName("Debería actualizar el inventario de un producto y estado 200 OK")
    void deberiaActualizarInventario() throws Exception {
        Long productoId = 1L;
        int nuevoStock = 100;
        ProductoInventoryDto inventoryDto = new ProductoInventoryDto();
        inventoryDto.setId(productoId);
        inventoryDto.setStockBodega(nuevoStock);

        when(inventoryService.updateInventory(anyLong(), anyInt())).thenReturn(ResponseEntity.ok(inventoryDto));

        mockMvc.perform(put("/api/v1/inventory/update/{id}/{stock}", productoId, nuevoStock))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productoId))
                // SOLUCIÓN: Cambiar "stockBodega" a "stock_bodega"
                .andExpect(jsonPath("$.stock_bodega").value(nuevoStock));
    }

    @Test
    @DisplayName("Debería transferir stock de un producto y estado 200 OK")
    void deberiaTransferirStock() throws Exception {
        Long productoId = 1L;
        RequestStockDto requestDto = new RequestStockDto();
        requestDto.setCantidad(10);

        ProductResponseDto responseDto = new ProductResponseDto();
        responseDto.setMessage("Transferencia exitosa");

        when(inventoryService.transferirStock(anyLong(), any(RequestStockDto.class)))
                .thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(put("/api/v1/inventory/transfer/{id}", productoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transferencia exitosa"));
    }

    @Test
    @DisplayName("Debería devolver Bad Request al transferir si la lógica de negocio falla")
    void deberiaDevolverBadRequestAlTransferir() throws Exception {
        Long productoId = 1L;
        RequestStockDto requestDto = new RequestStockDto();
        requestDto.setCantidad(100);

        ProductResponseDto responseDto = new ProductResponseDto();
        responseDto.setMessage("Stock insuficiente");

        when(inventoryService.transferirStock(anyLong(), any(RequestStockDto.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto));

        mockMvc.perform(put("/api/v1/inventory/transfer/{id}", productoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Stock insuficiente"));
    }
}