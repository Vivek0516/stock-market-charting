package com.cg.stockmarket.admin_exchange_service.controller;

import com.cg.stockmarket.admin_exchange_service.dto.StockDTO;
import com.cg.stockmarket.admin_exchange_service.exception.NotFoundException;
import com.cg.stockmarket.admin_exchange_service.model.Exchange;
import com.cg.stockmarket.admin_exchange_service.service.ExchangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest(ExchangeController.class)
@SpringBootTest
public class ExchangeControllerTest {

    @InjectMocks
    private ExchangeController exchangeController;

    @MockBean
    private ExchangeService exchangeService;

    private MockMvc mockMvc;
    private Exchange exchange;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(exchangeController).build();
        exchange = new Exchange();
        exchange.setId(1L);
        exchange.setName("NYSE");
        exchange.setCountry("USA");
    }

    @Test
    void testGetAllExchanges() throws Exception {
        when(exchangeService.getAllExchanges()).thenReturn(List.of(exchange));
        mockMvc.perform(get("/exchanges"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("NYSE"));
    }

    @Test
    void testGetExchangeById() throws Exception {
        when(exchangeService.getExchangeById(1L)).thenReturn(exchange);
        mockMvc.perform(get("/exchanges/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NYSE"));
    }

//    @Test
//    void testGetExchangeByIdNotFound() throws Exception {
//        // Mocking the behavior of the service to throw NotFoundException
//        when(exchangeService.getExchangeById(1L)).thenThrow(new NotFoundException("Exchange not found with id 1"));
//
//        // Performing the request and asserting the results
//        mockMvc.perform(MockMvcRequestBuilders.get("/exchanges/1"))
//                .andExpect(status().isNotFound())
//                .andExpect(content().string("Exchange not found with id 1"));
//    }



    @Test
    void testGetExchangesByCountry() throws Exception {
        when(exchangeService.getExchangesByCountry("USA")).thenReturn(List.of(exchange));
        mockMvc.perform(get("/exchanges/country/USA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("NYSE"));
    }

    @Test
    void testAddExchange() throws Exception {
        when(exchangeService.addExchange(any(Exchange.class))).thenReturn(exchange);
        mockMvc.perform(post("/exchanges")
                        .contentType("application/json")
                        .content("{\"name\":\"NYSE\",\"country\":\"USA\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("NYSE"));
    }

    @Test
    void testUpdateExchange() throws Exception {
        when(exchangeService.updateExchange(eq(1L), any(Exchange.class))).thenReturn(exchange);
        mockMvc.perform(put("/exchanges/1")
                        .contentType("application/json")
                        .content("{\"name\":\"NYSE\",\"country\":\"USA\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NYSE"));
    }

    @Test
    void testDeleteExchange() throws Exception {
        doNothing().when(exchangeService).deleteExchange(1L);
        mockMvc.perform(delete("/exchanges/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetStockById() throws Exception {
        StockDTO stockDTO = new StockDTO();
        when(exchangeService.getStockFromAdminStockService(1L)).thenReturn(stockDTO);
        mockMvc.perform(get("/exchanges/stock/1"))
                .andExpect(status().isOk());
    }
}
