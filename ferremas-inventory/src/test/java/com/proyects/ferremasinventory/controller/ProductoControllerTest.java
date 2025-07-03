package com.proyects.ferremasinventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyects.ferremasinventory.dto.ProductResponseDto;
import com.proyects.ferremasinventory.dto.ProductoCreateDto;
import com.proyects.ferremasinventory.dto.ProductoDto;
import com.proyects.ferremasinventory.dto.ProductoUpdateDto;
import com.proyects.ferremasinventory.dto.ProductoUpdateStockDto;
import com.proyects.ferremasinventory.service.ProductoService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductoController.class)
public class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductoService productoService;

    @Test
    @DisplayName("Debería devolver una lista de productos y estado 200 OK")
    void deberiaDevolverTodosLosProductos() throws Exception {
        ProductoDto productoDto = new ProductoDto();
        productoDto.setId(1L);
        productoDto.setNombre("Taladro");

        when(productoService.findAllProductos()).thenReturn(ResponseEntity.ok(List.of(productoDto)));

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("Taladro"));
    }

    @Test
    @DisplayName("Debería devolver estado 204 No Content si no hay productos")
    void deberiaDevolverNoContentSiNoHayProductos() throws Exception {
        when(productoService.findAllProductos()).thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Debería devolver un producto por su ID y estado 200 OK")
    void deberiaDevolverProductoPorId() throws Exception {
        Long productoId = 1L;
        ProductoDto productoDto = new ProductoDto();
        productoDto.setId(productoId);

        when(productoService.findProductoById(productoId)).thenReturn(ResponseEntity.ok(productoDto));

        mockMvc.perform(get("/api/v1/products/{id}", productoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productoId));
    }

    @Test
    @DisplayName("Debería crear un producto y devolver estado 201 Created")
    void deberiaCrearUnProducto() throws Exception {
        ProductoCreateDto createDto = new ProductoCreateDto();
        createDto.setNombre("Sierra Circular");
        createDto.setMarca("Makita");
        createDto.setCodigo("SKU-MAK-5007");
        createDto.setPrecio(150000);
        createDto.setStock(10);
        createDto.setStockBodega(20);
        createDto.setDescripcion("Sierra circular de 7-1/4 pulgadas.");
        createDto.setCategoriaId(1L);

        ProductResponseDto responseDto = new ProductResponseDto();
        responseDto.setMessage("Producto creado");

        when(productoService.createProducto(any(ProductoCreateDto.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(responseDto));

        mockMvc.perform(post("/api/v1/products/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Producto creado"));
    }

    @Test
    @DisplayName("Debería actualizar un producto y devolver estado 200 OK")
    void deberiaActualizarUnProducto() throws Exception {
        Long productoId = 1L;

        // SOLUCIÓN: Crear un DTO completo y válido
        ProductoUpdateDto updateDto = new ProductoUpdateDto();
        updateDto.setNombre("Sierra de Calar Profesional");
        updateDto.setMarca("Bosch");
        updateDto.setCodigo("SKU-12345");
        updateDto.setPrecio(50000);
        updateDto.setStock(10);
        updateDto.setDescripcion("Sierra de alta potencia para uso profesional.");
        updateDto.setFechaCreacion(java.time.LocalDate.now());
        updateDto.setCategoriaId(1L);

        ProductResponseDto responseDto = new ProductResponseDto();
        responseDto.setMessage("Producto actualizado");

        when(productoService.updateProducto(anyLong(), any(ProductoUpdateDto.class)))
                .thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(put("/api/v1/products/update/{id}", productoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Producto actualizado"));
    }

    @Test
    @DisplayName("Debería actualizar el stock de un producto y devolver estado 200 OK")
    void deberiaActualizarElStockDeUnProducto() throws Exception {
        Long productoId = 1L;
        int nuevoStock = 50;
        ProductoUpdateStockDto stockDto = new ProductoUpdateStockDto();
        stockDto.setStock(nuevoStock);

        when(productoService.updateStock(anyLong(), anyInt())).thenReturn(ResponseEntity.ok(stockDto));

        mockMvc.perform(put("/api/v1/products/stock/{id}/update/{stock}", productoId, nuevoStock))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(nuevoStock));
    }

    @Test
    @DisplayName("Debería devolver el stock de un producto por su ID y estado 200 OK")
    void deberiaDevolverElStockPorId() throws Exception {
        Long productoId = 1L;
        ProductoUpdateStockDto stockDto = new ProductoUpdateStockDto();
        stockDto.setStock(100);

        when(productoService.findProductosStockById(productoId)).thenReturn(ResponseEntity.ok(stockDto));

        mockMvc.perform(get("/api/v1/products/stock/{id}", productoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(100));
    }

    @Test
    @DisplayName("Debería eliminar un producto y devolver estado 200 OK")
    void deberiaEliminarUnProducto() throws Exception {
        Long productoId = 1L;
        ProductResponseDto responseDto = new ProductResponseDto();
        responseDto.setMessage("Producto eliminado");

        when(productoService.deleteProducto(productoId)).thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(delete("/api/v1/products/delete/{id}", productoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Producto eliminado"));
    }
}