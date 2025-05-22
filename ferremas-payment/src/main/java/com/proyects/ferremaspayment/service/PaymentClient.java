package com.proyects.ferremaspayment.service;

import com.proyects.ferremaspayment.dto.InventoryRequestDto;
import com.proyects.ferremaspayment.dto.ProductoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class PaymentClient {

    private final RestTemplate restTemplate;

    @Autowired
    public PaymentClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<InventoryRequestDto> actualizarStock(Long id, int cantidad){

        String inventoryUrl = "http://localhost:8082/api/products/stock/" + id;

        try{

            ResponseEntity<InventoryRequestDto> response =
                    restTemplate.getForEntity(inventoryUrl, InventoryRequestDto.class);

            if(response.getStatusCode().is2xxSuccessful() && response.getBody() != null){

                InventoryRequestDto inventoryRequestDto = response.getBody();
                // Si el stock es suficiente, se puede continuar con la lógica de negocio

                int stockActualizado = inventoryRequestDto.getStock() - cantidad;

                String updateUrl = "http://localhost:8082/api/products/stock/" + id + "/update/" + stockActualizado;

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
                    throw new RuntimeException("Error al actualizar el stock del producto con id " + id);
                }
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(response.getBody());
            }else {
                throw new RuntimeException("El producto con la id " + id + " no existe");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public boolean validarStock(Long id){
        String inventoryUrl = "http://localhost:8082/api/products/stock/" + id;

        try{

            ResponseEntity<InventoryRequestDto> response =
                    restTemplate.getForEntity(inventoryUrl, InventoryRequestDto.class);

            if(response.getStatusCode().is2xxSuccessful() && response.getBody() != null){

                InventoryRequestDto inventoryRequestDto = response.getBody();

                if(inventoryRequestDto.getStock() <= 0){
                    return false;
                }
                return true;
            }else {
                throw new RuntimeException("El producto con la id " + id + " no existe");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public ProductoDto obtenerProductos(Long id){

        String inventoryUrlProducts = "http://localhost:8082/api/products/" + id;

        try{

            ProductoDto productos = restTemplate.getForObject(inventoryUrlProducts, ProductoDto.class);
            return productos;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
