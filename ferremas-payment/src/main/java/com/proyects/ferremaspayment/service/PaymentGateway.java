package com.proyects.ferremaspayment.service;


import cl.transbank.common.IntegrationApiKeys;
import cl.transbank.common.IntegrationCommerceCodes;
import cl.transbank.common.IntegrationType;
import cl.transbank.webpay.common.WebpayOptions;
import cl.transbank.webpay.webpayplus.WebpayPlus;
import cl.transbank.webpay.webpayplus.responses.WebpayPlusTransactionCreateResponse;
import com.proyects.ferremaspayment.dto.TransaccionResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class PaymentGateway {

    public TransaccionResponseDto iniciarTransaccion(String sessionId, String buyOrder, String returnUrl, int amount){


        try{
            // Crear la transacción
            WebpayPlus.Transaction tx = new WebpayPlus.Transaction(new WebpayOptions(IntegrationCommerceCodes.WEBPAY_PLUS, IntegrationApiKeys.WEBPAY, IntegrationType.TEST));
            final WebpayPlusTransactionCreateResponse response = tx.create(buyOrder, sessionId, amount, returnUrl);

            return new TransaccionResponseDto(response.getUrl(), response.getToken());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


}
