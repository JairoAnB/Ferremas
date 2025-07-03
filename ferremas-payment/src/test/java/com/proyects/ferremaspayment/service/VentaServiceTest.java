package com.proyects.ferremaspayment.service;

import com.proyects.ferremaspayment.dto.*;
import com.proyects.ferremaspayment.exceptions.ProductoNoExiste;
import com.proyects.ferremaspayment.exceptions.StockNoSuficiente;
import com.proyects.ferremaspayment.exceptions.UsuarioNoEncontrado;
import com.proyects.ferremaspayment.exceptions.VentaNoEncontrada;
import com.proyects.ferremaspayment.mapper.DetalleVentaMapper;
import com.proyects.ferremaspayment.mapper.VentaMapper;
import com.proyects.ferremaspayment.model.Venta;
import com.proyects.ferremaspayment.repository.VentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {


        @Mock
        private VentaRepository ventaRepository;
        @Mock
        private PaymentClient paymentClient;
        @Mock
        private VentaMapper ventaMapper;
        @Mock
        private DetalleVentaMapper detalleVentaMapper;

        @InjectMocks
        private VentaService ventaService;

        private VentaRequestDto ventaRequestDto;
        private UsuarioRequestDto usuarioRequestDto;
        private ProductoDto productoDto;

        @BeforeEach
        void setUp() {
            ventaRequestDto = new VentaRequestDto();
            ventaRequestDto.setUsuarioId(1L);
            ItemVentaDto item = new ItemVentaDto();
            item.setProductoId(101L);
            item.setCantidad(2);
            ventaRequestDto.setProductos(Collections.singletonList(item));

            usuarioRequestDto = new UsuarioRequestDto();
            usuarioRequestDto.setId(1L);

            productoDto = new ProductoDto();
            productoDto.setId(101L);
            productoDto.setNombre("Martillo");
            productoDto.setPrecio(5000);
        }

        @Test
        @DisplayName("createVenta debería crear una venta exitosamente")
        void deberiaCrearUnaVentaExitosamente() {
            when(paymentClient.obtenerUsuario(anyLong())).thenReturn(usuarioRequestDto);
            when(paymentClient.validadProducto(anyLong())).thenReturn(productoDto);
            when(paymentClient.validarStock(anyLong(), anyInt())).thenReturn(true);
            when(paymentClient.obtenerProductos(anyLong())).thenReturn(productoDto);

            when(ventaRepository.save(any(Venta.class))).thenAnswer(invocation -> {
                Venta venta = invocation.getArgument(0);
                if (venta.getId() == null) {
                    venta.setId(1L);
                }
                return venta;
            });

            when(ventaMapper.ventaToDto(any(Venta.class))).thenReturn(new VentaDto());
            when(detalleVentaMapper.toDtoList(anyList())).thenReturn(Collections.emptyList());

            ResponseEntity<CreateSaleResponse> response = ventaService.createVenta(ventaRequestDto);

            assertNotNull(response);
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().getMessage().contains("Venta creada correctamente"));

            verify(paymentClient, times(1)).obtenerUsuario(1L);
            verify(paymentClient, times(1)).validarStock(101L, 2);
            verify(ventaRepository, times(2)).save(any(Venta.class));
            verify(ventaMapper, times(1)).ventaToDto(any(Venta.class));
        }

        @Test
        @DisplayName("createVenta debería lanzar UsuarioNoEncontrado si el usuario no existe")
        void deberiaLanzarExcepcionSiUsuarioNoExisteAlCrear() {
            when(paymentClient.obtenerUsuario(anyLong())).thenReturn(null);

            assertThrows(UsuarioNoEncontrado.class, () -> {
                ventaService.createVenta(ventaRequestDto);
            });
        }

        @Test
        @DisplayName("createVenta debería lanzar ProductoNoExiste si el producto no es válido")
        void deberiaLanzarExcepcionSiProductoNoExisteAlCrear() {
            when(paymentClient.obtenerUsuario(anyLong())).thenReturn(usuarioRequestDto);
            when(paymentClient.validadProducto(anyLong())).thenReturn(null);

            assertThrows(ProductoNoExiste.class, () -> {
                ventaService.createVenta(ventaRequestDto);
            });
        }

        @Test
        @DisplayName("createVenta debería lanzar StockNoSuficiente si no hay stock")
        void deberiaLanzarExcepcionSiNoHayStockSuficiente() {
            when(paymentClient.obtenerUsuario(anyLong())).thenReturn(usuarioRequestDto);
            when(paymentClient.validadProducto(anyLong())).thenReturn(productoDto);
            when(paymentClient.validarStock(anyLong(), anyInt())).thenReturn(false);

            assertThrows(StockNoSuficiente.class, () -> {
                ventaService.createVenta(ventaRequestDto);
            });
        }

        @Test
        @DisplayName("createVenta debería lanzar StockNoSuficiente si un producto se repite")
        void deberiaLanzarExcepcionSiUnProductoSeRepiteEnLaVenta() {
            ItemVentaDto item1 = new ItemVentaDto();
            item1.setProductoId(101L);
            item1.setCantidad(1);
            ItemVentaDto item2 = new ItemVentaDto();
            item2.setProductoId(101L);
            item2.setCantidad(1);
            ventaRequestDto.setProductos(List.of(item1, item2));

            when(paymentClient.obtenerUsuario(anyLong())).thenReturn(usuarioRequestDto);
            when(paymentClient.validadProducto(anyLong())).thenReturn(productoDto);

            assertThrows(StockNoSuficiente.class, () -> {
                ventaService.createVenta(ventaRequestDto);
            }, "El producto con id 101 no puede repetirse");
        }

        @Test
        @DisplayName("findAllVentas debería retornar una lista de ventas")
        void deberiaRetornarTodasLasVentas() {
            List<Venta> ventasRepo = Collections.singletonList(new Venta());
            List<VentaResponseDto> ventasDto = Collections.singletonList(new VentaResponseDto());

            when(ventaRepository.findAll()).thenReturn(ventasRepo);
            when(ventaMapper.toResponseDtoList(ventasRepo)).thenReturn(ventasDto);

            ResponseEntity<List<VentaResponseDto>> response = ventaService.findAllVentas();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(1, response.getBody().size());
            verify(ventaRepository).findAll();
            verify(ventaMapper).toResponseDtoList(anyList());
        }

        @Test
        @DisplayName("findVentaById debería retornar una venta si existe")
        void deberiaRetornarUnaVentaPorSuId() {
            Venta venta = new Venta();
            venta.setId(1L);
            venta.setDetalle(Collections.emptyList());

            when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
            when(ventaMapper.ventaToDto(any(Venta.class))).thenReturn(new VentaDto());
            when(detalleVentaMapper.toDtoList(anyList())).thenReturn(Collections.emptyList());

            ResponseEntity<VentaDto> response = ventaService.findVentaById(1L);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            verify(ventaRepository).findById(1L);
        }

        @Test
        @DisplayName("findVentaById debería lanzar VentaNoEncontrada si no existe")
        void deberiaLanzarExcepcionAlBuscarPorIdSiVentaNoExiste() {
            when(ventaRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(VentaNoEncontrada.class, () -> {
                ventaService.findVentaById(99L);
            });
        }
}