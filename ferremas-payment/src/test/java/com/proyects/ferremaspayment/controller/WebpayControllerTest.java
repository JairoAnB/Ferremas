package com.proyects.ferremaspayment.controller;

import cl.transbank.webpay.webpayplus.WebpayPlus;
import cl.transbank.webpay.webpayplus.responses.WebpayPlusTransactionCommitResponse;
import com.proyects.ferremaspayment.dto.TransaccionResponseDto;
import com.proyects.ferremaspayment.dto.VentaDto;
import com.proyects.ferremaspayment.exceptions.ErrorWebpayTransaccion;
import com.proyects.ferremaspayment.model.Estado;
import com.proyects.ferremaspayment.model.Venta;
import com.proyects.ferremaspayment.repository.VentaRepository;
import com.proyects.ferremaspayment.service.PaymentClient;
import com.proyects.ferremaspayment.service.PaymentGateway;
import com.proyects.ferremaspayment.service.VentaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WebpayController.class)
class WebpayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VentaService ventaService;
    @MockBean
    private PaymentGateway paymentGateway;
    @MockBean
    private VentaRepository ventaRepository;
    @MockBean
    private PaymentClient paymentClient;

    @Test
    @DisplayName("Debería iniciar el pago y retornar la vista 'pago' con los atributos correctos")
    void deberiaIniciarElPagoYRetornarVistaConAtributos() throws Exception {
        long ventaId = 1L;
        VentaDto ventaDto = new VentaDto();
        ventaDto.setId(ventaId);
        ventaDto.setTotal(10000);
        ventaDto.setBuyOrder("fm-1");
        ventaDto.setMoneda("pesos");

        TransaccionResponseDto transaccionDto = new TransaccionResponseDto("http://webpay.url/pago", "token123");

        when(ventaService.findVentaById(ventaId)).thenReturn(ResponseEntity.ok(ventaDto));
        when(paymentGateway.iniciarTransaccion(anyString(), anyString(), anyString(), anyInt())).thenReturn(transaccionDto);

        mockMvc.perform(get("/pago").param("id", String.valueOf(ventaId)))
                .andExpect(status().isOk())
                .andExpect(view().name("pago"))
                .andExpect(model().attributeExists("urlPago", "token", "monto", "ventaId"))
                .andExpect(model().attribute("token", "http://webpay.url/pago"))
                .andExpect(model().attribute("urlPago", "token123"));
    }

    @Test
    @DisplayName("Debería lanzar una excepción si el servicio falla al iniciar el pago")
    void deberiaLanzarExcepcionSiServicioFallaAlIniciar() throws Exception {
        when(ventaService.findVentaById(anyLong())).thenThrow(new RuntimeException("Error de servicio simulado"));

        mockMvc.perform(get("/pago").param("id", "1"))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ErrorWebpayTransaccion));
    }

    @Test
    @DisplayName("Debería completar la venta si la confirmación del pago es aprobada")
    void deberiaCompletarVentaConConfirmacionAprobada() throws Exception {
        String token = "valid_token";
        WebpayPlusTransactionCommitResponse commitResponse = mock(WebpayPlusTransactionCommitResponse.class);

        when(commitResponse.getStatus()).thenReturn("AUTHORIZED");
        when(commitResponse.getBuyOrder()).thenReturn("fm-1");
        doReturn((byte) 0).when(commitResponse).getResponseCode();

        Venta venta = new Venta();
        venta.setId(1L);
        venta.setDetalle(Collections.emptyList());
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        try (MockedConstruction<WebpayPlus.Transaction> mockedTx = mockConstruction(WebpayPlus.Transaction.class,
                (mock, context) -> when(mock.commit(token)).thenReturn(commitResponse))) {

            mockMvc.perform(get("/pago/confirmacion").param("token_ws", token))
                    .andExpect(status().isOk())
                    .andExpect(view().name("webpay/pago-completado"));

            ArgumentCaptor<Venta> ventaCaptor = ArgumentCaptor.forClass(Venta.class);
            verify(ventaRepository).save(ventaCaptor.capture());
            assertEquals(Estado.COMPLETADA, ventaCaptor.getValue().getEstado());
        }
    }

    @Test
    @DisplayName("Debería cancelar la venta si la confirmación del pago es rechazada")
    void deberiaCancelarVentaConConfirmacionRechazada() throws Exception {
        String token = "rejected_token";
        WebpayPlusTransactionCommitResponse commitResponse = mock(WebpayPlusTransactionCommitResponse.class);

        when(commitResponse.getStatus()).thenReturn("FAILED");
        when(commitResponse.getBuyOrder()).thenReturn("fm-2");
        doReturn((byte) -1).when(commitResponse).getResponseCode();

        Venta venta = new Venta();
        venta.setId(2L);
        when(ventaRepository.findById(2L)).thenReturn(Optional.of(venta));

        try (MockedConstruction<WebpayPlus.Transaction> mockedTx = mockConstruction(WebpayPlus.Transaction.class,
                (mock, context) -> when(mock.commit(token)).thenReturn(commitResponse))) {

            mockMvc.perform(get("/pago/confirmacion").param("token_ws", token))
                    .andExpect(status().isOk())
                    .andExpect(view().name("webpay/pago-rechazado"));

            ArgumentCaptor<Venta> ventaCaptor = ArgumentCaptor.forClass(Venta.class);
            verify(ventaRepository).save(ventaCaptor.capture());
            assertEquals(Estado.CANCELADA, ventaCaptor.getValue().getEstado());
        }
    }
}