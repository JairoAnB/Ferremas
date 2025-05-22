package com.proyects.ferremasinventory.mapper;


import com.proyects.ferremasinventory.dto.ProductoCreateDto;
import com.proyects.ferremasinventory.dto.ProductoDto;
import com.proyects.ferremasinventory.dto.ProductoUpdateDto;
import com.proyects.ferremasinventory.dto.ProductoUpdateStockDto;
import com.proyects.ferremasinventory.model.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductoMapper {

    @Mappings({
            @Mapping(source = "marca", target = "marca"),
            @Mapping(source = "nombre", target = "nombre"),
            @Mapping(source = "precio", target = "precio"),
            @Mapping(source = "stock", target = "stock"),
            @Mapping(source = "descripcion", target = "descripcion"),
            @Mapping(source = "categoriaId", target = "categoriaId"),
            @Mapping(source = "fechaCreacion", target = "fechaCreacion")
    })
    ProductoDto toDto(Producto producto);

    @Mappings({
            @Mapping(source = "marca", target = "marca"),
            @Mapping(source = "nombre", target = "nombre"),
            @Mapping(source = "precio", target = "precio"),
            @Mapping(source = "stock", target = "stock"),
            @Mapping(source = "descripcion", target = "descripcion"),
            @Mapping(source = "categoriaId", target = "categoriaId"),
            @Mapping(source = "fechaCreacion", target = "fechaCreacion")
    })
    Producto toEntity(ProductoDto productoDto);

    List<ProductoDto> toDtoList(List<Producto> productos);
    List<Producto> toEntityList(List<ProductoDto> productoDtos);

    @Mappings({
            @Mapping(source = "marca", target = "marca"),
            @Mapping(source = "nombre", target = "nombre"),
            @Mapping(source = "precio", target = "precio"),
            @Mapping(source = "stock", target = "stock"),
            @Mapping(source = "descripcion", target = "descripcion"),
            @Mapping(source = "categoriaId", target = "categoriaId"),
    })
    ProductoDto productoCreatetoDto(ProductoCreateDto productoCreateDto);

    @Mappings({
            @Mapping(source = "marca", target = "marca"),
            @Mapping(source = "nombre", target = "nombre"),
            @Mapping(source = "precio", target = "precio"),
            @Mapping(source = "stock", target = "stock"),
            @Mapping(source = "descripcion", target = "descripcion"),
            @Mapping(source = "categoriaId", target = "categoriaId"),
            @Mapping(source = "fechaCreacion", target = "fechaCreacion")
    })
    ProductoDto productoUpdatetoDto(ProductoUpdateDto productoUpdateDto);

    @Mappings({
            @Mapping(source = "marca", target = "marca"),
            @Mapping(source = "nombre", target = "nombre"),
            @Mapping(source = "precio", target = "precio"),
            @Mapping(source = "stock", target = "stock"),
            @Mapping(source = "descripcion", target = "descripcion"),
            @Mapping(source = "categoriaId", target = "categoriaId"),
    })
    Producto productoCreatetoEntity(ProductoCreateDto productoCreateDto);

    @Mappings({
            @Mapping(source = "marca", target = "marca"),
            @Mapping(source = "nombre", target = "nombre"),
            @Mapping(source = "precio", target = "precio"),
            @Mapping(source = "stock", target = "stock"),
            @Mapping(source = "descripcion", target = "descripcion"),
            @Mapping(source = "categoriaId", target = "categoriaId"),
            @Mapping(source = "fechaCreacion", target = "fechaCreacion")
    })
    Producto productoUpdatetoEntity(ProductoUpdateDto productoUpdateDto);

    Producto toEntityStock(ProductoUpdateStockDto productoUpdateStockDto);

    ProductoUpdateStockDto toDtoStock(Producto producto);

    List<ProductoUpdateStockDto> toDtoStockList(List<Producto> productos);


}
