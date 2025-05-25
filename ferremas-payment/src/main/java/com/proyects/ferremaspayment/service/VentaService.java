package com.proyects.ferremaspayment.service;

import com.proyects.ferremaspayment.dto.*;
import com.proyects.ferremaspayment.exceptions.*;
import com.proyects.ferremaspayment.mapper.DetalleVentaMapper;
import com.proyects.ferremaspayment.mapper.VentaMapper;
import com.proyects.ferremaspayment.model.DetalleVenta;
import com.proyects.ferremaspayment.model.Estado;
import com.proyects.ferremaspayment.model.Venta;
import com.proyects.ferremaspayment.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class VentaService {

    @Autowired
    private final VentaRepository ventaRepository;
    private final PaymentClient paymentClient;
    private final PaymentGateway paymentGateway;
    private final VentaMapper ventaMapper;
    private final DetalleVentaMapper detalleVentaMapper;

    //CREAR UNA VENTA
    public ResponseEntity<CreateSaleResponse> createVenta(VentaRequestDto ventaRequestDto) {
        //validar que el producto existe

        List<ItemVentaDto> items = ventaRequestDto.getProductos();
        List<DetalleVenta> detalles = new ArrayList<>();

        //Inicializo el total de una venta
        int total = 0;
        //Instancio una nueva venta
        Venta venta = new Venta();

        UsuarioRequestDto usuario = paymentClient.obtenerUsuario(ventaRequestDto.getUsuarioId());
        if(usuario == null){
            throw new UsuarioNoEncontrado("El usuario con id " + ventaRequestDto.getUsuarioId() + " no existe");
        }

        //Recorro sobre cada producto de una venta especialmente en items donde solo hay id y cantidad
        Set<Long> ids = new HashSet<>(); //Genero un conjunto que solo permite valores unicos, si se intenta repetir el valor, no se acepta
        for(ItemVentaDto item : items){
            //valido id
            ProductoDto validacionProducto = paymentClient.validadProducto(item.getProductoId());

            if(validacionProducto == null) {
                throw new ProductoNoExiste("El producto con id " + item.getProductoId() + " no existe");
            }

            if (!ids.add(item.getProductoId())) { //agrego el producto o la id, si ya existe, no se agrega
                throw new StockNoSuficiente("El producto con id " + item.getProductoId() + " no puede repetirse");
            }

            int cantidad = item.getCantidad();

            //Valido que cada producto tiene stock disponible.
            Boolean disponible = paymentClient.validarStock(item.getProductoId(), cantidad);

            if(!disponible){
                //Si no hay stock disponible, se retorna un error
                throw new StockNoSuficiente("El producto con id " + item.getProductoId() + " no tiene stock suficiente");
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
            detalle.setSubTotal(subtotal);
            detalle.setNombreProducto(producto.getNombre());
            detalle.setVenta(venta);
            //Lo agrego al detalle de la lista de detalles de venta.
            detalles.add(detalle);

            //acumulo el subtotal
            total += subtotal;

        }

        //Asigno los detalles y el total a la entidad de la venta para guardarlo en la base de datos

        venta.setUsuarioId(ventaRequestDto.getUsuarioId());
        venta.setDetalle(detalles);
        venta.setUsuarioId(ventaRequestDto.getUsuarioId());
        venta.setMetodoPago(ventaRequestDto.getMetodoPago());
        venta.setMoneda(ventaRequestDto.getMoneda());
        venta.setEstado(Estado.EN_PROCESO);
        venta.setFechaVenta(LocalDateTime.now());
        //Seteo el precio total de la venta
        venta.setTotal(total);
        //Guardo la venta en la base de datos
        ventaRepository.save(venta);

        venta.setBuyOrder("fm-" + venta.getId());

        try {
            ventaRepository.save(venta);
        }catch (Exception e){
            throw new VentaNoCreada("La venta no fue creada correctamente, por favor revise los datos ingresados");
        }

        //Retorno la venta creada y respondo con un ok(200) y el mismo dto que se envio
        List<DetalleVentaDto> detalleVentaDto = detalleVentaMapper.toDtoList(detalles);
        VentaDto ventaDto = ventaMapper.ventaToDto(venta);
        ventaDto.setDetalle(detalleVentaDto);

        CreateSaleResponse createSaleResponse = new CreateSaleResponse();

        createSaleResponse.setId(venta.getId());
        createSaleResponse.setMessage("Venta creada correctamente, dirigete a localhost:8085/pago?id=" + venta.getId() + " para realizar el pago");
        createSaleResponse.setStatus(HttpStatus.CREATED);
        createSaleResponse.setTimestamp(LocalDateTime.now());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createSaleResponse);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<List<VentaResponseDto>> findAllVentas() {
        List<VentaResponseDto> ventas = ventaMapper.toResponseDtoList(ventaRepository.findAll());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ventas);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<VentaDto> findVentaById(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new VentaNoEncontrada("La venta con la id " + id + " no existe, revisa la base de datos e intenta nuevamente"));

        VentaDto ventaDto = ventaMapper.ventaToDto(venta);
        List<DetalleVentaDto> detalleVentaDto = detalleVentaMapper.toDtoList(venta.getDetalle());

        ventaDto.setDetalle(detalleVentaDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ventaDto);
    }
}
