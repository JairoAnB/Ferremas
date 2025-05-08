package com.proyects.ferremasinventory.exeptions;

public class ProductoNoEncontrado extends RuntimeException {
    public ProductoNoEncontrado(String message) {
        super(message);
    }
}
