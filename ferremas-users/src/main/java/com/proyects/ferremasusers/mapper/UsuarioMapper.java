package com.proyects.ferremasusers.mapper;

import com.proyects.ferremasusers.dto.UsuarioCreateDto;
import com.proyects.ferremasusers.dto.UsuarioDto;
import com.proyects.ferremasusers.dto.UsuarioUpdateDto;
import com.proyects.ferremasusers.model.Usuario;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioDto toDto(Usuario usuario);
    Usuario toEntity(UsuarioDto usuarioDto);

    List<UsuarioDto> toDtoList(List<Usuario> usuarios);
    List<Usuario> toEntityList(List<UsuarioDto> usuarioDtos);

    Usuario toEntity(UsuarioCreateDto dto);
    UsuarioCreateDto toCreateDto(Usuario usuario);

    Usuario toEntity(UsuarioUpdateDto dto);
}
