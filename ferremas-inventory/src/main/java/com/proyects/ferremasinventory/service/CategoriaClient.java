package com.proyects.ferremasinventory.service;

import com.proyects.ferremasinventory.dto.CategoriaRequestDto;
import com.proyects.ferremasinventory.exceptions.CategoriaNoEncontrada;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CategoriaClient {

    private final RestTemplate restTemplate;

    @Autowired
    public CategoriaClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    String url = "http://localhost:8081/api/v1/categories";

    public ResponseEntity<CategoriaRequestDto> validarCategoria(Long id){
        String url = "http://localhost:8081/api/v1/categories/" + id;

        try{

            ResponseEntity<CategoriaRequestDto> response =
                    restTemplate.getForEntity(url, CategoriaRequestDto.class);

            if(response.getStatusCode().is2xxSuccessful() && response.getBody() != null){
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(response.getBody());
            }else {
                throw new CategoriaNoEncontrada("La categoria con la id " + id + " no existe");
            }
        } catch (Exception e) {
            throw new CategoriaNoEncontrada("La categoria con la id " + id + " no existe");
        }
    }
}
