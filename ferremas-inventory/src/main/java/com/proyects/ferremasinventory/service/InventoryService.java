package com.proyects.ferremasinventory.service;

import com.proyects.ferremasinventory.dto.ProductResponseDto;
import com.proyects.ferremasinventory.dto.ProductoInventoryDto;
import com.proyects.ferremasinventory.dto.RequestStockDto;
import com.proyects.ferremasinventory.exceptions.ProductoNoEncontrado;
import com.proyects.ferremasinventory.mapper.ProductoMapper;
import com.proyects.ferremasinventory.model.Producto;
import com.proyects.ferremasinventory.repository.ProductoRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    @Autowired
    private final ProductoMapper productoMapper;
    private final ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public ResponseEntity<ProductoInventoryDto> findProductoInventorybyId(Long id){
        Producto productoEntity = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontrado("El producto con la id " + id + " no existe"));

        ProductoInventoryDto productoInventoryDto = productoMapper.toInventoryDto(productoEntity);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productoInventoryDto);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<List<ProductoInventoryDto>> findAllProductosInventory(){
        List<ProductoInventoryDto> productoInventoryDtos = productoMapper.toInventoryDtoList(productoRepository.findAll());

        if (productoInventoryDtos.isEmpty()) {
            return ResponseEntity
                    .noContent()
                    .build();
        }

        return ResponseEntity
                .ok(productoInventoryDtos);
    }

    @Transactional
    public ResponseEntity<ProductoInventoryDto> updateInventory(Long id, int stock){
        ProductoInventoryDto productoInventoryDto = productoMapper.toInventoryDto(productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontrado("El producto con la id " + id + " no existe")));

        productoInventoryDto.setStockBodega(stock);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productoInventoryDto);
    }

    @Transactional
    public ResponseEntity<ProductResponseDto> transferirStock(Long id, @Valid RequestStockDto requestStockDto) {
        Producto productoEntity = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontrado("El producto con la id " + id + " no existe"));

        int cantidad = requestStockDto.getCantidad();
        int stockBodega = productoEntity.getStockBodega();

        if (stockBodega < cantidad) {

            ProductResponseDto inventoryResponse = new ProductResponseDto();

            inventoryResponse.setId(productoEntity.getId());
            inventoryResponse.setMessage("No hay suficiente stock en bodega para transferir al producto con id " + id);
            inventoryResponse.setStatus(HttpStatus.BAD_REQUEST);
            inventoryResponse.setTimestamp(LocalDateTime.now());

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(inventoryResponse);
        }

        productoEntity.setStockBodega(stockBodega - cantidad);
        productoEntity.setStock(productoEntity.getStock() + cantidad);

        productoRepository.save(productoEntity);

        ProductResponseDto productResponseDto = new ProductResponseDto();

        productResponseDto.setId(productoEntity.getId());
        productResponseDto.setMessage("Se transfirió el stock correctamente al producto con id " + id);
        productResponseDto.setStatus(HttpStatus.OK);
        productResponseDto.setTimestamp(LocalDateTime.now());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productResponseDto);
    }

}
