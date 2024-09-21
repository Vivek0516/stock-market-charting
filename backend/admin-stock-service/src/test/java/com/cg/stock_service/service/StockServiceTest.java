package com.cg.stock_service.service;

import com.cg.stock_service.client.AdminExchangeFeignClient;
import com.cg.stock_service.dto.Exchange;
import com.cg.stock_service.exception.InvalidExchangeIdException;
import com.cg.stock_service.exception.StockNotFoundException;
import com.cg.stock_service.model.Stock;
import com.cg.stock_service.repository.StockRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private AdminExchangeFeignClient adminExchangeFeignClient;


    @InjectMocks
    private StockService stockService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    private List<Stock> readStocksFromJson() throws IOException {
        ClassPathResource resource = new ClassPathResource("stock-data.json");
        return objectMapper.readValue(resource.getFile(), objectMapper.getTypeFactory().constructCollectionType(List.class, Stock.class));
    }

    @Test
    public void testGetAllStocks() throws IOException {
        List<Stock> stocks = readStocksFromJson();
        when(stockRepository.findAll()).thenReturn(stocks);

        List<Stock> result = stockService.getAllStocks();
        assertEquals(1, result.size());
        assertEquals("AAPL", result.get(0).getName());
    }

    @Test
    public void testGetStockById() throws IOException {
        List<Stock> stocks = readStocksFromJson();
        when(stockRepository.findById(1L)).thenReturn(Optional.of(stocks.get(0)));

        Optional<Stock> foundStock = stockService.getStockById(1L);
        assertEquals("AAPL", foundStock.get().getName());
    }

    @Test
    public void testAddStock() throws IOException {
        Stock stock = readStocksFromJson().get(0);
        when(adminExchangeFeignClient.getExchangeById(1L)).thenReturn(new Exchange(1L, "NYSE"));
        when(stockRepository.save(stock)).thenReturn(stock);

        Stock addedStock = stockService.addStock(stock);
        assertEquals("AAPL", addedStock.getName());
    }

    @Test
    public void testUpdateStock() throws IOException {
        Stock stock = readStocksFromJson().get(0);
        when(stockRepository.existsById(1L)).thenReturn(true);
        when(adminExchangeFeignClient.getExchangeById(1L)).thenReturn(new Exchange(1L, "NYSE"));
        when(stockRepository.save(stock)).thenReturn(stock);

        Stock updatedStock = stockService.updateStock(1L, stock);
        assertEquals("AAPL", updatedStock.getName());
    }

    @Test
    public void testDeleteStock() {
        when(stockRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(stockRepository).deleteById(anyLong());
        stockService.deleteStock(1L);
        verify(stockRepository, times(1)).existsById(1L);
        verify(stockRepository, times(1)).deleteById(1L);
    }


    @Test
    public void testDeleteStockNotFound() {
        when(stockRepository.existsById(1L)).thenReturn(false);

        assertThrows(StockNotFoundException.class, () -> {
            stockService.deleteStock(1L);
        });

        verify(stockRepository, times(1)).existsById(1L);
        verify(stockRepository, never()).deleteById(anyLong());
    }


    @Test
    public void testGetStocksByExchangeId() throws IOException {
        List<Stock> stocks = readStocksFromJson();
        when(stockRepository.findByExchangeId(1L)).thenReturn(stocks);

        List<Stock> result = stockService.getStocksByExchangeId(1L);
        assertEquals(1, result.size());
        assertEquals("AAPL", result.get(0).getName());
    }

    @Test
    public void testUpdateStockInvalidExchangeId() throws IOException {
        Stock stock = readStocksFromJson().get(0);
        stock.setExchangeId(999L);  // Invalid exchange ID
        when(stockRepository.existsById(1L)).thenReturn(true);
        when(adminExchangeFeignClient.getExchangeById(999L)).thenReturn(null); // Simulate exchange not found

        assertThrows(InvalidExchangeIdException.class, () -> {
            stockService.updateStock(1L, stock);
        });

        verify(adminExchangeFeignClient, times(1)).getExchangeById(999L);
        verify(stockRepository, never()).save(any(Stock.class));
    }


    @Test
    public void testInvalidExchangeIdException() {
        // Create a stock object with an invalid exchange ID
        Stock stock = new Stock(1L, "AAPL", 150.0, 999L); // 999L is an invalid exchange ID

        // Mock the adminExchangeFeignClient to return null when the invalid exchange ID is passed
        when(adminExchangeFeignClient.getExchangeById(anyLong())).thenReturn(null);

        // Expect InvalidExchangeIdException to be thrown
        assertThrows(InvalidExchangeIdException.class, () -> {
            stockService.addStock(stock);
        });

        // Verify the feign client was called with the invalid exchange ID
        verify(adminExchangeFeignClient, times(1)).getExchangeById(999L);
    }


    @Test
    public void testStockNotFoundException() {
        when(stockRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(StockNotFoundException.class, () -> {
            stockService.deleteStock(1L);
        });
    }

    @Test
    public void testUpdateStockNotFound() throws IOException {
        Stock stock = readStocksFromJson().get(0);
        when(stockRepository.existsById(1L)).thenReturn(false);  // Simulate stock not found

        assertThrows(StockNotFoundException.class, () -> {
            stockService.updateStock(1L, stock);
        });

        verify(stockRepository, times(1)).existsById(1L);
        verify(adminExchangeFeignClient, never()).getExchangeById(anyLong());
        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    public void testDeleteStockExists() {
        when(stockRepository.existsById(1L)).thenReturn(true);  // Stock exists
        doNothing().when(stockRepository).deleteById(1L);

        stockService.deleteStock(1L);

        verify(stockRepository, times(1)).existsById(1L);
        verify(stockRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testUploadStockData() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);

        doNothing().when(stockService).uploadStockData(mockFile);

        stockService.uploadStockData(mockFile);

        verify(stockService, times(1)).uploadStockData(mockFile);
    }

    @Test
    public void testGenerateStockChart() throws IOException {
        Stock stock = readStocksFromJson().get(0);
        when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));

        doNothing().when(userGraphFeignClient).generateStockChart(any(StockData.class));

        stockService.generateStockChart(1L);

        verify(stockRepository, times(1)).findById(1L);
        verify(userGraphFeignClient, times(1)).generateStockChart(any(StockData.class));
    }

    @Test
    public void testGenerateStockChartStockNotFound() {
        when(stockRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(StockNotFoundException.class, () -> {
            stockService.generateStockChart(1L);
        });

        verify(stockRepository, times(1)).findById(1L);
        verify(userGraphFeignClient, never()).generateStockChart(any(StockData.class));
    }

    @Test
    public void testSaveAll() {
        // Given
        Stock stock1 = new Stock(null, "AAPL", 150.0, 1L);
        Stock stock2 = new Stock(null, "GOOGL", 2800.0, 1L);
        List<Stock> stocks = List.of(stock1, stock2);

        // When
        stockService.saveAll(stocks);

        // Then
        verify(stockRepository, times(1)).saveAll(stocks);
    }

}
