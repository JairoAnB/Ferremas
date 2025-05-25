package com.proyects.ferremascategory.service;


import com.proyects.ferremascategory.dto.CategoriaCreateDto;
import com.proyects.ferremascategory.dto.CategoriaDto;
import com.proyects.ferremascategory.dto.CategoriaResponseDto;
import com.proyects.ferremascategory.dto.CategoriaUpdateDto;
import com.proyects.ferremascategory.exceptions.CategoriaNoCreada;
import com.proyects.ferremascategory.exceptions.CategoriaNoEncontrada;
import com.proyects.ferremascategory.mapper.CategoriaMapper;
import com.proyects.ferremascategory.model.Categoria;
import com.proyects.ferremascategory.repository.CategoriaRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {


    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    public CategoriaService(CategoriaRepository categoriaRepository, CategoriaMapper categoriaMapper) {
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
    }

    //Operaciones CRUD
    @Transactional(readOnly = true)
    public ResponseEntity<List<CategoriaDto>> findAllCategorias(){
        List<CategoriaDto> categoriaDtos = categoriaMapper.toDtoList(categoriaRepository.findAll());

        if (categoriaDtos.isEmpty()) {
            return ResponseEntity
                    .noContent()
                    .build();
        }

        return ResponseEntity
                .ok(categoriaDtos);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<CategoriaDto> findCategoriaById(Long id){
        Categoria categoriaEntity = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNoEncontrada("La categoria con la id " + id + " no existe"));

        CategoriaDto categoriaDto = categoriaMapper.toDto(categoriaEntity);

        return ResponseEntity.status(HttpStatus.OK)
                .body(categoriaDto);
    }

    @Transactional
    public ResponseEntity<CategoriaResponseDto> createCategoria(@Valid CategoriaCreateDto categoriaCreateDto) {
        // verfico si la categoria existe mediante una consulta personalizada en el repositorio
        // Obtengo un opcional que me retorna solo un valor, que no puede ser nulo
        Optional<Categoria> categoriaExistente = categoriaRepository.findByNombre(categoriaCreateDto.getNombre());

        // si el opcional no esta vacio, significa que ya existe una categoria con ese nombre
        // por lo tanto retorno un response entity con un mensaje de error personalizado mas un status 409 de conflicto
        if (!categoriaExistente.isEmpty()) {

            CategoriaResponseDto categoriaResponseDto = new CategoriaResponseDto();

            categoriaResponseDto.setId(categoriaExistente.get().getId());
            categoriaResponseDto.setMessage("Error, no pueden haber categorias duplicadas. Por favor intente nuevamente");
            categoriaResponseDto.setStatus(HttpStatus.CONFLICT);
            categoriaResponseDto.setTimestamp(LocalDateTime.now());


            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(categoriaResponseDto);
        }

        // si la categoria no existe, paso el DTO recibido a una entidad del modelo Categoria y sigo con el flujo normal

     try{
         Categoria categoriaEntity = categoriaMapper.toEntity(categoriaCreateDto);

         Categoria categoriaCreada = categoriaRepository.save(categoriaEntity);

         CategoriaDto categoriaDto = categoriaMapper.toDto(categoriaCreada);

         CategoriaResponseDto categoriaResponseDto = new CategoriaResponseDto();

            categoriaResponseDto.setId(categoriaDto.getId());
            categoriaResponseDto.setMessage("Categoria creada correctamente");
            categoriaResponseDto.setStatus(HttpStatus.CREATED);
            categoriaResponseDto.setTimestamp(LocalDateTime.now());

         return ResponseEntity
                 .status(HttpStatus.CREATED)
                 .body(categoriaResponseDto);

         } catch (CategoriaNoCreada e) {
             throw new CategoriaNoCreada("La categoria no fue creada correctamente");
         }

    }

    @Transactional
    public ResponseEntity<CategoriaResponseDto> updateCategoria(@Valid Long id, CategoriaUpdateDto categoriaUpdateDto){
        Categoria categoriaExistente = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNoEncontrada("La categoria con la id " + id + " no existe"));

        categoriaExistente.setDescripcion(categoriaUpdateDto.getDescripcion());
        categoriaExistente.setNombre(categoriaUpdateDto.getNombre());
        categoriaExistente.setImagen(categoriaUpdateDto.getImagen());

        Categoria categoriaActualizada = categoriaRepository.save(categoriaExistente);

        CategoriaDto categoriaDto = categoriaMapper.toDto(categoriaActualizada);

        CategoriaResponseDto categoriaResponseDto = new CategoriaResponseDto();
        categoriaResponseDto.setId(categoriaDto.getId());
        categoriaResponseDto.setMessage("Categoria actualizada correctamente");
        categoriaResponseDto.setStatus(HttpStatus.OK);
        categoriaResponseDto.setTimestamp(LocalDateTime.now());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(categoriaResponseDto);
    }

    @Transactional
    public ResponseEntity<CategoriaResponseDto> deleteCategoria(Long id){
        Categoria categoriaExistente = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNoEncontrada("La categoria con la id " + id + " no existe"));

        categoriaRepository.delete(categoriaExistente);

        CategoriaResponseDto categoriaResponseDto = new CategoriaResponseDto();
        categoriaResponseDto.setId(categoriaExistente.getId());
        categoriaResponseDto.setMessage("Categoria eliminada correctamente");
        categoriaResponseDto.setStatus(HttpStatus.OK);
        categoriaResponseDto.setTimestamp(LocalDateTime.now());


        return ResponseEntity.status(HttpStatus.OK)
                .body(categoriaResponseDto);
    }
}
