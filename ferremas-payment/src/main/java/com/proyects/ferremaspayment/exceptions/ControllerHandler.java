package com.proyects.ferremaspayment.exceptions;

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

    @ExceptionHandler(VentaNoCreada.class)
    public ResponseEntity<ExceptionMessage> handlerVentaNoCreada(VentaNoCreada ex) {
        ExceptionMessage error = new ExceptionMessage(ex.getMessage(), "Ocurrio un error al intentar crear la venta, revisa los parametros ingresados");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(VentaNoEncontrada.class)
    public ResponseEntity<ExceptionMessage> handlerVentaNoEncontrada(VentaNoEncontrada ex) {
        ExceptionMessage error = new ExceptionMessage(ex.getMessage(), "La venta no fue encontrada, revisa la base de datos si ya existe o cree la venta");
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(VentaNoEliminada.class)
    public ResponseEntity<ExceptionMessage> handlerVentaNoEliminada(VentaNoEliminada ex) {
        ExceptionMessage error = new ExceptionMessage(ex.getMessage(), "Ocurrio un error al intentar eliminar la venta, revisa la base de datos si ya existe o cree la venta");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(StockNoActualizado.class)
    public ResponseEntity<ExceptionMessage> handlerStockNoActualizado(StockNoActualizado ex) {
        ExceptionMessage error = new ExceptionMessage(ex.getMessage(), "Ocurrio un error al intentar actualizar el stock, revise la solicitud");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(StockNoSuficiente.class)
    public ResponseEntity<ExceptionMessage> handlerStockNoSuficiente(StockNoSuficiente ex) {
        ExceptionMessage error = new ExceptionMessage(ex.getMessage(), "El stock no es suficiente, por favor revise la cantidad solicitada o revise el stock del producto");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ProductoNoExiste.class)
    public ResponseEntity<ExceptionMessage> handlerProductoNoExiste(ProductoNoExiste ex) {
        ExceptionMessage error = new ExceptionMessage(ex.getMessage(), "El producto no existe, revise la id e intente nuevamente");
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(ProductoNoObtenido.class)
    public ResponseEntity<ExceptionMessage> handlerProductoNoObtenido(ProductoNoObtenido ex) {
        ExceptionMessage error = new ExceptionMessage(ex.getMessage(), "Ocurrio un error al intentar obtener el producto del microservicio de inventario, revise la solicitud e intente nuevamente");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ErrorWebpayConfirmarPago.class)
    public ResponseEntity<ExceptionMessage> handlerErrorWebpay(ErrorWebpayConfirmarPago ex) {
        ExceptionMessage error = new ExceptionMessage(ex.getMessage(), "Ocurrio un error al intentar aceptar el pago, revise la solicitud o los servidores de Webpay");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ErrorAlActualizar.class)
    public ResponseEntity<ExceptionMessage> handlerErrorAlActualizar(ErrorAlActualizar ex) {
        ExceptionMessage error = new ExceptionMessage(ex.getMessage(), "Ocurrio un error al intentar actualizar el stock, revise los datos ingresados o el metodo utilizado");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ErrorWebpayTransaccion.class)
    public ResponseEntity<ExceptionMessage> handlerErrorWebpayTransaccion(ErrorWebpayTransaccion ex) {
        ExceptionMessage error = new ExceptionMessage(ex.getMessage(), "Ocurrio un error al intentar realizar la transaccion");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CambioNoValido.class)
    public ResponseEntity<ExceptionMessage> handlerCambioNoValido(CambioNoValido ex) {
        ExceptionMessage error = new ExceptionMessage(ex.getMessage(), "Ocurrio un error al intentar realizar el cambio, los datos validos para el cambio son: dolar o euro");
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
    @ExceptionHandler(UsuarioNoEncontrado.class)
    public ResponseEntity<ExceptionMessage> handlerUsuarioNoEncontrado(UsuarioNoEncontrado ex) {
        ExceptionMessage error = new ExceptionMessage(ex.getMessage(), "El usuario no fue encontrado en la base de datos");
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

}
