package com.proyects.ferremasinventory.exeptions;

public class ProductoNoCreado extends RuntimeException {
  public ProductoNoCreado(String message) {
    super(message);
  }
}
