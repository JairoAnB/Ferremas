package com.proyects.ferremasinventory.controller;

import com.proyects.ferremasinventory.dto.*;
import com.proyects.ferremasinventory.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductoController {

    private final ProductoService productoService;

    @Autowired
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }


    @GetMapping
    public ResponseEntity<List<ProductoDto>> getAllProductos() {

        return productoService.findAllProductos();

    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDto> getProductoById(@PathVariable Long id) {

        return productoService.findProductoById(id);
    }

    @PostMapping("/create")
    public ResponseEntity<ProductResponseDto> createProducto(@Valid @RequestBody ProductoCreateDto productoCreateDtoDto) {

        return productoService.createProducto(productoCreateDtoDto);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ProductResponseDto> updateProducto(@PathVariable Long id, @Valid @RequestBody ProductoUpdateDto productoUpdateDto) {

        return productoService.updateProducto(id, productoUpdateDto);
    }

    @PutMapping("/stock/{id}/update/{stock}")
    public ResponseEntity<ProductoUpdateStockDto> updateStock(@Valid @PathVariable Long id,@PathVariable int stock) {
        return productoService.updateStock(id, stock);
    }

    @GetMapping("/stock/{id}")
    public ResponseEntity<ProductoUpdateStockDto> getStock(@PathVariable Long id) {
        return productoService.findProductosStockById(id);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ProductResponseDto> deleteProducto(@PathVariable Long id)  {
        return productoService.deleteProducto(id);
    }
}
