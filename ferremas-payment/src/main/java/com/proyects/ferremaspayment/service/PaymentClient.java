package com.proyects.ferremaspayment.service;

import com.proyects.ferremaspayment.dto.CurrencyDto;
import com.proyects.ferremaspayment.dto.InventoryRequestDto;
import com.proyects.ferremaspayment.dto.ProductoDto;
import com.proyects.ferremaspayment.dto.UsuarioRequestDto;
import com.proyects.ferremaspayment.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PaymentClient {

    private final RestTemplate restTemplate;

    @Autowired
    public PaymentClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<InventoryRequestDto> actualizarStock(Long id, int cantidad){

        String inventoryUrl = "http://localhost:8082/api/v1/products/stock/" + id;

        try{

            ResponseEntity<InventoryRequestDto> response =
                    restTemplate.getForEntity(inventoryUrl, InventoryRequestDto.class);

            if(response.getStatusCode().is2xxSuccessful() && response.getBody() != null){

                InventoryRequestDto inventoryRequestDto = response.getBody();
                // Si el stock es suficiente, se puede continuar con la lógica de negocio

                int stockActualizado = inventoryRequestDto.getStock() - cantidad;

                String updateUrl = "http://localhost:8082/api/v1/products/stock/" + id + "/update/" + stockActualizado;

                try{
                    ResponseEntity<InventoryRequestDto> updateResponse = restTemplate.exchange(
                            updateUrl,
                            HttpMethod.PUT,
                            null,
                            InventoryRequestDto.class
                    );

                    if(updateResponse.getBody() != null){
                        return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(updateResponse.getBody());
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    throw new StockNoActualizado("No se pudo actualizar el stock del producto con la id " + id);
                }
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(response.getBody());
            }else {
                throw new ProductoNoExiste("El producto con la id " + id + " no existe");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorAlActualizar("No se pudo actualizar el stock, Por favor intente nuevamente");
        }
    }


    public boolean validarStock(Long id, int cantidad){
        String inventoryUrl = "http://localhost:8082/api/v1/products/stock/" + id;

        try{

            ResponseEntity<InventoryRequestDto> response =
                    restTemplate.getForEntity(inventoryUrl, InventoryRequestDto.class);

            if(response.getStatusCode().is2xxSuccessful() && response.getBody() != null){

                InventoryRequestDto inventoryRequestDto = response.getBody();


                if(inventoryRequestDto.getStock() < cantidad){
                    return false;
                }

                return true;
            }else {
                throw new ProductoNoExiste("El producto con la id " + id + " no existe");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new StockNoActualizado("No se pudo actualizar el stock del producto con la id, revisa la solicitud ");
        }
    }

    public ProductoDto obtenerProductos(Long id){

        String inventoryUrlProducts = "http://localhost:8082/api/v1/products/" + id;

        try{

            ProductoDto productos = restTemplate.getForObject(inventoryUrlProducts, ProductoDto.class);
            return productos;

        } catch (Exception e) {
            e.printStackTrace();
            throw new ProductoNoObtenido("No se pudo obtener el producto con la id " + id);
        }
    }

    public CurrencyDto obtenerCambio(String moneda, String fecha){

        String currencyUrl = "https://mindicador.cl/api/" + moneda + "/" + fecha;

        try{

            CurrencyDto currencyDto = restTemplate.getForObject(currencyUrl, CurrencyDto.class);
            return currencyDto;


        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new CambioNoValido("No se pudo obtener el cambio de la moneda");
        }

    }

    public UsuarioRequestDto obtenerUsuario(Long id){

        String userUrl = "http://localhost:8086/api/v1/users/" + id;

        try{

            ResponseEntity<UsuarioRequestDto> response =
                    restTemplate.getForEntity(userUrl, UsuarioRequestDto.class);

            if(response.getStatusCode().is2xxSuccessful() && response.getBody() != null){

                UsuarioRequestDto usuarios = response.getBody();
                return usuarios;

            }else {
                throw new UsuarioNoEncontrado("El usuario con la id " + id + " no existe");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new UsuarioNoEncontrado("El usuario con la id " + id + " no existe");
        }
    }


}
