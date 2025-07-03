package com.proyects.ferremaspayment.service;

import cl.transbank.webpay.webpayplus.WebpayPlus;
import cl.transbank.webpay.webpayplus.responses.WebpayPlusTransactionCreateResponse;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.proyects.ferremaspayment.dto.TransaccionResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentGatewayTest {

    @Mock
    private APIContext apiContext;

    @InjectMocks
    private PaymentGateway paymentGateway;

    @Test
    @DisplayName("Debería crear una transacción de Webpay exitosamente")
    void deberiaIniciarTransaccionWebpayExitosamente() throws Exception {
        String mockUrl = "https://mock-transbank.cl/url";
        String mockToken = "mock-token-12345";
        WebpayPlusTransactionCreateResponse mockResponse = mock(WebpayPlusTransactionCreateResponse.class);
        when(mockResponse.getUrl()).thenReturn(mockUrl);
        when(mockResponse.getToken()).thenReturn(mockToken);

        try (MockedConstruction<WebpayPlus.Transaction> mockedTx = mockConstruction(WebpayPlus.Transaction.class,
                (mock, context) -> {
                    when(mock.create(anyString(), anyString(), anyDouble(), anyString())).thenReturn(mockResponse);
                })) {

            TransaccionResponseDto result = paymentGateway.iniciarTransaccion("session123", "buyOrder456", "return.url", 10000);

            assertNotNull(result);
            assertEquals(mockToken, result.getUrl());
            assertEquals(mockUrl, result.getToken());
            assertEquals(1, mockedTx.constructed().size());
        }
    }

    @Test
    @DisplayName("Debería lanzar una excepción si Transbank falla al iniciar la transacción")
    void deberiaLanzarExcepcionSiTransbankFalla() {
        try (MockedConstruction<WebpayPlus.Transaction> mockedTx = mockConstruction(WebpayPlus.Transaction.class,
                (mock, context) -> {
                    when(mock.create(anyString(), anyString(), anyDouble(), anyString())).thenThrow(new IOException("Error de red"));
                })) {

            assertThrows(RuntimeException.class, () -> {
                paymentGateway.iniciarTransaccion("session123", "buyOrder456", "return.url", 10000);
            });
        }
    }

    @Test
    @DisplayName("Debería crear un pago de PayPal exitosamente")
    void deberiaCrearPagoPaypalExitosamente() throws PayPalRESTException {
        try (MockedConstruction<Payment> mockedPayment = mockConstruction(Payment.class,
                (mock, context) -> {
                    when(mock.create(apiContext)).thenReturn(mock);
                })) {

            Payment result = paymentGateway.createPago(99.99, "USD", "paypal", "sale", "custom123", "Descripción de prueba", "http://cancel.url", "http://success.url");

            assertNotNull(result);
            Payment createdPayment = mockedPayment.constructed().get(0);
            verify(createdPayment).create(apiContext);
            verify(createdPayment).setIntent("sale");
            verify(createdPayment).setTransactions(anyList());
            verify(createdPayment).setRedirectUrls(any());
        }
    }

    @Test
    @DisplayName("Debería lanzar PayPalRESTException si la creación del pago falla")
    void deberiaLanzarExcepcionAlCrearPagoPaypal() {
        try (MockedConstruction<Payment> mockedPayment = mockConstruction(Payment.class,
                (mock, context) -> {
                    when(mock.create(apiContext)).thenThrow(new PayPalRESTException("Error de API"));
                })) {

            assertThrows(PayPalRESTException.class, () -> {
                paymentGateway.createPago(99.99, "USD", "paypal", "sale", "custom123", "Descripción", "cancel.url", "success.url");
            });
        }
    }

    @Test
    @DisplayName("Debería ejecutar un pago de PayPal exitosamente")
    void deberiaEjecutarPagoPaypalExitosamente() throws PayPalRESTException {
        String paymentId = "PAYID-12345";
        String payerId = "PAYERID-67890";
        ArgumentCaptor<PaymentExecution> executionCaptor = ArgumentCaptor.forClass(PaymentExecution.class);

        try (MockedConstruction<Payment> mockedPayment = mockConstruction(Payment.class,
                (mock, context) -> {
                    when(mock.execute(eq(apiContext), any(PaymentExecution.class))).thenReturn(mock);
                })) {

            Payment result = paymentGateway.excutePayment(paymentId, payerId);

            assertNotNull(result);
            Payment executedPayment = mockedPayment.constructed().get(0);

            verify(executedPayment).setId(paymentId);
            verify(executedPayment).execute(eq(apiContext), executionCaptor.capture());

            assertEquals(payerId, executionCaptor.getValue().getPayerId());
        }
    }

    @Test
    @DisplayName("Debería lanzar PayPalRESTException si la ejecución del pago falla")
    void deberiaLanzarExcepcionAlEjecutarPagoPaypal() {
        try (MockedConstruction<Payment> mockedPayment = mockConstruction(Payment.class,
                (mock, context) -> {
                    when(mock.execute(eq(apiContext), any(PaymentExecution.class)))
                            .thenThrow(new PayPalRESTException("Error de ejecución"));
                })) {

            assertThrows(PayPalRESTException.class, () -> {
                paymentGateway.excutePayment("paymentId", "payerId");
            });
        }
    }
}