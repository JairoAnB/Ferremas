package com.proyects.ferremasusers.service;

import com.proyects.ferremasusers.dto.UsuarioCreateDto;
import com.proyects.ferremasusers.dto.UsuarioCreateResponse;
import com.proyects.ferremasusers.dto.UsuarioDto;
import com.proyects.ferremasusers.dto.UsuarioUpdateDto;
import com.proyects.ferremasusers.exceptions.EmailNoValido;
import com.proyects.ferremasusers.exceptions.PasswordNoValida;
import com.proyects.ferremasusers.exceptions.UsuarioNoActualizado;
import com.proyects.ferremasusers.exceptions.UsuarioNoEncontrado;
import com.proyects.ferremasusers.mapper.UsuarioMapper;
import com.proyects.ferremasusers.model.Usuario;
import com.proyects.ferremasusers.repository.UsuarioRespository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRespository usuarioRespository;
    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioService usuarioService;

    @DisplayName("Debería retornar verdadero para una contraseña válida")
    @Test
    void deberiaValidarContraseñaCorrectamente() {
        assertTrue(usuarioService.isValidPassword("Valida123"));
    }

    @DisplayName("Debería retornar falso para contraseñas inválidas")
    @ParameterizedTest
    @ValueSource(strings = {"chica", "sinmayuscula1", "SINMINUSCULA1", "SinDigitos"})
    void deberiaRechazarContraseñasInvalidas(String password) {
        assertFalse(usuarioService.isValidPassword(password));
    }

    @DisplayName("Debería retornar verdadero para un email válido")
    @Test
    void deberiaValidarEmailCorrectamente() {
        assertTrue(usuarioService.isEmailValid("test@dominio.com"));
    }

    @DisplayName("Debería retornar falso para emails inválidos")
    @ParameterizedTest
    @ValueSource(strings = {"invalido", "invalido@", "invalido@dominio", "invalido@dominio."})
    void deberiaRechazarEmailsInvalidos(String email) {
        assertFalse(usuarioService.isEmailValid(email));
    }

    @Test
    @DisplayName("Debería devolver una lista de todos los usuarios")
    void deberiaDevolverTodosLosUsuarios() {
        when(usuarioRespository.findAll()).thenReturn(List.of(new Usuario()));
        when(usuarioMapper.toDtoList(any())).thenReturn(List.of(new UsuarioDto()));

        ResponseEntity<List<UsuarioDto>> response = usuarioService.findAllUsuarios();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("Debería devolver No Content si no hay usuarios")
    void deberiaDevolverNoContentSiNoHayUsuarios() {
        when(usuarioRespository.findAll()).thenReturn(Collections.emptyList());
        when(usuarioMapper.toDtoList(any())).thenReturn(Collections.emptyList());

        ResponseEntity<List<UsuarioDto>> response = usuarioService.findAllUsuarios();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("Debería encontrar y devolver un usuario por su ID")
    void deberiaEncontrarUsuarioPorId() {
        Long usuarioId = 1L;
        when(usuarioRespository.findById(usuarioId)).thenReturn(Optional.of(new Usuario()));
        when(usuarioMapper.toDto(any(Usuario.class))).thenReturn(new UsuarioDto());

        ResponseEntity<UsuarioDto> response = usuarioService.findUsuarioById(usuarioId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Debería lanzar UsuarioNoEncontrado si el usuario no existe al buscar por ID")
    void deberiaLanzarExcepcionAlBuscarPorIdSiNoExiste() {
        when(usuarioRespository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UsuarioNoEncontrado.class, () -> {
            usuarioService.findUsuarioById(99L);
        });
    }

    @Test
    @DisplayName("Debería crear un usuario exitosamente")
    void deberiaCrearUnUsuarioExitosamente() {
        UsuarioCreateDto createDto = new UsuarioCreateDto();
        Usuario usuarioEntity = new Usuario();
        usuarioEntity.setEmail("valido@test.com");
        usuarioEntity.setPassword("Valida123");
        UsuarioDto responseDto = new UsuarioDto();
        responseDto.setId(1L);

        when(usuarioMapper.toEntity(createDto)).thenReturn(usuarioEntity);
        when(usuarioRespository.save(usuarioEntity)).thenReturn(usuarioEntity);
        when(usuarioMapper.toDto(usuarioEntity)).thenReturn(responseDto);

        ResponseEntity<UsuarioCreateResponse> response = usuarioService.createUsuario(createDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("El usuario fue creado correctamente", response.getBody().getMessage());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    @DisplayName("Debería lanzar PasswordNoValida si la contraseña no cumple los requisitos")
    void deberiaLanzarExcepcionSiContraseñaEsInvalidaAlCrear() {
        UsuarioCreateDto createDto = new UsuarioCreateDto();
        Usuario usuarioEntity = new Usuario();
        usuarioEntity.setPassword("corta");

        when(usuarioMapper.toEntity(createDto)).thenReturn(usuarioEntity);

        assertThrows(PasswordNoValida.class, () -> {
            usuarioService.createUsuario(createDto);
        });
    }

    @Test
    @DisplayName("Debería lanzar EmailNoValido si el email no cumple con el formato")
    void deberiaLanzarExcepcionSiEmailEsInvalidoAlCrear() {
        UsuarioCreateDto createDto = new UsuarioCreateDto();
        Usuario usuarioEntity = new Usuario();
        usuarioEntity.setPassword("Valida123");
        usuarioEntity.setEmail("emailinvalido");

        when(usuarioMapper.toEntity(createDto)).thenReturn(usuarioEntity);

        assertThrows(EmailNoValido.class, () -> {
            usuarioService.createUsuario(createDto);
        });
    }

    @Test
    @DisplayName("Debería actualizar un usuario exitosamente")
    void deberiaActualizarUnUsuarioExitosamente() {
        Long usuarioId = 1L;
        UsuarioUpdateDto updateDto = new UsuarioUpdateDto();
        Usuario usuarioExistente = new Usuario();

        when(usuarioRespository.findById(usuarioId)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRespository.save(usuarioExistente)).thenReturn(usuarioExistente);

        ResponseEntity<UsuarioCreateResponse> response = usuarioService.updateUsuario(usuarioId, updateDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("El usuario fue actualizado correctamente", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Debería lanzar UsuarioNoActualizado si el guardado falla al actualizar")
    void deberiaLanzarExcepcionSiGuardadoFallaAlActualizar() {
        Long usuarioId = 1L;
        UsuarioUpdateDto updateDto = new UsuarioUpdateDto();
        Usuario usuarioExistente = new Usuario();
        when(usuarioRespository.findById(usuarioId)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRespository.save(any(Usuario.class))).thenThrow(new RuntimeException("Error de BD"));

        assertThrows(UsuarioNoActualizado.class, () -> {
            usuarioService.updateUsuario(usuarioId, updateDto);
        });
    }

    @Test
    @DisplayName("Debería eliminar un usuario exitosamente")
    void deberiaEliminarUnUsuarioExitosamente() {
        Long usuarioId = 1L;
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(usuarioId);

        when(usuarioRespository.findById(usuarioId)).thenReturn(Optional.of(usuarioExistente));

        ResponseEntity<UsuarioCreateResponse> response = usuarioService.deleteUsuario(usuarioId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("El usuario fue eliminado correctamente", response.getBody().getMessage());
        verify(usuarioRespository).delete(usuarioExistente);
    }
}