package com.cg.stock_service.client;

import com.cg.stock_service.dto.Exchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@SpringBootTest
public class AdminExchangeFeignClientTest {

    @Mock
    private Exchange exchange;

    @MockBean
    private AdminExchangeFeignClient adminExchangeFeignClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetExchangeById() {
        // Given
        Long exchangeId = 1L;

        // When
        adminExchangeFeignClient.getExchangeById(exchangeId);

        // Then
        verify(adminExchangeFeignClient, times(1)).getExchangeById(exchangeId);
    }
}
