package com.proyects.ferremaspayment.controller;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.PayPalRESTException;
import com.proyects.ferremaspayment.dto.CurrencyDto;
import com.proyects.ferremaspayment.dto.CurrencyItemsDto;
import com.proyects.ferremaspayment.dto.VentaDto;
import com.proyects.ferremaspayment.model.DetalleVenta;
import com.proyects.ferremaspayment.model.Estado;
import com.proyects.ferremaspayment.model.Venta;
import com.proyects.ferremaspayment.repository.VentaRepository;
import com.proyects.ferremaspayment.service.PaymentClient;
import com.proyects.ferremaspayment.service.PaymentGateway;
import com.proyects.ferremaspayment.service.VentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaypalControllerTest {

    @Mock
    private PaymentGateway paymentGateway;
    @Mock
    private VentaService ventaService;
    @Mock
    private VentaRepository ventaRepository;
    @Mock
    private PaymentClient paymentClient;

    @InjectMocks
    private PaypalController paypalController;

    private VentaDto ventaDto;

    @BeforeEach
    void setUp() {
        ventaDto = new VentaDto();
        ventaDto.setId(1L);
        ventaDto.setTotal(10000);
        ventaDto.setMoneda("dolar");
    }

    @Test
    @DisplayName("Debería crear un pago en USD y redirigir a PayPal")
    void deberiaCrearPagoYRedirigirAPaypal() throws PayPalRESTException {
        when(ventaService.findVentaById(1L)).thenReturn(ResponseEntity.ok(ventaDto));

        CurrencyDto currencyDto = new CurrencyDto();
        CurrencyItemsDto itemsDto = new CurrencyItemsDto(null, new BigDecimal("950.00"));
        currencyDto.setSerie(Collections.singletonList(itemsDto));
        when(paymentClient.obtenerCambio(anyString(), anyString())).thenReturn(currencyDto);

        Payment mockPayment = new Payment();
        Links approvalLink = new Links("https://paypal.com/approve?token=123", "approval_url");
        mockPayment.setLinks(Collections.singletonList(approvalLink));
        when(paymentGateway.createPago(anyDouble(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(mockPayment);

        RedirectView redirectView = paypalController.createPayment("1");

        assertNotNull(redirectView);
        assertEquals("https://paypal.com/approve?token=123", redirectView.getUrl());

        ArgumentCaptor<Double> totalCaptor = ArgumentCaptor.forClass(Double.class);
        verify(paymentGateway).createPago(totalCaptor.capture(), eq("USD"), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        assertEquals(10.53, totalCaptor.getValue(), 0.01);
    }

    @Test
    @DisplayName("Debería redirigir a la página de error si la creación del pago en PayPal falla")
    void deberiaRedirigirAErrorSiPaypalFallaAlCrearPago() throws PayPalRESTException {
        when(ventaService.findVentaById(1L)).thenReturn(ResponseEntity.ok(ventaDto));

        CurrencyDto mockCambio = new CurrencyDto();
        CurrencyItemsDto mockItem = new CurrencyItemsDto(null, new BigDecimal("950.0"));
        mockCambio.setSerie(Collections.singletonList(mockItem));
        when(paymentClient.obtenerCambio(anyString(), anyString())).thenReturn(mockCambio);

        when(paymentGateway.createPago(anyDouble(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new PayPalRESTException("Error de API"));

        RedirectView redirectView = paypalController.createPayment("1");

        assertNotNull(redirectView);
        assertEquals("/paypal/pago-error", redirectView.getUrl());
    }

    @Test
    @DisplayName("Debería completar la venta y actualizar stock si el pago es aprobado")
    void deberiaCompletarVentaYActualizarStockConPagoExitoso() throws PayPalRESTException {
        Payment mockPayment = new Payment();
        mockPayment.setState("approved");
        Transaction transaction = new Transaction();
        transaction.setCustom("1");
        mockPayment.setTransactions(Collections.singletonList(transaction));
        when(paymentGateway.excutePayment("paymentId123", "payerId456")).thenReturn(mockPayment);

        Venta venta = new Venta();
        venta.setId(1L);
        DetalleVenta detalle = new DetalleVenta();
        detalle.setProductoId(101L);
        detalle.setCantidad(2);
        venta.setDetalle(List.of(detalle));
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        String viewName = paypalController.paymentSuccess("paymentId123", "payerId456");

        assertEquals("paypal/pago-completado", viewName);
        verify(paymentClient, times(1)).actualizarStock(101L, 2);

        ArgumentCaptor<Venta> ventaCaptor = ArgumentCaptor.forClass(Venta.class);
        verify(ventaRepository, times(1)).save(ventaCaptor.capture());
        assertEquals(Estado.COMPLETADA, ventaCaptor.getValue().getEstado());
    }

    @Test
    @DisplayName("Debería retornar la vista de completado sin procesar la venta si el pago no es aprobado")
    void deberiaRetornarVistaCompletadoSiPagoNoEsAprobado() throws PayPalRESTException {
        Payment mockPayment = new Payment();
        mockPayment.setState("failed");
        when(paymentGateway.excutePayment(anyString(), anyString())).thenReturn(mockPayment);

        String viewName = paypalController.paymentSuccess("paymentId123", "payerId456");

        assertEquals("paypal/pago-completado", viewName);
        verify(ventaRepository, never()).findById(anyLong());
        verify(paymentClient, never()).actualizarStock(anyLong(), anyInt());
    }

    @Test
    @DisplayName("Debería retornar la vista de pago cancelado")
    void deberiaRetornarVistaDePagoCancelado() {
        String viewName = paypalController.paymentCancel();
        assertEquals("paypal/pago-rechazado", viewName);
    }

    @Test
    @DisplayName("Debería retornar la vista de error de pago")
    void deberiaRetornarVistaDeErrorDePago() {
        String viewName = paypalController.paymentError();
        assertEquals("paypal/pago-error", viewName);
    }
}