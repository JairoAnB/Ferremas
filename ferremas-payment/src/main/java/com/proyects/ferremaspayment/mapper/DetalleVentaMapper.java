package com.proyects.ferremaspayment.mapper;

import com.proyects.ferremaspayment.dto.DetalleVentaDto;
import com.proyects.ferremaspayment.dto.ItemVentaDto;
import com.proyects.ferremaspayment.model.DetalleVenta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DetalleVentaMapper {

    DetalleVentaDto toDto(ItemVentaDto itemVentaDto);

    ItemVentaDto toItemVentaDto(DetalleVentaDto detalleVentaDto);

    @Mapping(source = "nombreProducto", target = "nombre")
    @Mapping(source = "subTotal", target = "subTotal")
    DetalleVentaDto toDto(DetalleVenta detalleVenta);

    @Mapping(source = "nombre", target = "nombreProducto")
    @Mapping(source = "subTotal", target = "subTotal")
    DetalleVenta toEntity(DetalleVentaDto detalleVentaDto);

    List<DetalleVentaDto> toDtoList(List<DetalleVenta> detalleVentas);
}
