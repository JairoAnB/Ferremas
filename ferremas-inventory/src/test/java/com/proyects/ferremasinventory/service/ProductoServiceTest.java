package com.proyects.ferremasinventory.service;

import com.proyects.ferremasinventory.dto.CategoriaRequestDto;
import com.proyects.ferremasinventory.dto.ProductResponseDto;
import com.proyects.ferremasinventory.dto.ProductoCreateDto;
import com.proyects.ferremasinventory.dto.ProductoDto;
import com.proyects.ferremasinventory.dto.ProductoUpdateDto;
import com.proyects.ferremasinventory.dto.ProductoUpdateStockDto;
import com.proyects.ferremasinventory.exceptions.ProductoNoActualizado;
import com.proyects.ferremasinventory.exceptions.ProductoNoCreado;
import com.proyects.ferremasinventory.exceptions.ProductoNoEliminado;
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private ProductoMapper productoMapper;
    @Mock
    private CategoriaClient categoriaClient;

    @InjectMocks
    private ProductoService productoService;

    @Test
    @DisplayName("Debería devolver una lista de todos los productos")
    void deberiaDevolverTodosLosProductos() {
        Producto producto = new Producto();
        ProductoDto productoDto = new ProductoDto();
        when(productoRepository.findAll()).thenReturn(List.of(producto));
        when(productoMapper.toDtoList(List.of(producto))).thenReturn(List.of(productoDto));

        ResponseEntity<List<ProductoDto>> response = productoService.findAllProductos();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("Debería devolver No Content si no hay productos")
    void deberiaDevolverNoContentSiNoHayProductos() {
        when(productoRepository.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<List<ProductoDto>> response = productoService.findAllProductos();

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("Debería encontrar y devolver un producto por su ID")
    void deberiaEncontrarProductoPorId() {
        Long productoId = 1L;
        Producto producto = new Producto();
        ProductoDto productoDto = new ProductoDto();
        when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));
        when(productoMapper.toDto(producto)).thenReturn(productoDto);

        ResponseEntity<ProductoDto> response = productoService.findProductoById(productoId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productoDto, response.getBody());
    }

    @Test
    @DisplayName("Debería lanzar ProductoNoEncontrado si el producto no existe al buscar por ID")
    void deberiaLanzarExcepcionAlBuscarPorIdSiNoExiste() {
        Long productoId = 99L;
        when(productoRepository.findById(productoId)).thenReturn(Optional.empty());

        assertThrows(ProductoNoEncontrado.class, () -> {
            productoService.findProductoById(productoId);
        });
    }

    @Test
    @DisplayName("Debería crear un producto exitosamente")
    void deberiaCrearUnProductoExitosamente() {
        ProductoCreateDto createDto = new ProductoCreateDto();
        createDto.setCategoriaId(1L);
        Producto productoEntity = new Producto();
        ProductoDto productoGuardadoDto = new ProductoDto();
        productoGuardadoDto.setId(10L);

        when(categoriaClient.validarCategoria(anyLong())).thenReturn(ResponseEntity.ok(new CategoriaRequestDto()));
        when(productoMapper.productoCreatetoEntity(createDto)).thenReturn(productoEntity);
        when(productoRepository.save(productoEntity)).thenReturn(productoEntity);
        when(productoMapper.toDto(productoEntity)).thenReturn(productoGuardadoDto);

        ResponseEntity<ProductResponseDto> response = productoService.createProducto(createDto);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("El producto fue creado correctamente", response.getBody().getMessage());
        assertEquals(10L, response.getBody().getId());

        ArgumentCaptor<Producto> productoCaptor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepository).save(productoCaptor.capture());
        assertEquals(LocalDate.now(), productoCaptor.getValue().getFechaCreacion());
    }

    @Test
    @DisplayName("Debería lanzar ProductoNoCreado si el guardado en el repositorio falla")
    void deberiaLanzarExcepcionSiGuardadoFallaAlCrear() {
        ProductoCreateDto createDto = new ProductoCreateDto();
        createDto.setCategoriaId(1L);

        when(categoriaClient.validarCategoria(anyLong())).thenReturn(ResponseEntity.ok(new CategoriaRequestDto()));
        when(productoMapper.productoCreatetoEntity(any(ProductoCreateDto.class))).thenReturn(new Producto());

        when(productoRepository.save(any(Producto.class))).thenThrow(new RuntimeException("Error de base de datos"));

        assertThrows(ProductoNoCreado.class, () -> {
            productoService.createProducto(createDto);
        });
    }

    @Test
    @DisplayName("Debería actualizar un producto exitosamente")
    void deberiaActualizarUnProductoExitosamente() {
        Long productoId = 1L;
        ProductoUpdateDto updateDto = new ProductoUpdateDto();
        updateDto.setNombre("Taladro Inalámbrico");
        Producto productoExistente = new Producto();
        productoExistente.setId(productoId);
        ProductoDto productoActualizadoDto = new ProductoDto();
        productoActualizadoDto.setId(productoId);

        when(productoRepository.findById(productoId)).thenReturn(Optional.of(productoExistente));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoExistente);
        when(productoMapper.toDto(any(Producto.class))).thenReturn(productoActualizadoDto);

        ResponseEntity<ProductResponseDto> response = productoService.updateProducto(productoId, updateDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("El producto fue actualizado correctamente", response.getBody().getMessage());

        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepository).save(captor.capture());
        assertEquals("Taladro Inalámbrico", captor.getValue().getNombre());
    }

    @Test
    @DisplayName("Debería lanzar ProductoNoActualizado si el guardado falla al actualizar")
    void deberiaLanzarExcepcionSiGuardadoFallaAlActualizar() {
        Long productoId = 1L;
        ProductoUpdateDto updateDto = new ProductoUpdateDto();
        Producto productoExistente = new Producto();
        when(productoRepository.findById(productoId)).thenReturn(Optional.of(productoExistente));
        when(productoRepository.save(any(Producto.class))).thenThrow(new RuntimeException("Error de base de datos"));

        assertThrows(ProductoNoActualizado.class, () -> {
            productoService.updateProducto(productoId, updateDto);
        });
    }

    @Test
    @DisplayName("Debería eliminar un producto exitosamente")
    void deberiaEliminarUnProductoExitosamente() {
        Long productoId = 1L;
        Producto productoExistente = new Producto();
        productoExistente.setId(productoId);
        when(productoRepository.findById(productoId)).thenReturn(Optional.of(productoExistente));
        doNothing().when(productoRepository).delete(productoExistente);

        ResponseEntity<ProductResponseDto> response = productoService.deleteProducto(productoId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("El producto fue eliminado correctamente", response.getBody().getMessage());
        verify(productoRepository).delete(productoExistente);
    }

    @Test
    @DisplayName("Debería lanzar ProductoNoEliminado si la eliminación falla")
    void deberiaLanzarExcepcionSiEliminacionFalla() {
        Long productoId = 1L;
        Producto productoExistente = new Producto();
        when(productoRepository.findById(productoId)).thenReturn(Optional.of(productoExistente));
        doThrow(new RuntimeException("Error de base de datos")).when(productoRepository).delete(any(Producto.class));

        assertThrows(ProductoNoEliminado.class, () -> {
            productoService.deleteProducto(productoId);
        });
    }

    @Test
    @DisplayName("Debería actualizar el stock de un producto exitosamente")
    void deberiaActualizarElStockExitosamente() {
        Long productoId = 1L;
        int nuevoStock = 50;
        Producto productoExistente = new Producto();
        when(productoRepository.findById(productoId)).thenReturn(Optional.of(productoExistente));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoExistente);
        when(productoMapper.toDtoStock(any(Producto.class))).thenReturn(new ProductoUpdateStockDto());

        ResponseEntity<ProductoUpdateStockDto> response = productoService.updateStock(productoId, nuevoStock);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepository).save(captor.capture());
        assertEquals(nuevoStock, captor.getValue().getStock());
    }

    @Test
    @DisplayName("Debería encontrar y devolver el stock de un producto por su ID")
    void deberiaEncontrarStockPorId() {
        Long productoId = 1L;
        Producto producto = new Producto();
        ProductoUpdateStockDto stockDto = new ProductoUpdateStockDto();
        when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));
        when(productoMapper.toDtoStock(producto)).thenReturn(stockDto);

        ResponseEntity<ProductoUpdateStockDto> response = productoService.findProductosStockById(productoId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(stockDto, response.getBody());
    }
}