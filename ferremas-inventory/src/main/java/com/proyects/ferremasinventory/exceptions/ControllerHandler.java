package com.proyects.ferremasinventory.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ControllerHandler {

    @ExceptionHandler(ProductoNoActualizado.class)
    public ResponseEntity<ExceptionMessage> manejarProductoNoActualizado(ProductoNoActualizado ex) {
        ExceptionMessage error = new ExceptionMessage(ex.getMessage(), "Ocurrio un error al intentar actualizar el producto, por favor intenta nuevamente");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductoNoEliminado.class)
    public ResponseEntity<ExceptionMessage> manejarProductoNoEliminado(ProductoNoEliminado ex) {
        ExceptionMessage error = new ExceptionMessage(ex.getMessage(), "Ocurrio un error al intentar eliminar el producto, por favor intenta nuevamente");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ProductoNoEncontrado.class)
    public ResponseEntity<ExceptionMessage> manejarProductoNoEncontrado(ProductoNoEncontrado ex) {
        ExceptionMessage error = new ExceptionMessage(ex.getMessage(), "El producto no pudo ser encontrado, por favor revisa si la id es correcta");
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProductoNoCreado.class)
    public ResponseEntity<ExceptionMessage> manejarProductoNoCreado(ProductoNoCreado ex) {
        ExceptionMessage error = new ExceptionMessage(ex.getMessage(), "Ocurrio un error al intentar crear el producto, por favor intenta nuevamente");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CategoriaNoEncontrada.class)
    public ResponseEntity<ExceptionMessage> manejarCategoriaNoEncontrada(CategoriaNoEncontrada ex) {
        ExceptionMessage error = new ExceptionMessage(ex.getMessage(), "La categoria no fue encontrada, Revise la id e intente nuevamente o cree una nueva categoria");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex){

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
