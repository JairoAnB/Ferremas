package com.proyects.ferremaspayment.exceptions;

public class ErrorWebpayTransaccion extends RuntimeException {
    public ErrorWebpayTransaccion(String message) {
        super(message);
    }
}
