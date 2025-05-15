package com.proyects.ferremascategory.mapper;

import com.proyects.ferremascategory.dto.CategoriaCreateDto;
import com.proyects.ferremascategory.dto.CategoriaDto;
import com.proyects.ferremascategory.dto.CategoriaUpdateDto;
import com.proyects.ferremascategory.model.Categoria;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    Categoria toEntity(CategoriaCreateDto categoriaCreateDto);

    Categoria toEntity(CategoriaUpdateDto categoriaUpdateDto);

    Categoria toEntity(CategoriaDto categoriaDto);

    CategoriaUpdateDto toUpdateDto(Categoria categoria);

    CategoriaCreateDto toCreateDto(Categoria categoria);

    CategoriaDto toDto(Categoria categoria);

    List<CategoriaDto> toDtoList(List<Categoria> categorias);
    List<Categoria> toEntityList(List<CategoriaDto> categoriaDtos);
}
