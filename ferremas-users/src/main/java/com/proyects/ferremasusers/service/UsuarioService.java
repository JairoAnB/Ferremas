package com.proyects.ferremasusers.service;

import com.proyects.ferremasusers.dto.UsuarioCreateDto;
import com.proyects.ferremasusers.dto.UsuarioCreateResponse;
import com.proyects.ferremasusers.dto.UsuarioDto;
import com.proyects.ferremasusers.dto.UsuarioUpdateDto;
import com.proyects.ferremasusers.exceptions.*;
import com.proyects.ferremasusers.mapper.UsuarioMapper;
import com.proyects.ferremasusers.model.Usuario;
import com.proyects.ferremasusers.repository.UsuarioRespository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRespository usuarioRespository;
    private final UsuarioMapper usuarioMapper;

    @Autowired
    public UsuarioService(UsuarioRespository usuarioRespository, UsuarioMapper usuarioMapper) {
        this.usuarioRespository = usuarioRespository;
        this.usuarioMapper = usuarioMapper;
    }

    //validaciones
    public boolean isValidPassword(@NotNull String password) {
        if (password.length() < 8) {
            return false;
        }

        if (!password.matches(".*[A-Z].*")) {
            return false;
        }

        if (!password.matches(".*\\d.*")) {
            return false;
        }
        return true;
    }

    public boolean isEmailValid(@NotNull String email){
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }


    //Metodos CRUD
    @Transactional(readOnly = true)
    public ResponseEntity<List<UsuarioDto>> findAllUsuarios(){
        List<UsuarioDto> usuarioDtos = usuarioMapper.toDtoList(usuarioRespository.findAll());

        if (usuarioDtos.isEmpty()) {
            return ResponseEntity
                    .noContent()
                    .build();
        }

        return ResponseEntity.ok(usuarioDtos);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<UsuarioDto> findUsuarioById(Long id){
        Usuario usuarioEntity = usuarioRespository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontrado("El usuario con la id " + id + " no existe"));

        UsuarioDto usuarioDto = usuarioMapper.toDto(usuarioEntity);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(usuarioDto);
    }

    @Transactional
    public ResponseEntity<UsuarioCreateResponse> createUsuario(UsuarioCreateDto usuarioCreateDto){
            // Mapeo del DTO de entrada a la entidad JPA que será guardada en la base de datos
            Usuario entity = usuarioMapper.toEntity(usuarioCreateDto);

            if(!isValidPassword(entity.getPassword())){
                throw new PasswordNoValida("La contraseña no es válida");
            }
            if(!isEmailValid(entity.getEmail())){
                throw new EmailNoValido("El email no es válido");
            }

            // Guardado de la entidad en la base de datos usando el repositorio
            Usuario usuarioEntity = usuarioRespository.save(entity);

            // Mapeo de la entidad persistida a un DTO de respuesta (sin campos sensibles como password)
            UsuarioDto responseDto = usuarioMapper.toDto(usuarioEntity);

            UsuarioCreateResponse usuarioCreateResponse = new UsuarioCreateResponse();
            usuarioCreateResponse.setId(responseDto.getId());
            usuarioCreateResponse.setMessage("El usuario fue creado correctamente");
            usuarioCreateResponse.setStatus(HttpStatus.CREATED);
            usuarioCreateResponse.setTimestamp(LocalDateTime.now());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(usuarioCreateResponse);

    }

    @Transactional
    public ResponseEntity<UsuarioCreateResponse> updateUsuario(Long id, UsuarioUpdateDto usuarioUpdateDto) {
        Usuario usuarioEntity = usuarioRespository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontrado("El usuario con la id " + id + " no existe"));

        try{
            usuarioEntity.setNombre(usuarioUpdateDto.getNombre());
            usuarioEntity.setApellido(usuarioUpdateDto.getApellido());
            usuarioEntity.setEmail(usuarioUpdateDto.getEmail());
            usuarioEntity.setPassword(usuarioUpdateDto.getPassword());
            usuarioEntity.setRol(usuarioUpdateDto.getRol());

            Usuario updatedUsuario = usuarioRespository.save(usuarioEntity);

            UsuarioCreateResponse usuarioCreateResponse = new UsuarioCreateResponse();
            usuarioCreateResponse.setId(updatedUsuario.getId());
            usuarioCreateResponse.setMessage("El usuario fue actualizado correctamente");
            usuarioCreateResponse.setStatus(HttpStatus.OK);
            usuarioCreateResponse.setTimestamp(LocalDateTime.now());

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(usuarioCreateResponse);

        } catch (Exception e) {
            throw new UsuarioNoActualizado("El usuario no fue actualizado correctamente");
        }
    }

    @Transactional
    public ResponseEntity<UsuarioCreateResponse> deleteUsuario(Long id){
        Usuario usuarioEntity = usuarioRespository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontrado("El usuario con la id " + id + " no existe"));

        usuarioRespository.delete(usuarioEntity);

        UsuarioCreateResponse usuarioCreateResponse = new UsuarioCreateResponse();

        usuarioCreateResponse.setId(usuarioEntity.getId());
        usuarioCreateResponse.setMessage("El usuario fue eliminado correctamente");
        usuarioCreateResponse.setStatus(HttpStatus.OK);
        usuarioCreateResponse.setTimestamp(LocalDateTime.now());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(usuarioCreateResponse);
    }

}
