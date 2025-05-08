package com.proyects.ferremasinventory.mapper;


import com.proyects.ferremasinventory.dto.ProductoDto;
import com.proyects.ferremasinventory.model.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductoMapper {

    @Mappings({
            @Mapping(source = "codigo", target = "codigoProducto"),
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
            @Mapping(source = "codigoProducto", target = "codigo"),
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
}
