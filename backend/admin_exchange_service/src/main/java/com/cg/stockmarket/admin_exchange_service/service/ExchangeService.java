package com.cg.stockmarket.admin_exchange_service.service;

import com.cg.stockmarket.admin_exchange_service.client.AdminStockClient;
import com.cg.stockmarket.admin_exchange_service.dto.StockDTO;
import com.cg.stockmarket.admin_exchange_service.exception.NotFoundException;
import com.cg.stockmarket.admin_exchange_service.model.Exchange;
import com.cg.stockmarket.admin_exchange_service.repository.ExchangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing stock exchanges.
 */
@Service
public class ExchangeService {

    @Autowired
    private AdminStockClient adminStockClient;
    @Autowired
    private ExchangeRepository exchangeRepository;

    /**
     * Retrieves a list of all stock exchanges.
     *
     * @return List of all stock exchanges.
     */
    public List<Exchange> getAllExchanges() {
        return exchangeRepository.findAll();
    }

    /**
     * Retrieves a stock exchange by its ID.
     *
     * @param id the ID of the exchange to retrieve.
     * @return The Exchange object if found.
     * @throws NotFoundException if the exchange is not found.
     */
    public Exchange getExchangeById(Long id) {
        return exchangeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Exchange not found with id " + id));
    }

    /**
     * Adds a new stock exchange.
     *
     * @param exchange the Exchange object to be added.
     * @return The newly added Exchange object.
     */
    public Exchange addExchange(Exchange exchange) {
        return exchangeRepository.save(exchange);
    }

    /**
     * Updates an existing stock exchange.
     *
     * @param id       the ID of the exchange to update.
     * @param exchange the updated Exchange object.
     * @return The updated Exchange object.
     * @throws NotFoundException if the exchange is not found.
     */
    public Exchange updateExchange(Long id, Exchange exchange) {
        if (!exchangeRepository.existsById(id)) {
            throw new NotFoundException("Exchange not found with id " + id);
        }
        exchange.setId(id);
        return exchangeRepository.save(exchange);
    }

    /**
     * Deletes a stock exchange by its ID.
     *
     * @param id the ID of the exchange to delete.
     * @throws NotFoundException if the exchange is not found.
     */
    public void deleteExchange(Long id) {
        if (!exchangeRepository.existsById(id)) {
            throw new NotFoundException("Exchange not found with id " + id);
        }
        exchangeRepository.deleteById(id);
    }

    /**
     * Retrieves a list of exchanges by country.
     *
     * @param country the country to filter exchanges by.
     * @return List of exchanges from the specified country.
     */
    public List<Exchange> getExchangesByCountry(String country) {
        return exchangeRepository.findByCountry(country);
    }

    /**
     * Retrieves a stock by its ID from the Admin Stock Service.
     *
     * @param stockId the ID of the stock to retrieve.
     * @return The StockDTO object containing stock information.
     */
    public StockDTO getStockFromAdminStockService(Long stockId) {
        return adminStockClient.getStockById(stockId);
    }
}
