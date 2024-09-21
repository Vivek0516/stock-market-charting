package com.cg.stockmarket.adminuser.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class GlobalExceptionHandlerTest {
    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    private UserNotFoundException userNotFoundException;
    private Exception generalException;

    @BeforeEach
    public void setUp() {
        userNotFoundException = new UserNotFoundException("User not found with id: 1");
        generalException = new Exception("General error");
    }

    @Test
    public void testHandleUserNotFound() {
        ResponseEntity<String> response = globalExceptionHandler.handleUserNotFound(userNotFoundException);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found with id: 1", response.getBody());
    }

    @Test
    public void testHandleGeneralException() {
        ResponseEntity<String> response = globalExceptionHandler.handleGeneralException(generalException);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred: General error", response.getBody());
    }
}
