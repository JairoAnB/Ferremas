package com.proyects.ferremasinventory.controller;

import com.proyects.ferremasinventory.dto.ProductoDto;
import com.proyects.ferremasinventory.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
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
    public ResponseEntity<String> createProducto(@Valid @RequestBody ProductoDto productoDto){

        productoService.createProducto(productoDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("El producto fue creado correctamente.");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateProducto(@Valid @PathVariable Long id, @RequestBody ProductoDto productoDto) {
        productoService.updateProducto(id, productoDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("El producto fue actualizado correctamente.");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProducto(@PathVariable Long id){
        return productoService.deleteProducto(id);
    }
}
