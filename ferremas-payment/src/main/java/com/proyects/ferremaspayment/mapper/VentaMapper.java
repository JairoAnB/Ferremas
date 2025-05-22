package com.proyects.ferremaspayment.mapper;

import com.proyects.ferremaspayment.dto.VentaDto;
import com.proyects.ferremaspayment.dto.VentaRequestDto;
import com.proyects.ferremaspayment.model.Venta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VentaMapper {

    @Mapping(target = "detalle", ignore = true)
    Venta toEntity(VentaRequestDto dto);

    VentaRequestDto toDto(Venta entity);

    VentaDto ventaToDto(Venta venta);

    Venta toEntity(VentaDto dto);

    List<VentaDto> toDtoList(List<Venta> ventas);
}
