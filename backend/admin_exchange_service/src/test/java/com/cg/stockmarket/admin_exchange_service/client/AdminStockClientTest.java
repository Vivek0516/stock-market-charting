package com.cg.stockmarket.admin_exchange_service.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

//@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class AdminStockClientTest {

    @Mock
    private AdminStockClient adminStockClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetStockById() {
        // Given
        Long stockId = 1L;

        // When
        adminStockClient.getStockById(stockId);

        // Then
        verify(adminStockClient, times(1)).getStockById(stockId);
    }
}
