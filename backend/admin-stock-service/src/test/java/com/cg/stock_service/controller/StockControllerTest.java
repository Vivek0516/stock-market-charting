package com.cg.stock_service.controller;

import com.cg.stock_service.dto.FileUploadResponse;
import com.cg.stock_service.model.Stock;
import com.cg.stock_service.dto.StockPerformance;
import com.cg.stock_service.service.StockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StockController.class)
public class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockService stockService;

    @Test
    public void testGetAllStocks() throws Exception {
        when(stockService.getAllStocks()).thenReturn(List.of(new Stock(1L, "AAPL", 150.0, 1L)));

        mockMvc.perform(MockMvcRequestBuilders.get("/stocks")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("AAPL"));
    }

    @Test
    public void testGetStockById() throws Exception {
        Stock stock = new Stock(1L, "AAPL", 150.0, 1L);
        when(stockService.getStockById(anyLong())).thenReturn(Optional.of(stock));

        mockMvc.perform(MockMvcRequestBuilders.get("/stocks/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("AAPL"));
    }

    @Test
    public void testAddStock() throws Exception {
        Stock stock = new Stock(1L, "AAPL", 150.0, 1L);
        when(stockService.addStock(any())).thenReturn(stock);

        mockMvc.perform(MockMvcRequestBuilders.post("/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"AAPL\",\"price\":150.0,\"exchangeId\":1}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("AAPL"));
    }

    @Test
    public void testUpdateStock() throws Exception {
        Stock stock = new Stock(1L, "AAPL", 150.0, 1L);
        when(stockService.updateStock(anyLong(), any())).thenReturn(stock);

        mockMvc.perform(MockMvcRequestBuilders.put("/stocks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"AAPL\",\"price\":150.0,\"exchangeId\":1}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("AAPL"));
    }

    @Test
    public void testDeleteStock() throws Exception {
        when(stockService.getStockById(1L)).thenReturn(Optional.of(new Stock(1L, "AAPL", 150.0, 1L)));
        doNothing().when(stockService).deleteStock(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/stocks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteStock_NotFound() throws Exception {
        when(stockService.getStockById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.delete("/stocks/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetStocksByExchangeId() throws Exception {
        when(stockService.getStocksByExchangeId(anyLong())).thenReturn(List.of(new Stock(1L, "AAPL", 150.0, 1L)));

        mockMvc.perform(MockMvcRequestBuilders.get("/stocks/exchange/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("AAPL"));
    }

    @Test
    public void testUploadFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "stocks.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "dummy content".getBytes());
        FileUploadResponse response = new FileUploadResponse("File uploaded successfully", 100L);

        when(stockService.isValidExcelFile(any())).thenReturn(true);
        doNothing().when(stockService).saveExcelData(any());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/stocks/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("File uploaded successfully"));
    }

    @Test
    public void testGenerateStockChart() throws Exception {
        StockPerformance performance = new StockPerformance("AAPL", List.of(150.0, 152.0, 153.5));
        when(stockService.generateStockChart(anyLong())).thenReturn(performance);

        mockMvc.perform(MockMvcRequestBuilders.get("/stocks/1/chart")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.stockName").value("AAPL"));
    }

    @Test
    public void testDownloadTemplate() throws Exception {
        byte[] templateBytes = "template content".getBytes();
        when(stockService.getTemplateExcelFile()).thenReturn(new ByteArrayInputStream(templateBytes));

        mockMvc.perform(MockMvcRequestBuilders.get("/stocks/download-template"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=template.xlsx"))
                .andExpect(content().bytes(templateBytes));
    }

    @Test
    public void testAddStock_IllegalArgumentException() throws Exception {
        when(stockService.addStock(any())).thenThrow(new IllegalArgumentException("Invalid stock data"));

        mockMvc.perform(MockMvcRequestBuilders.post("/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"InvalidStock\",\"price\":-100.0,\"exchangeId\":1}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    public void testUpdateStock_IllegalArgumentException() throws Exception {
        when(stockService.updateStock(anyLong(), any())).thenThrow(new IllegalArgumentException("Invalid stock data"));

        mockMvc.perform(MockMvcRequestBuilders.put("/stocks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"InvalidStock\",\"price\":-100.0,\"exchangeId\":1}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    public void testSaveStockData() throws Exception {
        doNothing().when(stockService).saveAll(anyList());

        mockMvc.perform(MockMvcRequestBuilders.post("/stocks/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"name\":\"AAPL\",\"price\":150.0,\"exchangeId\":1},{\"name\":\"GOOGL\",\"price\":2800.0,\"exchangeId\":1}]")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Stocks saved successfully"));

        verify(stockService).saveAll(argThat(stocks ->
                stocks.size() == 2 &&
                        stocks.get(0).getName().equals("AAPL") &&
                        stocks.get(1).getName().equals("GOOGL")
        ));
    }
}
