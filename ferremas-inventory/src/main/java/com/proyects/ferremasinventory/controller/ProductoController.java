package com.proyects.ferremasinventory.controller;

import com.proyects.ferremasinventory.dto.ProductoCreateDto;
import com.proyects.ferremasinventory.dto.ProductoDto;
import com.proyects.ferremasinventory.dto.ProductoUpdateDto;
import com.proyects.ferremasinventory.dto.ProductoUpdateStockDto;
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
    public ResponseEntity<ProductoDto> getProductoById(@Valid @PathVariable Long id) {

        return productoService.findProductoById(id);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createProducto(@Valid @RequestBody ProductoCreateDto productoCreateDtoDto) {

        productoService.createProducto(productoCreateDtoDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("El producto fue creado correctamente.");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateProducto(@Valid @PathVariable Long id, @RequestBody ProductoUpdateDto productoUpdateDto) {
        productoService.updateProducto(id, productoUpdateDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("El producto fue actualizado correctamente.");
    }

    @PutMapping("/stock/{id}/update/{stock}")
    public ResponseEntity<ProductoUpdateStockDto> updateStock(@Valid @PathVariable Long id, @PathVariable int stock) {
        return productoService.updateStock(id, stock);
    }

    @GetMapping("/stock/{id}")
    public ResponseEntity<ProductoUpdateStockDto> getStock(@PathVariable Long id) {
        return productoService.findProductosStockById(id);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProducto(@PathVariable Long id)  {
        return productoService.deleteProducto(id);
    }
}
