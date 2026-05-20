package com.ecommers.cart.exception;

public record ErrorResponse(int status, String message, String timestamp) {}
