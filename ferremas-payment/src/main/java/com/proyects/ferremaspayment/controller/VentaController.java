package com.proyects.ferremaspayment.controller;

import cl.transbank.common.IntegrationApiKeys;
import cl.transbank.common.IntegrationCommerceCodes;
import cl.transbank.common.IntegrationType;
import cl.transbank.webpay.common.WebpayOptions;
import cl.transbank.webpay.webpayplus.WebpayPlus;
import cl.transbank.webpay.webpayplus.responses.WebpayPlusTransactionCommitResponse;
import com.proyects.ferremaspayment.dto.TransaccionResponseDto;
import com.proyects.ferremaspayment.dto.VentaDto;
import com.proyects.ferremaspayment.exceptions.ErrorWebpayConfirmarPago;
import com.proyects.ferremaspayment.exceptions.ErrorWebpayTransaccion;
import com.proyects.ferremaspayment.model.DetalleVenta;
import com.proyects.ferremaspayment.model.Estado;
import com.proyects.ferremaspayment.model.Venta;
import com.proyects.ferremaspayment.repository.VentaRepository;
import com.proyects.ferremaspayment.service.PaymentClient;
import com.proyects.ferremaspayment.service.PaymentGateway;
import com.proyects.ferremaspayment.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DecimalFormat;
import java.util.Optional;


@Controller
public class VentaController {


    private final VentaService ventaService;
    private final PaymentGateway paymentGateway;
    private final VentaRepository ventaRepository;
    private final PaymentClient paymentClient;

    @Autowired
    public VentaController(VentaService ventaService, PaymentGateway paymentGateway, VentaRepository ventaRepository, PaymentClient paymentClient) {
        this.ventaService = ventaService;
        this.paymentGateway = paymentGateway;
        this.ventaRepository = ventaRepository;
        this.paymentClient = paymentClient;
    }


    @GetMapping("/pago")
    public String iniciarPago(Model model, @RequestParam Long id){

        try {
            ResponseEntity<VentaDto> venta = ventaService.findVentaById(id);

            VentaDto ventaResponse = venta.getBody();

            System.out.println(ventaResponse);

            int amount = ventaResponse.getTotal();
            String buyOrder = ventaResponse.getBuyOrder();
            String sessionId = ventaResponse.getId().toString();
            System.out.println( "monto " + amount);
            String returnUrl = "http://localhost:8085/pago/confirmacion";

            TransaccionResponseDto response = paymentGateway.iniciarTransaccion( sessionId,buyOrder,returnUrl, amount);

            DecimalFormat formato = new DecimalFormat("#,###");
            String montoFormateado = formato.format(amount);

            model.addAttribute("urlPago", response.getUrl());
            model.addAttribute("token", response.getToken());
            model.addAttribute("monto", montoFormateado);
            model.addAttribute("ventaId", ventaResponse.getId());
            model.addAttribute("cambio", ventaResponse.getMoneda());

            System.out.println(response.getUrl());
            System.out.println(response.getToken());

            return "pago";

        } catch (Exception e) {
            throw new ErrorWebpayTransaccion("Error al iniciar la transacción, verifique los datos ingresados");
        }

    }


    @GetMapping("/pago/confirmacion")
    public String confirmarPago(@RequestParam("token_ws") String token, Model model){

        try{
                WebpayPlus.Transaction tx = new WebpayPlus.Transaction(
                        new WebpayOptions(IntegrationCommerceCodes.WEBPAY_PLUS, IntegrationApiKeys.WEBPAY, IntegrationType.TEST)
                );

            WebpayPlusTransactionCommitResponse response = tx.commit(token);

            String buyOrder =  response.getBuyOrder();
            Long ventaId = Long.valueOf(response.getBuyOrder().replace("fm-", ""));

            Optional<Venta> ventaOptional = ventaRepository.findById(ventaId);

            if(ventaOptional.isPresent()){

                Venta venta = ventaOptional.get();

                if("AUTHORIZED".equals(response.getStatus()) && response.getResponseCode() == 0){

                    for(DetalleVenta ventasDetalle : venta.getDetalle()){

                        paymentClient.actualizarStock(ventasDetalle.getProductoId(), ventasDetalle.getCantidad());
                    }
                    venta.setEstado(Estado.COMPLETADA);
                    ventaRepository.save(venta);

                    return "webpay/pago-completado";
                }else {

                    venta.setEstado(Estado.CANCELADA);
                    ventaRepository.save(venta);
                    return "webpay/pago-rechazado";
                }
            }else {
                return "webpay/pago-error";
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorWebpayConfirmarPago("Error al procesar la transacción y confirmar el pago");

        }

    }
}
