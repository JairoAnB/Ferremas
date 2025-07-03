package com.proyects.ferremasinventory.service;

import com.proyects.ferremasinventory.dto.ProductResponseDto;
import com.proyects.ferremasinventory.dto.ProductoInventoryDto;
import com.proyects.ferremasinventory.dto.RequestStockDto;
import com.proyects.ferremasinventory.exceptions.ProductoNoEncontrado;
import com.proyects.ferremasinventory.mapper.ProductoMapper;
import com.proyects.ferremasinventory.model.Producto;
import com.proyects.ferremasinventory.repository.ProductoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock
    private ProductoMapper productoMapper;
    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    @DisplayName("Debería encontrar y devolver el inventario de un producto por su ID")
    void deberiaEncontrarInventarioPorId() {
        Long productoId = 1L;
        Producto producto = new Producto();
        producto.setId(productoId);
        ProductoInventoryDto productoInventoryDto = new ProductoInventoryDto();
        productoInventoryDto.setId(productoId);

        when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));
        when(productoMapper.toInventoryDto(producto)).thenReturn(productoInventoryDto);

        ResponseEntity<ProductoInventoryDto> response = inventoryService.findProductoInventorybyId(productoId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productoInventoryDto, response.getBody());
        verify(productoRepository).findById(productoId);
        verify(productoMapper).toInventoryDto(producto);
    }

    @Test
    @DisplayName("Debería lanzar ProductoNoEncontrado si el producto no existe al buscar por ID")
    void deberiaLanzarExcepcionAlBuscarInventarioSiProductoNoExiste() {
        Long productoId = 99L;
        when(productoRepository.findById(productoId)).thenReturn(Optional.empty());

        assertThrows(ProductoNoEncontrado.class, () -> {
            inventoryService.findProductoInventorybyId(productoId);
        });
    }

    @Test
    @DisplayName("Debería devolver una lista de todos los inventarios de productos")
    void deberiaDevolverTodosLosInventarios() {
        Producto producto = new Producto();
        ProductoInventoryDto productoInventoryDto = new ProductoInventoryDto();
        when(productoRepository.findAll()).thenReturn(List.of(producto));
        when(productoMapper.toInventoryDtoList(List.of(producto))).thenReturn(List.of(productoInventoryDto));

        ResponseEntity<List<ProductoInventoryDto>> response = inventoryService.findAllProductosInventory();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("Debería devolver No Content si no hay inventarios de productos")
    void deberiaDevolverNoContentSiNoHayInventarios() {
        when(productoRepository.findAll()).thenReturn(Collections.emptyList());
        when(productoMapper.toInventoryDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        ResponseEntity<List<ProductoInventoryDto>> response = inventoryService.findAllProductosInventory();

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("Debería actualizar el stock en el DTO de inventario")
    void deberiaActualizarElStockDelInventario() {
        Long productoId = 1L;
        int nuevoStock = 50;
        Producto producto = new Producto();
        producto.setId(productoId);
        ProductoInventoryDto productoInventoryDto = new ProductoInventoryDto();
        productoInventoryDto.setId(productoId);
        productoInventoryDto.setStockBodega(20);

        when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));
        when(productoMapper.toInventoryDto(producto)).thenReturn(productoInventoryDto);

        ResponseEntity<ProductoInventoryDto> response = inventoryService.updateInventory(productoId, nuevoStock);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(nuevoStock, response.getBody().getStockBodega());
    }

    @Test
    @DisplayName("Debería lanzar ProductoNoEncontrado si el producto no existe al intentar actualizar inventario")
    void deberiaLanzarExcepcionAlActualizarInventarioSiProductoNoExiste() {
        Long productoId = 99L;
        when(productoRepository.findById(productoId)).thenReturn(Optional.empty());

        assertThrows(ProductoNoEncontrado.class, () -> {
            inventoryService.updateInventory(productoId, 10);
        });
    }

    @Test
    @DisplayName("Debería transferir stock exitosamente si hay suficiente en bodega")
    void deberiaTransferirStockExitosamente() {
        Long productoId = 1L;
        RequestStockDto requestDto = new RequestStockDto();
        requestDto.setCantidad(10);

        Producto producto = new Producto();
        producto.setId(productoId);
        producto.setStockBodega(50);
        producto.setStock(20);

        when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));

        ResponseEntity<ProductResponseDto> response = inventoryService.transferirStock(productoId, requestDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Se transfirió el stock correctamente al producto con id " + productoId, response.getBody().getMessage());

        ArgumentCaptor<Producto> productoCaptor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepository).save(productoCaptor.capture());

        Producto productoGuardado = productoCaptor.getValue();
        assertEquals(40, productoGuardado.getStockBodega());
        assertEquals(30, productoGuardado.getStock());
    }

    @Test
    @DisplayName("Debería devolver Bad Request si no hay suficiente stock en bodega para transferir")
    void deberiaDevolverBadRequestSiNoHayStockSuficienteParaTransferir() {
        Long productoId = 1L;
        RequestStockDto requestDto = new RequestStockDto();
        requestDto.setCantidad(100);

        Producto producto = new Producto();
        producto.setId(productoId);
        producto.setStockBodega(50);

        when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));

        ResponseEntity<ProductResponseDto> response = inventoryService.transferirStock(productoId, requestDto);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No hay suficiente stock en bodega para transferir al producto con id " + productoId, response.getBody().getMessage());
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debería lanzar ProductoNoEncontrado si el producto no existe al intentar transferir stock")
    void deberiaLanzarExcepcionAlTransferirStockSiProductoNoExiste() {
        Long productoId = 99L;
        RequestStockDto requestDto = new RequestStockDto();
        requestDto.setCantidad(10);

        when(productoRepository.findById(productoId)).thenReturn(Optional.empty());

        assertThrows(ProductoNoEncontrado.class, () -> {
            inventoryService.transferirStock(productoId, requestDto);
        });
    }
}