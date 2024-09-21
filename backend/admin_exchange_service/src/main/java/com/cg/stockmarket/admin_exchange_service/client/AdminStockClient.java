package com.cg.stockmarket.admin_exchange_service.client;

import com.cg.stockmarket.admin_exchange_service.dto.StockDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client interface for communicating with the Admin Stock Service.
 */
@FeignClient(name = "admin-stock-service")
public interface AdminStockClient {
    /**
     * Retrieves a specific stock by its ID from the Admin Stock Service.
     *
     * @param id the ID of the stock to retrieve.
     * @return StockDTO object containing stock details.
     */
    @GetMapping("/stocks/{id}")
    StockDTO getStockById(@PathVariable("id") Long id);
}
