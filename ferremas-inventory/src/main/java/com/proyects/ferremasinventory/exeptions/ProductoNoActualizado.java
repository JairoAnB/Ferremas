package com.proyects.ferremasinventory.exeptions;

public class ProductoNoActualizado extends RuntimeException {
    public ProductoNoActualizado(String message) {
        super(message);
    }
}
