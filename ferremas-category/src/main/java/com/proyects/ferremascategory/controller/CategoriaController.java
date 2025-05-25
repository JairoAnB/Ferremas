package com.proyects.ferremascategory.controller;

import com.proyects.ferremascategory.dto.CategoriaCreateDto;
import com.proyects.ferremascategory.dto.CategoriaDto;
import com.proyects.ferremascategory.dto.CategoriaUpdateDto;
import com.proyects.ferremascategory.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @Autowired //-> Inyectar dependencias
    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaDto>> getAllCategorias() {
        return categoriaService.findAllCategorias();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDto> getCategoriaById(@PathVariable Long id) {
        return categoriaService.findCategoriaById(id);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createCategoria(@Valid @RequestBody CategoriaCreateDto categoriaCreateDto) {
        return categoriaService.createCategoria(categoriaCreateDto);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateCategoria(@PathVariable Long id, @Valid @RequestBody CategoriaUpdateDto categoriaUpdateDto) {
        return categoriaService.updateCategoria(id, categoriaUpdateDto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCategoria(@PathVariable Long id) {
        return categoriaService.deleteCategoria(id);
    }
}
