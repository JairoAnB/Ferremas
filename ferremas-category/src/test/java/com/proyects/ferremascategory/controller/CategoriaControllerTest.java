package com.proyects.ferremascategory.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyects.ferremascategory.dto.CategoriaCreateDto;
import com.proyects.ferremascategory.dto.CategoriaDto;
import com.proyects.ferremascategory.dto.CategoriaResponseDto;
import com.proyects.ferremascategory.dto.CategoriaUpdateDto;
import com.proyects.ferremascategory.service.CategoriaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoriaController.class)
public class CategoriaControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoriaService categoriaService;

    @Test
    @DisplayName("Debería devolver una lista de categorías y estado 200 OK")
    void deberiaDevolverListaDeCategorias() throws Exception {
        CategoriaDto categoriaDto = new CategoriaDto();
        categoriaDto.setId(1L);
        categoriaDto.setNombre("Herramientas");

        when(categoriaService.findAllCategorias()).thenReturn(ResponseEntity.ok(List.of(categoriaDto)));

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Herramientas"));
    }

    @Test
    @DisplayName("Debería devolver estado 204 No Content si no hay categorías")
    void deberiaDevolverNoContentSiListaEstaVacia() throws Exception {
        when(categoriaService.findAllCategorias()).thenReturn(ResponseEntity.noContent().build());
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isNoContent()); // Esperamos un estado 204 No Content
    }

    @Test
    @DisplayName("Debería devolver una categoría por su ID y estado 200 OK")
    void deberiaDevolverCategoriaPorId() throws Exception {
        CategoriaDto categoriaDto = new CategoriaDto();
        categoriaDto.setId(1L);
        categoriaDto.setNombre("Pinturas");

        when(categoriaService.findCategoriaById(1L)).thenReturn(ResponseEntity.ok(categoriaDto));

        mockMvc.perform(get("/api/v1/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Pinturas"));
    }

    @Test
    @DisplayName("Debería crear una nueva categoría y devolver estado 201 Created")
    void deberiaCrearCategoriaNueva() throws Exception {
        CategoriaCreateDto createDto = new CategoriaCreateDto();
        createDto.setNombre("Jardinería");
        createDto.setDescripcion("Artículos y herramientas para el cuidado del jardín.");
        createDto.setImagen("url_de_la_imagen.jpg");

        CategoriaResponseDto responseDto = new CategoriaResponseDto();
        responseDto.setId(10L);
        responseDto.setMessage("Categoría creada correctamente");
        responseDto.setStatus(HttpStatus.CREATED);
        responseDto.setTimestamp(LocalDateTime.now());

        when(categoriaService.createCategoria(any(CategoriaCreateDto.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(responseDto));
        mockMvc.perform(post("/api/v1/categories/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Categoría creada correctamente"));
    }

    @Test
    @DisplayName("Debería actualizar una categoría y devolver estado 200 OK")
    void deberiaActualizarUnaCategoria() throws Exception {
        Long id = 5L;
        CategoriaUpdateDto updateDto = new CategoriaUpdateDto();
        updateDto.setNombre("Electricidad Avanzada");
        updateDto.setDescripcion("Componentes eléctricos de uso profesional.");
        updateDto.setImagen("nueva_url.png");

        CategoriaResponseDto responseDto = new CategoriaResponseDto();
        responseDto.setId(id);
        responseDto.setMessage("Categoría actualizada correctamente");

        when(categoriaService.updateCategoria(eq(id), any(CategoriaUpdateDto.class)))
                .thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(put("/api/v1/categories/update/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Categoría actualizada correctamente"));
    }

    @Test
    @DisplayName("Debería eliminar una categoría y devolver estado 200 OK")
    void deberiaEliminarUnaCategoria() throws Exception {
        Long id = 22L;
        CategoriaResponseDto responseDto = new CategoriaResponseDto();
        responseDto.setId(id);
        responseDto.setMessage("Categoría eliminada correctamente");

        when(categoriaService.deleteCategoria(id)).thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(delete("/api/v1/categories/delete/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Categoría eliminada correctamente"));
    }
}
