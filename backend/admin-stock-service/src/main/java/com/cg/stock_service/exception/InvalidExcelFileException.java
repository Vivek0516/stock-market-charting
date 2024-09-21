package com.cg.stock_service.exception;

public class InvalidExcelFileException extends RuntimeException {
    public InvalidExcelFileException(String message) {
        super(message);
    }
}
