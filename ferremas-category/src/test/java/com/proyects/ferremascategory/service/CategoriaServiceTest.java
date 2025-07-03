package com.proyects.ferremascategory.service;

import com.proyects.ferremascategory.dto.CategoriaCreateDto;
import com.proyects.ferremascategory.dto.CategoriaDto;
import com.proyects.ferremascategory.dto.CategoriaResponseDto;
import com.proyects.ferremascategory.dto.CategoriaUpdateDto;
import com.proyects.ferremascategory.exceptions.CategoriaNoEncontrada;
import com.proyects.ferremascategory.mapper.CategoriaMapper;
import com.proyects.ferremascategory.model.Categoria;
import com.proyects.ferremascategory.repository.CategoriaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private CategoriaMapper categoriaMapper;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    void testCrearCategoria_Success() {
        CategoriaCreateDto createDto = new CategoriaCreateDto();
        createDto.setNombre("Herramientas");

        Categoria categoriaSinId = new Categoria();
        categoriaSinId.setNombre("Herramientas");

        Categoria categoriaGuardada = new Categoria();
        categoriaGuardada.setId(1L);
        categoriaGuardada.setNombre("Herramientas");

        CategoriaDto categoriaDto = new CategoriaDto();
        categoriaDto.setId(1L);
        categoriaDto.setNombre("Herramientas");

        when(categoriaRepository.findByNombre("Herramientas")).thenReturn(Optional.empty());
        when(categoriaMapper.toEntity(any(CategoriaCreateDto.class))).thenReturn(categoriaSinId);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaGuardada);
        when(categoriaMapper.toDto(any(Categoria.class))).thenReturn(categoriaDto);
        ResponseEntity<CategoriaResponseDto> response = categoriaService.createCategoria(createDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Categoria creada correctamente", response.getBody().getMessage());
        assertEquals(1L, response.getBody().getId());
    }


    @Test
    void testCreateCategoria_siExiste_RetornaError() {

        CategoriaCreateDto createDto = new CategoriaCreateDto();
        createDto.setNombre("Herramientas");

        Categoria categoriaExistente = new Categoria();
        categoriaExistente.setId(1L);
        categoriaExistente.setNombre("Herramientas");

        when(categoriaRepository.findByNombre("Herramientas")).thenReturn(Optional.of(categoriaExistente));

        // Act
        ResponseEntity<CategoriaResponseDto> response = categoriaService.createCategoria(createDto);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("no pueden haber categorias duplicadas"));

        verify(categoriaRepository, never()).save(any(Categoria.class));
    }


    @Test
    void testUpdateCategoriaCompletado() {
        Long id = 1L;
        CategoriaUpdateDto updateDto = new CategoriaUpdateDto();
        updateDto.setNombre("Ferretería");

        Categoria categoriaExistente = new Categoria();
        categoriaExistente.setId(id);

        CategoriaDto categoriaDto = new CategoriaDto();
        categoriaDto.setId(id);
        categoriaDto.setNombre("Ferretería");

        when(categoriaRepository.findById(id)).thenReturn(Optional.of(categoriaExistente));
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(categoriaMapper.toDto(any(Categoria.class))).thenReturn(categoriaDto);

        // Act
        ResponseEntity<CategoriaResponseDto> response = categoriaService.updateCategoria(id, updateDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Categoria actualizada correctamente", response.getBody().getMessage());
    }

    @Test
    void testDeleteCategoria_Completo() {
        Long id = 1L;
        Categoria categoriaExistente = new Categoria();
        categoriaExistente.setId(id);
        categoriaExistente.setNombre("Eliminar");

        when(categoriaRepository.findById(id)).thenReturn(Optional.of(categoriaExistente));
        doNothing().when(categoriaRepository).delete(any(Categoria.class)); // El servicio llama a delete(entity)

        ResponseEntity<CategoriaResponseDto> response = categoriaService.deleteCategoria(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Categoria eliminada correctamente", response.getBody().getMessage());
        assertEquals(id, response.getBody().getId());

        verify(categoriaRepository).delete(categoriaExistente);
    }

    @Test
    void testFindCategoriaByIdNoEncontrado() {
        Long id = 2L;
        when(categoriaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(CategoriaNoEncontrada.class, () -> {
            categoriaService.findCategoriaById(id);
        });
    }

    @Test
    void testDeleteCategoriaNoEncontradoLanzaExcepcion() {
        Long id = 2L;
        when(categoriaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(CategoriaNoEncontrada.class, () -> {
            categoriaService.deleteCategoria(id);
        });
    }

    @Test
    void testUpdateCategoriaCuando_NoExiste_LanzaExcepcion() {
        // Arrange
        Long id = 2L;
        CategoriaUpdateDto updateDto = new CategoriaUpdateDto();
        updateDto.setNombre("No existe");

        when(categoriaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(CategoriaNoEncontrada.class, () -> {
            categoriaService.updateCategoria(id, updateDto);
        });
    }

    @Test
    void testFindAllCategorias_Completo() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Test");

        CategoriaDto categoriaDto = new CategoriaDto();
        categoriaDto.setId(1L);
        categoriaDto.setNombre("Test");

        when(categoriaRepository.findAll()).thenReturn(List.of(categoria));
        when(categoriaMapper.toDtoList(anyList())).thenReturn(List.of(categoriaDto));
        ResponseEntity<List<CategoriaDto>> response = categoriaService.findAllCategorias();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Test", response.getBody().get(0).getNombre());
    }

    @Test
    void testFindAllCategorias_EstaVacio() {
        when(categoriaRepository.findAll()).thenReturn(Collections.emptyList());
        when(categoriaMapper.toDtoList(anyList())).thenReturn(Collections.emptyList());

        ResponseEntity<List<CategoriaDto>> response = categoriaService.findAllCategorias();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }
}
