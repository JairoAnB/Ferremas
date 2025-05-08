package com.proyects.ferremasinventory.exeptions;

public class ProductoNoEliminado extends RuntimeException {
    public ProductoNoEliminado(String message) {
        super(message);
    }
}
