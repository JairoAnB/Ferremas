package com.proyects.ferremascategory.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerHandler {

    @ExceptionHandler(CategoriaNoActualizada.class)
    public ResponseEntity<ExceptionMessage> handlerCategoriaNoActualizada(CategoriaNoActualizada ex) {
        ExceptionMessage error = new ExceptionMessage(ex.getMessage(), "Ocurrio un error al intentar actualizar la categoria, revise la solicitud e intente nuevamente");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CategoriaNoEliminada.class)
    public ResponseEntity<ExceptionMessage> handlerCategoriaNoEliminada(CategoriaNoEliminada ex) {
        ExceptionMessage error = new ExceptionMessage(ex.getMessage(), "Ocurrio un error al intentar eliminar la categoria, Intente nuevamente.");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CategoriaNoEncontrada.class)
    public ResponseEntity<ExceptionMessage> handlerCategoriaNoEncontrada(CategoriaNoEncontrada ex) {
        ExceptionMessage error = new ExceptionMessage(ex.getMessage(), "La categoria no fue encontrada, revise la id e intente nuevamente");
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CategoriaNoCreada.class)
    public ResponseEntity<ExceptionMessage> handlerCategoriaNoCreada(CategoriaNoCreada ex) {
        ExceptionMessage error = new ExceptionMessage(ex.getMessage(), "Ocurrio un error al intentar crear la categoria, revise los parametros e intente nuevamente");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


}
