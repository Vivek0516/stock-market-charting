package com.cg.stock_service.exception;

public class InvalidExchangeIdException extends RuntimeException {
    public InvalidExchangeIdException(String message) {
        super(message);
    }
}
