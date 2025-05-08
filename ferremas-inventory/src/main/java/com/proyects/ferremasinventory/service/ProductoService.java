package com.proyects.ferremasinventory.service;


import com.proyects.ferremasinventory.dto.ProductoDto;
import com.proyects.ferremasinventory.exeptions.ProductoNoActualizado;
import com.proyects.ferremasinventory.exeptions.ProductoNoCreado;
import com.proyects.ferremasinventory.exeptions.ProductoNoEliminado;
import com.proyects.ferremasinventory.exeptions.ProductoNoEncontrado;
import com.proyects.ferremasinventory.mapper.ProductoMapper;
import com.proyects.ferremasinventory.model.Producto;
import com.proyects.ferremasinventory.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    public ProductoService(ProductoRepository productoRepository, ProductoMapper productoMapper) {
        this.productoRepository = productoRepository;
        this.productoMapper = productoMapper;
    }

    // Método para buscar todos los productos
    @Transactional(readOnly = true)
    public ResponseEntity<List<ProductoDto>> findAllProductos(){
        List<Producto> productoEntidades = productoRepository.findAll();

        if (productoEntidades.isEmpty()) {
            return ResponseEntity.noContent().build(); // SI NO HAY DATOS SE RETORNA NO CONTENT 204
        }

        List<ProductoDto> productoDtos = productoMapper.toDtoList(productoEntidades);

        return ResponseEntity.status(HttpStatus.OK)
                .body(productoDtos);
    }

    // Método para buscar un producto por ID
    @Transactional(readOnly = true)
    public ResponseEntity<ProductoDto> findProductoById(Long id){
        Producto productoEntity = productoRepository.findById(id)
                //cambiar por controllerADVICE MAS ADELANTE
                .orElseThrow(() -> new ProductoNoEncontrado("El producto con la id " + id + " no existe"));

        ProductoDto productoDto = productoMapper.toDto(productoEntity);

        return ResponseEntity.status(HttpStatus.OK)
                .body(productoDto);
    }

    @Transactional
    public ResponseEntity<ProductoDto> createProducto(ProductoDto productoDto){

        try{
            //Paso el DTO recibido a una entidad del modelo Producto
            Producto productoEntity = productoMapper.toEntity(productoDto);

            productoEntity.setFechaCreacion(LocalDate.now());

            //Guardo la entidad creada en la base de datos
            Producto entidadGuardada = productoRepository.save(productoEntity);

            //Paso la entidad guardada a DTO
            ProductoDto productoGuardado = productoMapper.toDto(entidadGuardada);
            //Retorno el DTO guardado, con el status de creado
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(productoGuardado);

        } catch (Exception e) {
            throw new ProductoNoCreado("El producto no fue creado correctamente, por favor revisa los datos ingresados");
        }
    }

    @Transactional
    public ResponseEntity<ProductoDto> updateProducto(Long id, ProductoDto productoDto){

        //Busco el producto existente en la base de datos
        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontrado("El producto con la id " + id + " no existe"));

        try{
            productoExistente.setCodigo(productoDto.getCodigo());
            productoExistente.setNombre(productoDto.getNombre());
            productoExistente.setMarca(productoDto.getMarca());
            productoExistente.setPrecio(productoDto.getPrecio());
            productoExistente.setStock(productoDto.getStock());
            productoExistente.setDescripcion(productoDto.getDescripcion());
            productoExistente.setCategoriaId(productoDto.getCategoriaId());
            productoExistente.setFechaCreacion(productoDto.getFechaCreacion());


            //Guardo la entidad actualizada en la base de datos
            Producto entidadActualizada = productoRepository.save(productoExistente);
            //Paso la entidad actualizada a DTO
            ProductoDto productoActualizado = productoMapper.toDto(entidadActualizada);

            //Retorno el DTO actualizado, con el status de OK
            return ResponseEntity.status(HttpStatus.OK)
                    .body(productoActualizado);

        } catch (Exception e) {
            throw new ProductoNoActualizado("El producto con la id " + id + " no fue actualizado correctamente");
        }
    }

    @Transactional
    public ResponseEntity<String> deleteProducto(Long id){
        //Busco el producto existente en la base de datos
        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontrado("El producto con la id " + id + " no existe"));
        try{
            //Elimino el producto existente
            productoRepository.delete(productoExistente);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("El producto con la id " + id + " fue eliminado correctamente.");
        } catch (Exception e) {
            throw new ProductoNoEliminado("El producto con la id " + id + " no fue eliminado");
        }
    }
}
