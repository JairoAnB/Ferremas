package com.proyects.ferremasusers.controller;

import com.proyects.ferremasusers.dto.UsuarioCreateDto;
import com.proyects.ferremasusers.dto.UsuarioDto;
import com.proyects.ferremasusers.dto.UsuarioUpdateDto;
import com.proyects.ferremasusers.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDto>> getAllUsuarios() {
        return usuarioService.findAllUsuarios();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDto> getUsuarioById(@PathVariable Long id) {
        return usuarioService.findUsuarioById(id);
    }

    @PostMapping("/create")
    //OBLIGATORIO LA ANOTACION DE REQUEST BODYYYYYYY!!
    public ResponseEntity<UsuarioDto> createUsuario(@Valid @RequestBody UsuarioCreateDto usuarioCreateDto)  {
        return usuarioService.createUsuario(usuarioCreateDto);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateUsuario(@PathVariable Long id,@Valid @RequestBody UsuarioUpdateDto usuarioUpdateDto) {
        return usuarioService.updateUsuario(id, usuarioUpdateDto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUsuario(@PathVariable Long id) {
        return usuarioService.deleteUsuario(id);
    }
}
