package com.cg.stock_service.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testHandleStockNotFoundException() {
        StockNotFoundException exception = new StockNotFoundException("Stock not found");
        ResponseEntity<String> response = globalExceptionHandler.handleStockNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Stock not found", response.getBody());
    }

    @Test
    public void testHandleInvalidExchangeIdException() {
        InvalidExchangeIdException exception = new InvalidExchangeIdException("Invalid exchange ID");
        ResponseEntity<String> response = globalExceptionHandler.handleInvalidExchangeIdException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid exchange ID", response.getBody());
    }

    @Test
    public void testHandleGeneralException() {
        Exception exception = new Exception("General error");
        ResponseEntity<String> response = globalExceptionHandler.handleGeneralException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred: General error", response.getBody());
    }
}
