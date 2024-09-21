package com.cg.stockmarket.admin_exchange_service.controller;

import com.cg.stockmarket.admin_exchange_service.dto.StockDTO;
import com.cg.stockmarket.admin_exchange_service.model.Exchange;
import com.cg.stockmarket.admin_exchange_service.service.ExchangeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing stock exchanges.
 */
@RestController
@RequestMapping("/exchanges")
@Tag(name = "Admin Exchange Service", description = "Endpoints for managing stock exchanges")
public class ExchangeController {

    @Autowired
    private ExchangeService exchangeService;

    /**
     * Retrieves a list of all stock exchanges.
     *
     * @return ResponseEntity containing the list of all exchanges.
     */
    @Operation(summary = "Get all exchanges", description = "Retrieve a list of all stock exchanges")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of exchanges")
    })
    @GetMapping
    public ResponseEntity<List<Exchange>> getAllExchanges() {
        List<Exchange> exchanges = exchangeService.getAllExchanges();
        return ResponseEntity.ok(exchanges);
    }

    /**
     * Retrieves a specific exchange by its ID.
     *
     * @param id the ID of the exchange to retrieve.
     * @return ResponseEntity containing the exchange.
     */
    @Operation(summary = "Get exchange by ID", description = "Retrieve a specific exchange by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved exchange"),
            @ApiResponse(responseCode = "404", description = "Exchange not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Exchange> getExchangeById(
            @Parameter(description = "ID of the exchange to retrieve") @PathVariable Long id) {
        return ResponseEntity.ok(exchangeService.getExchangeById(id));
    }


    /**
     * Retrieves a list of exchanges filtered by country.
     *
     * @param country the country to filter exchanges by.
     * @return ResponseEntity containing the list of exchanges from the specified country.
     */
    @Operation(summary = "Get exchanges by country", description = "Retrieve a list of exchanges by country")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of exchanges")
    })
    @GetMapping("/country/{country}")
    public ResponseEntity<List<Exchange>> getExchangesByCountry(
            @Parameter(description = "Country to filter exchanges") @PathVariable String country) {
        List<Exchange> exchanges = exchangeService.getExchangesByCountry(country);
        return ResponseEntity.ok(exchanges);
    }

    /**
     * Adds a new stock exchange.
     *
     * @param exchange the Exchange object to be added.
     * @return ResponseEntity containing the newly created exchange.
     */
    @Operation(summary = "Add a new exchange", description = "Create a new stock exchange")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created exchange")
    })
    @PostMapping
    public ResponseEntity<Exchange> addExchange(
            @Parameter(description = "Exchange data to be added") @RequestBody Exchange exchange) {
        Exchange newExchange = exchangeService.addExchange(exchange);
        return ResponseEntity.status(HttpStatus.CREATED).body(newExchange);
    }

    /**
     * Updates an existing stock exchange by its ID.
     *
     * @param id       the ID of the exchange to update.
     * @param exchange the updated Exchange object.
     * @return ResponseEntity containing the updated exchange.
     */
    @Operation(summary = "Update an existing exchange", description = "Update the details of an existing exchange by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated exchange"),
            @ApiResponse(responseCode = "404", description = "Exchange not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Exchange> updateExchange(
            @Parameter(description = "ID of the exchange to update") @PathVariable Long id,
            @Parameter(description = "Updated exchange data") @RequestBody Exchange exchange) {
        return ResponseEntity.ok(exchangeService.updateExchange(id, exchange));
    }

    /**
     * Deletes a specific exchange by its ID.
     *
     * @param id the ID of the exchange to delete.
     * @return ResponseEntity with no content if the deletion was successful.
     */
    @Operation(summary = "Delete an exchange", description = "Delete a specific exchange by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted exchange")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExchange(
            @Parameter(description = "ID of the exchange to delete") @PathVariable Long id) {
        exchangeService.deleteExchange(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves a specific stock from the admin stock service by its ID.
     *
     * @param stockId the ID of the stock to retrieve.
     * @return ResponseEntity containing the StockDTO.
     */
    @Operation(summary = "Get stock by ID", description = "Retrieve a specific stock from the admin stock service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved stock"),
            @ApiResponse(responseCode = "404", description = "Stock not found")
    })
    @GetMapping("/stock/{stockId}")
    public ResponseEntity<StockDTO> getStockById(
            @Parameter(description = "ID of the stock to retrieve") @PathVariable Long stockId) {
        StockDTO stockDTO = exchangeService.getStockFromAdminStockService(stockId);
        return ResponseEntity.ok(stockDTO);
    }
}
