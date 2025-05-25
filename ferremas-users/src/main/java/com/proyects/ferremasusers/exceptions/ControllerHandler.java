package com.proyects.ferremasusers.exceptions;


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

    @ExceptionHandler(UsuarioNoCreado.class)
    public ResponseEntity<ExceptionMessage> handleUsuarioNoCreado(UsuarioNoCreado ex) {
        ExceptionMessage message = new ExceptionMessage(ex.getMessage(), "El usuario no fue creado, por favor edita la solicitud y vuelve a intentarlo");
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsuarioNoEliminado.class)
    public ResponseEntity<ExceptionMessage> handleUsuarioNoEliminado(UsuarioNoEliminado ex) {
        ExceptionMessage message = new ExceptionMessage(ex.getMessage(), "El usuario no fue eliminado, por favor intenta nuevamente");
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(UsuarioNoEncontrado.class)
    public ResponseEntity<ExceptionMessage> handleUsuarioNoEncontrado(UsuarioNoEncontrado ex) {
        ExceptionMessage message = new ExceptionMessage(ex.getMessage(), "El usuario no fue encontrado, por favor revisa si la id es correcta");
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsuarioNoActualizado.class)
    public ResponseEntity<ExceptionMessage> handleUsuarioNoActualizado(UsuarioNoActualizado ex) {
        ExceptionMessage message = new ExceptionMessage(ex.getMessage(), "El usuario no fue actualizado, por favor intenta nuevamente");
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PasswordNoValida.class)
    public ResponseEntity<ExceptionMessage> handleContraseñaNoValida(PasswordNoValida ex) {
        ExceptionMessage message = new ExceptionMessage(ex.getMessage(), "La contraseña no es válida, debe tener al menos 8 caracteres, una letra mayúscula y un número");
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(RolNoValido.class)
    public ResponseEntity<ExceptionMessage> handleRolNoValido(RolNoValido ex) {
        ExceptionMessage message = new ExceptionMessage(ex.getMessage(), "El rol no es válido, por favor revisa los roles disponibles");
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailNoValido.class)
    public ResponseEntity<ExceptionMessage> handleEmailNoValido(EmailNoValido ex){
        ExceptionMessage message = new ExceptionMessage(ex.getMessage(), "El email no es valido, por favor revisa el formato entregado");
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
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
