package com.proyects.ferremaspayment.service;

import com.proyects.ferremaspayment.dto.*;
import com.proyects.ferremaspayment.mapper.DetalleVentaMapper;
import com.proyects.ferremaspayment.mapper.VentaMapper;
import com.proyects.ferremaspayment.model.DetalleVenta;
import com.proyects.ferremaspayment.model.Estado;
import com.proyects.ferremaspayment.model.Venta;
import com.proyects.ferremaspayment.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final PaymentClient paymentClient;
    private final PaymentGateway paymentGateway;
    private final VentaMapper ventaMapper;
    private final DetalleVentaMapper detalleVentaMapper;

    @Autowired
    public VentaService(VentaRepository ventaRepository, PaymentClient paymentClient, PaymentGateway paymentGateway, VentaMapper ventaMapper, DetalleVentaMapper detalleVentaMapper) {
        this.ventaRepository = ventaRepository;
        this.paymentClient = paymentClient;
        this.paymentGateway = paymentGateway;
        this.ventaMapper = ventaMapper;
        this.detalleVentaMapper = detalleVentaMapper;
    }



    //CREAR UNA VENTA
    public ResponseEntity<VentaDto> createVenta(VentaRequestDto ventaRequestDto) {
        //validar que el producto existe

        List<ItemVentaDto> items = ventaRequestDto.getProductos();
        List<DetalleVenta> detalles = new ArrayList<>();

        //Inicializo el total de una venta
        int total = 0;
        //Instancio una nueva venta
        Venta venta = new Venta();

        //Recorro sobre cada producto de una venta especialmente en items donde solo hay id y cantidad
        for(ItemVentaDto item : items){

            //Valido que cada producto tiene stock disponible.
            Boolean disponible = paymentClient.validarStock(item.getProductoId());

            if(!disponible){
                //Si no hay stock disponible, se retorna un error
                return ResponseEntity
                        .badRequest()
                        .body(null);
            }

            //Si pasa la validacion de stock, se procede a obtener el producto desde el microservicio.
            ProductoDto producto = paymentClient.obtenerProductos(item.getProductoId());
            //Se obtiene el precio de cada producto
            int precioUnitario = producto.getPrecio();

            //Se calcula el subtotal de cada producto multiplicando la cantidad por el precio unitario
            int subtotal = item.getCantidad() * precioUnitario;

            //Construyo el detalle de cada venta para este producto
            DetalleVenta detalle = new DetalleVenta();

            detalle.setProductoId(item.getProductoId());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(precioUnitario);
            detalle.setSubtotal(subtotal);
            detalle.setNombreProducto(producto.getNombre());
            detalle.setVenta(venta);
            //Lo agrego al detalle de la lista de detalles de venta.
            detalles.add(detalle);

            //acumulo el subtotal
            total += subtotal;

        }

        //Asigno los detalles y el total a la entidad de la venta para guardarlo en la base de datos
        venta.setDetalle(detalles);
        venta.setUsuarioId(ventaRequestDto.getUsuarioId());
        venta.setMetodoPago(ventaRequestDto.getMetodoPago());
        venta.setMoneda(ventaRequestDto.getMoneda());
        venta.setEstado(Estado.PENDIENTE);
        venta.setFechaVenta(LocalDateTime.now());
        //Seteo el precio total de la venta
        venta.setTotal(total);
        //Guardo la venta en la base de datos
        ventaRepository.save(venta);

        venta.setBuyOrder("fm-" + venta.getId());

        ventaRepository.save(venta);

        //Retorno la venta creada y respondo con un ok(200) y el mismo dto que se envio
        List<DetalleVentaDto> detalleVentaDto = detalleVentaMapper.toDtoList(detalles);
        VentaDto ventaDto = ventaMapper.ventaToDto(venta);
        ventaDto.setDetalle(detalleVentaDto);



        return ResponseEntity
                .ok(ventaDto);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<List<VentaDto>> findAllVentas() {
        List<VentaDto> ventas = ventaMapper.toDtoList(ventaRepository.findAll());

        return ResponseEntity.ok(ventas);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<VentaDto> findVentaById(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("La venta con la id " + id + " no existe"));

        VentaDto ventaDto = ventaMapper.ventaToDto(venta);

        return ResponseEntity
                .ok(ventaDto);
    }
//    @Transactional
//    public ResponseEntity<String> updateVenta(Long id, UpdateVentaDto updateVentaDto){
//        Venta venta = ventaRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("La venta con la id " + id + " no existe"));
//
//        //Actualizo la venta
//        Optional<Venta> ventaExistente = ventaRepository.findById(id);
//
//        if(ventaExistente.isPresent()){
//            Venta ventas = ventaExistente.get();
//
//
//            venta.setMetodoPago(updateVentaDto.getMetodoPago());
//            venta.setMoneda(updateVentaDto.getMoneda());
//
//
//            for(DetalleVentaDto detalle : venta.getDetalle()){
//                //Actualizo el stock de cada producto
//
//
//            }
//        }
//
//
//
//    }
}
