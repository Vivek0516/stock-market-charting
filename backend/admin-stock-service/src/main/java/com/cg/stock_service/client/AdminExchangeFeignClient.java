package com.cg.stock_service.client;

import com.cg.stock_service.dto.Exchange;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for communicating with the Admin Exchange Service.
 * Used for validating exchanges when managing stocks.
 */
@FeignClient(name = "admin-exchange-service")
public interface AdminExchangeFeignClient {

    /**
     * Retrieves exchange details by exchange ID.
     *
     * @param id The ID of the exchange.
     * @return Exchange object containing exchange details.
     */
    @GetMapping("/exchanges/{id}")
    Exchange getExchangeById(@PathVariable("id") Long id);
}
