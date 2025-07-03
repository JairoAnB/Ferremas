package com.proyects.ferremasinventory.service;

import com.proyects.ferremasinventory.dto.CategoriaRequestDto;
import com.proyects.ferremasinventory.exceptions.CategoriaNoEncontrada;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoriaClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CategoriaClient categoriaClient;

    @Test
    @DisplayName("Debería validar una categoría exitosamente si la API externa responde con OK")
    void deberiaValidarCategoriaExitosamente() {
        Long categoriaId = 1L;
        String url = "http://localhost:8081/api/v1/categories/" + categoriaId;
        CategoriaRequestDto mockCategoriaDto = new CategoriaRequestDto();
        mockCategoriaDto.setId(categoriaId);

        when(restTemplate.getForEntity(eq(url), eq(CategoriaRequestDto.class)))
                .thenReturn(new ResponseEntity<>(mockCategoriaDto, HttpStatus.OK));

        ResponseEntity<CategoriaRequestDto> response = categoriaClient.validarCategoria(categoriaId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(categoriaId, response.getBody().getId());
    }

    @Test
    @DisplayName("Debería lanzar CategoriaNoEncontrada si la API externa retorna 404 Not Found")
    void deberiaLanzarExcepcionSiApiRetornaNotFound() {
        Long categoriaId = 99L;
        String url = "http://localhost:8081/api/v1/categories/" + categoriaId;

        when(restTemplate.getForEntity(eq(url), eq(CategoriaRequestDto.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThrows(CategoriaNoEncontrada.class, () -> {
            categoriaClient.validarCategoria(categoriaId);
        });
    }

    @Test
    @DisplayName("Debería lanzar CategoriaNoEncontrada si ocurre un error de conexión con la API externa")
    void deberiaLanzarExcepcionSiHayErrorDeConexion() {
        Long categoriaId = 2L;
        String url = "http://localhost:8081/api/v1/categories/" + categoriaId;

        when(restTemplate.getForEntity(eq(url), eq(CategoriaRequestDto.class)))
                .thenThrow(new RestClientException("Error de conexión"));

        assertThrows(CategoriaNoEncontrada.class, () -> {
            categoriaClient.validarCategoria(categoriaId);
        });
    }

    @Test
    @DisplayName("Debería lanzar CategoriaNoEncontrada si la API externa responde OK pero con cuerpo nulo")
    void deberiaLanzarExcepcionSiApiRespondeConCuerpoNulo() {
        Long categoriaId = 3L;
        String url = "http://localhost:8081/api/v1/categories/" + categoriaId;

        when(restTemplate.getForEntity(eq(url), eq(CategoriaRequestDto.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        assertThrows(CategoriaNoEncontrada.class, () -> {
            categoriaClient.validarCategoria(categoriaId);
        });
    }
}