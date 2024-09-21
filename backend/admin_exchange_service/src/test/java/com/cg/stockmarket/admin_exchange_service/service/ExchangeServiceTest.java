package com.cg.stockmarket.admin_exchange_service.service;

import com.cg.stockmarket.admin_exchange_service.client.AdminStockClient;
import com.cg.stockmarket.admin_exchange_service.dto.StockDTO;
import com.cg.stockmarket.admin_exchange_service.exception.NotFoundException;
import com.cg.stockmarket.admin_exchange_service.model.Exchange;
import com.cg.stockmarket.admin_exchange_service.repository.ExchangeRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.InputStream;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExchangeServiceTest {

    @InjectMocks
    private ExchangeService exchangeService;

    @Mock
    private ExchangeRepository exchangeRepository;

    @Mock
    private AdminStockClient adminStockClient;

    private List<Exchange> exchanges;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);


        ObjectMapper objectMapper = new ObjectMapper();
        InputStream inputStream = getClass().getResourceAsStream("/exchange-data.json");
        exchanges = objectMapper.readValue(inputStream, new TypeReference<List<Exchange>>() {});
    }

    @Test
    public void testGetAllExchanges() {
        when(exchangeRepository.findAll()).thenReturn(exchanges);

        List<Exchange> result = exchangeService.getAllExchanges();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("NYSE", result.get(0).getName());
    }

    @Test
    public void testGetExchangeById() {
        Exchange exchange = exchanges.get(0);
        when(exchangeRepository.findById(1L)).thenReturn(Optional.of(exchange));

        Exchange result = exchangeService.getExchangeById(1L);

        assertNotNull(result);
        assertEquals("NYSE", result.getName());
    }

    @Test
    public void testGetExchangeByIdNotFound() {
        when(exchangeRepository.findById(3L)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            exchangeService.getExchangeById(3L);
        });

        assertEquals("Exchange not found with id 3", thrown.getMessage());
    }

    @Test
    public void testAddExchange() {
        Exchange newExchange = new Exchange(3L, "BSE", "India");
        when(exchangeRepository.save(newExchange)).thenReturn(newExchange);

        Exchange result = exchangeService.addExchange(newExchange);

        assertNotNull(result);
        assertEquals("BSE", result.getName());
    }

    @Test
    public void testUpdateExchange() {
        Exchange updatedExchange = new Exchange(1L, "NASDAQ", "USA");
        when(exchangeRepository.existsById(1L)).thenReturn(true);
        when(exchangeRepository.save(updatedExchange)).thenReturn(updatedExchange);

        Exchange result = exchangeService.updateExchange(1L, updatedExchange);

        assertNotNull(result);
        assertEquals("NASDAQ", result.getName());
    }

    @Test
    public void testUpdateExchangeNotFound() {
        Exchange updatedExchange = new Exchange(1L, "NASDAQ", "USA");
        when(exchangeRepository.existsById(1L)).thenReturn(false);

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            exchangeService.updateExchange(1L, updatedExchange);
        });

        assertEquals("Exchange not found with id 1", thrown.getMessage());
    }

    @Test
    public void testDeleteExchange() {
        when(exchangeRepository.existsById(1L)).thenReturn(true);

        exchangeService.deleteExchange(1L);

        verify(exchangeRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteExchangeNotFound() {
        when(exchangeRepository.existsById(1L)).thenReturn(false);

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            exchangeService.deleteExchange(1L);
        });

        assertEquals("Exchange not found with id 1", thrown.getMessage());
    }

    @Test
    public void testGetExchangesByCountry() {
        when(exchangeRepository.findByCountry("USA")).thenReturn(exchanges);

        List<Exchange> result = exchangeService.getExchangesByCountry("USA");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("NYSE", result.get(0).getName());
    }

    @Test
    public void testGetStockFromAdminStockService() {
        StockDTO stockDTO = new StockDTO(1L, "AAPL", 150.0, 1L);
        when(adminStockClient.getStockById(1L)).thenReturn(stockDTO);

        StockDTO result = exchangeService.getStockFromAdminStockService(1L);

        assertNotNull(result);
        assertEquals("AAPL", result.getName());
    }
}
