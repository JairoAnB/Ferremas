package com.proyects.ferremaspayment.controller;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.proyects.ferremaspayment.dto.CurrencyDto;
import com.proyects.ferremaspayment.dto.VentaDto;
import com.proyects.ferremaspayment.model.DetalleVenta;
import com.proyects.ferremaspayment.model.Estado;
import com.proyects.ferremaspayment.model.Venta;
import com.proyects.ferremaspayment.repository.VentaRepository;
import com.proyects.ferremaspayment.service.PaymentClient;
import com.proyects.ferremaspayment.service.PaymentGateway;
import com.proyects.ferremaspayment.service.VentaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PaypalController {

    private final PaymentGateway paymentGateway;
    private final VentaService ventaService;
    private final VentaRepository ventaRepository;
    private final PaymentClient paymentClient;


    @PostMapping("/paypal/create")
    public RedirectView createPayment(@RequestParam("ventaId") String ventaId) {
        ResponseEntity<VentaDto> venta = ventaService.findVentaById(Long.valueOf(ventaId));

        VentaDto ventaResponse = venta.getBody();
        BigDecimal total = BigDecimal.valueOf(ventaResponse.getTotal());
        String currency;
        CurrencyDto cambio;
        BigDecimal dolarValor = null;

        if("dolar".equalsIgnoreCase(ventaResponse.getMoneda())){

            cambio = paymentClient.obtenerCambio(ventaResponse.getMoneda(), LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

            if(cambio.getSerie().isEmpty()){
                cambio = paymentClient.obtenerCambio(ventaResponse.getMoneda(), LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            }

            dolarValor = cambio.getSerie().get(0).getValor();

            BigDecimal totalCompra = BigDecimal.valueOf(ventaResponse.getTotal());

            System.out.println("valor dolar: " + dolarValor);

            total = totalCompra.divide(dolarValor,2, RoundingMode.HALF_UP);
            currency = "USD";
        } else if ("euro".trim().equals(ventaResponse.getMoneda())) {

            cambio = paymentClient.obtenerCambio(ventaResponse.getMoneda(), LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

            BigDecimal euroValor = cambio.getSerie().get(0).getValor();
            BigDecimal totalCompra = BigDecimal.valueOf(ventaResponse.getTotal());

            System.out.println("valor euro: " + euroValor);

            total = totalCompra.divide(euroValor,2, RoundingMode.HALF_UP);
            currency = "EUR";

        } else {
            currency = "CLP";
        }

        try{

           String cancelUrl = "http://localhost:8085/paypal/cancel";
           String successUrl = "http://localhost:8085/paypal/success";

            Payment payment = paymentGateway.createPago(
                    total.doubleValue(), currency,"paypal","sale", ventaResponse.getId().toString(),"prueba venta",cancelUrl, successUrl
            );

            for(Links links : payment.getLinks()){
                if(links.getRel().equals("approval_url")){
                    return new RedirectView(links.getHref());
                }
            }
        } catch (PayPalRESTException e) {
            log.error("ocurrio un error al crear el pago: ", e);
        }
        return new RedirectView("/paypal/pago-error");
    }

    @GetMapping("/paypal/success")
    public String paymentSuccess(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId){
        try {

            Payment payment = paymentGateway.excutePayment(paymentId, payerId);
            if(payment.getState().equals("approved")){

                String idVenta = payment.getTransactions().get(0).getCustom();

             Optional<Venta> ventaOptional =  ventaRepository.findById(Long.valueOf(idVenta));

              if(ventaOptional.isPresent()){
                  Venta venta = ventaOptional.get();
                  for(DetalleVenta ventasDetalle : venta.getDetalle()){
                      paymentClient.actualizarStock(ventasDetalle.getProductoId(), ventasDetalle.getCantidad());
                  }
                  venta.setEstado(Estado.COMPLETADA);
                  ventaRepository.save(venta);
                  return "paypal/pago-completado";
              }
            }

        } catch (PayPalRESTException e) {
            log.error("ocurrio un error al crear el pago: ", e);
        }
        return "paypal/pago-completado";
    }

    @GetMapping("/paypal/cancel")
    public String paymentCancel(){
        return "paypal/pago-rechazado";
    }

    @GetMapping("/paypal/error")
    public String paymentError(){
        return "paypal/pago-error";
    }
}
