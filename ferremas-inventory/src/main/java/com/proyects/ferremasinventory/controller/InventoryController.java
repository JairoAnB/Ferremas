package com.proyects.ferremasinventory.controller;

import com.proyects.ferremasinventory.dto.ProductoInventoryDto;
import com.proyects.ferremasinventory.dto.RequestStockDto;
import com.proyects.ferremasinventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<List<ProductoInventoryDto>> getAllProducts() {

        return inventoryService.findAllProductosInventory();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoInventoryDto> getProductById(@PathVariable Long id) {

        return inventoryService.findProductoInventorybyId(id);
    }

    @PutMapping("/update/{id}/{stock}")
    public ResponseEntity<ProductoInventoryDto> updateProduct(@PathVariable Long id, @PathVariable int stock) {

        return inventoryService.updateInventory(id, stock);
    }

    @PutMapping("/transfer/{id}")
    public ResponseEntity<String> transferProduct(@PathVariable Long id, @RequestBody RequestStockDto stock) {

        return inventoryService.transferirStock(id, stock);
    }
}
