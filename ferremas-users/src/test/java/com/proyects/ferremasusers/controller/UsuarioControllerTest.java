package com.proyects.ferremasusers.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyects.ferremasusers.dto.UsuarioCreateDto;
import com.proyects.ferremasusers.dto.UsuarioCreateResponse;
import com.proyects.ferremasusers.dto.UsuarioDto;
import com.proyects.ferremasusers.dto.UsuarioUpdateDto;
import com.proyects.ferremasusers.model.RolUsuario;
import com.proyects.ferremasusers.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    @Test
    @DisplayName("Debería devolver una lista de usuarios y estado 200 OK")
    void deberiaDevolverTodosLosUsuarios() throws Exception {
        UsuarioDto usuarioDto = new UsuarioDto();
        usuarioDto.setId(1L);
        usuarioDto.setNombre("Juan");

        when(usuarioService.findAllUsuarios()).thenReturn(ResponseEntity.ok(List.of(usuarioDto)));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("Juan"));
    }

    @Test
    @DisplayName("Debería devolver estado 204 No Content si no hay usuarios")
    void deberiaDevolverNoContentSiNoHayUsuarios() throws Exception {
        when(usuarioService.findAllUsuarios()).thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Debería devolver un usuario por su ID y estado 200 OK")
    void deberiaDevolverUsuarioPorId() throws Exception {
        Long usuarioId = 1L;
        UsuarioDto usuarioDto = new UsuarioDto();
        usuarioDto.setId(usuarioId);

        when(usuarioService.findUsuarioById(usuarioId)).thenReturn(ResponseEntity.ok(usuarioDto));

        mockMvc.perform(get("/api/v1/users/{id}", usuarioId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuarioId));
    }

    @Test
    @DisplayName("Debería crear un usuario y devolver estado 201 Created")
    void deberiaCrearUnUsuario() throws Exception {
        // SOLUCIÓN: Crear un DTO completo y válido
        UsuarioCreateDto createDto = new UsuarioCreateDto();
        createDto.setNombre("Ana");
        createDto.setApellido("Gomez");
        createDto.setEmail("ana.gomez@test.com");
        createDto.setPassword("ContraseñaValida1");
        createDto.setRol(RolUsuario.USUARIO);

        UsuarioCreateResponse responseDto = new UsuarioCreateResponse();
        responseDto.setMessage("Usuario creado");

        when(usuarioService.createUsuario(any(UsuarioCreateDto.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(responseDto));

        mockMvc.perform(post("/api/v1/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Usuario creado"));
    }
    @Test
    @DisplayName("Debería actualizar un usuario y devolver estado 200 OK")
    void deberiaActualizarUnUsuario() throws Exception {
        Long usuarioId = 1L;

        // SOLUCIÓN: Crear un DTO completo y válido
        UsuarioUpdateDto updateDto = new UsuarioUpdateDto();
        updateDto.setNombre("Pedro");
        updateDto.setApellido("Perez");
        updateDto.setEmail("pedro.perez@test.com");
        updateDto.setPassword("NuevaClaveValida1");
        updateDto.setRol(RolUsuario.USUARIO);

        UsuarioCreateResponse responseDto = new UsuarioCreateResponse();
        responseDto.setMessage("Usuario actualizado");

        when(usuarioService.updateUsuario(anyLong(), any(UsuarioUpdateDto.class)))
                .thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(put("/api/v1/users/update/{id}", usuarioId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuario actualizado"));
    }

    @Test
    @DisplayName("Debería eliminar un usuario y devolver estado 200 OK")
    void deberiaEliminarUnUsuario() throws Exception {
        Long usuarioId = 1L;
        UsuarioCreateResponse responseDto = new UsuarioCreateResponse();
        responseDto.setMessage("Usuario eliminado");

        when(usuarioService.deleteUsuario(usuarioId)).thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(delete("/api/v1/users/delete/{id}", usuarioId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuario eliminado"));
    }
}