package com.proyects.ferremaspayment.service;

import com.proyects.ferremaspayment.dto.CurrencyDto;
import com.proyects.ferremaspayment.dto.InventoryRequestDto;
import com.proyects.ferremaspayment.dto.ProductoDto;
import com.proyects.ferremaspayment.dto.UsuarioRequestDto;
import com.proyects.ferremaspayment.exceptions.CambioNoValido;
import com.proyects.ferremaspayment.exceptions.ErrorAlActualizar;
import com.proyects.ferremaspayment.exceptions.ProductoNoObtenido;
import com.proyects.ferremaspayment.exceptions.UsuarioNoEncontrado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PaymentClient paymentClient;

    private Long productoId;
    private Long usuarioId;

    @BeforeEach
    void setUp() {
        productoId = 1L;
        usuarioId = 100L;
    }

    @Test
    @DisplayName("Deberia actualizar correctamente el stock")
    void deberiaActualizarElStockCorrectamente() {
        int cantidadRestar = 5;
        int stockInicial = 20;
        int stockFinal = 15;

        String getStockUrl = "http://localhost:8082/api/v1/products/stock/" + productoId;
        String updateStockUrl = "http://localhost:8082/api/v1/products/stock/" + productoId + "/update/" + stockFinal;

        InventoryRequestDto stockInicialDto = new InventoryRequestDto();
        stockInicialDto.setStock(stockInicial);

        InventoryRequestDto stockFinalDto = new InventoryRequestDto();
        stockFinalDto.setStock(stockFinal);

        when(restTemplate.getForEntity(eq(getStockUrl), eq(InventoryRequestDto.class)))
                .thenReturn(new ResponseEntity<>(stockInicialDto, HttpStatus.OK));
        when(restTemplate.exchange(eq(updateStockUrl), eq(HttpMethod.PUT), eq(null), eq(InventoryRequestDto.class)))
                .thenReturn(new ResponseEntity<>(stockFinalDto, HttpStatus.OK));

        ResponseEntity<InventoryRequestDto> response = paymentClient.actualizarStock(productoId, cantidadRestar);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(stockFinal, response.getBody().getStock());

        verify(restTemplate).getForEntity(eq(getStockUrl), eq(InventoryRequestDto.class));
        verify(restTemplate).exchange(eq(updateStockUrl), eq(HttpMethod.PUT), eq(null), eq(InventoryRequestDto.class));
    }

    @Test
    @DisplayName("Debería lanzar ErrorAlActualizar si el GET inicial de stock lanza una excepción")
    void deberiaLanzarErrorAlActualizarSiProductoNoExiste() {
        String getStockUrl = "http://localhost:8082/api/v1/products/stock/" + productoId;

        when(restTemplate.getForEntity(eq(getStockUrl), eq(InventoryRequestDto.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThrows(ErrorAlActualizar.class, () -> {
            paymentClient.actualizarStock(productoId, 5);
        });
    }

    @Test
    @DisplayName("Debería lanzar ErrorAlActualizar si el PUT de actualización falla")
    void deberiaLanzarErrorAlActualizarSiLaLlamadaPutFalla() {
        int stockInicial = 20;
        String getStockUrl = "http://localhost:8082/api/v1/products/stock/" + productoId;
        InventoryRequestDto stockInicialDto = new InventoryRequestDto();
        stockInicialDto.setStock(stockInicial);

        when(restTemplate.getForEntity(eq(getStockUrl), eq(InventoryRequestDto.class)))
                .thenReturn(new ResponseEntity<>(stockInicialDto, HttpStatus.OK));
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(), eq(InventoryRequestDto.class)))
                .thenThrow(new RestClientException("Error de conexión"));

        assertThrows(ErrorAlActualizar.class, () -> {
            paymentClient.actualizarStock(productoId, 5);
        });
    }

    @Test
    @DisplayName("Debería retornar true si el stock es suficiente")
    void deberiaRetornarVerdaderoSiElStockEsSuficiente() {
        InventoryRequestDto stockDto = new InventoryRequestDto();
        stockDto.setStock(10);
        String url = "http://localhost:8082/api/v1/products/stock/" + productoId;
        when(restTemplate.getForEntity(eq(url), eq(InventoryRequestDto.class)))
                .thenReturn(new ResponseEntity<>(stockDto, HttpStatus.OK));

        boolean resultado = paymentClient.validarStock(productoId, 5);

        assertTrue(resultado);
    }

    @Test
    @DisplayName("Debería retornar false si el stock es insuficiente")
    void deberiaRetornarFalsoSiElStockEsInsuficiente() {
        InventoryRequestDto stockDto = new InventoryRequestDto();
        stockDto.setStock(3);
        String url = "http://localhost:8082/api/v1/products/stock/" + productoId;
        when(restTemplate.getForEntity(eq(url), eq(InventoryRequestDto.class)))
                .thenReturn(new ResponseEntity<>(stockDto, HttpStatus.OK));

        boolean resultado = paymentClient.validarStock(productoId, 5);

        assertFalse(resultado);
    }

    @Test
    @DisplayName("Debería obtener un producto exitosamente")
    void deberiaObtenerUnProductoExitosamente() {
        ProductoDto producto = new ProductoDto();
        producto.setId(productoId);
        producto.setNombre("Martillo");
        String url = "http://localhost:8082/api/v1/products/" + productoId;
        when(restTemplate.getForObject(eq(url), eq(ProductoDto.class))).thenReturn(producto);

        ProductoDto resultado = paymentClient.obtenerProductos(productoId);

        assertNotNull(resultado);
        assertEquals("Martillo", resultado.getNombre());
    }

    @Test
    @DisplayName("Debería lanzar ProductoNoObtenido si getForObject falla")
    void deberiaLanzarExcepcionSiFallaAlObtenerProducto() {
        String url = "http://localhost:8082/api/v1/products/" + productoId;
        when(restTemplate.getForObject(eq(url), eq(ProductoDto.class)))
                .thenThrow(new RestClientException("Error"));

        assertThrows(ProductoNoObtenido.class, () -> {
            paymentClient.obtenerProductos(productoId);
        });
    }

    @Test
    @DisplayName("Debería validar un producto exitosamente")
    void deberiaValidarUnProductoExitosamente() {
        ProductoDto producto = new ProductoDto();
        producto.setId(productoId);
        String url = "http://localhost:8082/api/v1/products/" + productoId;
        when(restTemplate.getForEntity(eq(url), eq(ProductoDto.class)))
                .thenReturn(new ResponseEntity<>(producto, HttpStatus.OK));

        ProductoDto resultado = paymentClient.validadProducto(productoId);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(productoId);
    }

    @Test
    @DisplayName("Debería lanzar ProductoNoObtenido si el producto no se encuentra (404)")
    void deberiaLanzarExcepcionAlValidarSiProductoNoSeEncuentra() {
        String url = "http://localhost:8082/api/v1/products/" + productoId;

        when(restTemplate.getForEntity(eq(url), eq(ProductoDto.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThrows(ProductoNoObtenido.class, () -> {
            paymentClient.validadProducto(productoId);
        });
    }

    @Test
    @DisplayName("Debería obtener el cambio de moneda exitosamente")
    void deberiaObtenerElCambioDeMonedaExitosamente() {
        String moneda = "dolar";
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String url = "https://mindicador.cl/api/" + moneda + "/" + fecha;
        CurrencyDto currencyDto = new CurrencyDto();

        when(restTemplate.getForObject(eq(url), eq(CurrencyDto.class))).thenReturn(currencyDto);

        CurrencyDto resultado = paymentClient.obtenerCambio(moneda, fecha);

        assertThat(resultado).isNotNull();
    }

    @Test
    @DisplayName("Debería lanzar CambioNoValido si la API de moneda falla")
    void deberiaLanzarExcepcionSiLaApiDeMonedaFalla() {
        String moneda = "dolar";
        String fecha = "25-06-2025";
        String url = "https://mindicador.cl/api/" + moneda + "/" + fecha;
        when(restTemplate.getForObject(eq(url), eq(CurrencyDto.class)))
                .thenThrow(new RuntimeException("API no disponible"));

        assertThrows(CambioNoValido.class, () -> {
            paymentClient.obtenerCambio(moneda, fecha);
        });
    }

    @Test
    @DisplayName("Debería obtener un usuario exitosamente")
    void deberiaObtenerUnUsuarioExitosamente() {
        UsuarioRequestDto usuario = new UsuarioRequestDto();
        usuario.setId(usuarioId);
        usuario.setNombre("Juan Perez");
        String url = "http://localhost:8086/api/v1/users/" + usuarioId;
        when(restTemplate.getForEntity(eq(url), eq(UsuarioRequestDto.class)))
                .thenReturn(new ResponseEntity<>(usuario, HttpStatus.OK));

        UsuarioRequestDto resultado = paymentClient.obtenerUsuario(usuarioId);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(usuarioId);
        assertThat(resultado.getNombre()).isEqualTo("Juan Perez");
    }

    @Test
    @DisplayName("Debería lanzar UsuarioNoEncontrado si la API de usuarios retorna 404")
    void deberiaLanzarExcepcionSiElUsuarioNoSeEncuentra() {
        String url = "http://localhost:8086/api/v1/users/" + usuarioId;
        when(restTemplate.getForEntity(eq(url), eq(UsuarioRequestDto.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));

        assertThrows(UsuarioNoEncontrado.class, () -> {
            paymentClient.obtenerUsuario(usuarioId);
        });
    }
}