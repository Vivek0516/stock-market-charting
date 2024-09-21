package com.cg.stock_service.service;

import com.cg.stock_service.client.AdminExchangeFeignClient;
import com.cg.stock_service.dto.Exchange;
import com.cg.stock_service.exception.InvalidExchangeIdException;
import com.cg.stock_service.exception.StockNotFoundException;
import com.cg.stock_service.exception.TemplateGenerationException;
import com.cg.stock_service.model.Stock;
import com.cg.stock_service.dto.StockPerformance;
import com.cg.stock_service.repository.StockRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing stock data.
 */
@Service
public class StockService {

    private static final Logger logger = LoggerFactory.getLogger(StockService.class);

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private AdminExchangeFeignClient adminExchangeFeignClient;

    /**
     * Retrieves all stocks from the repository.
     *
     * @return a list of all stocks
     */
    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    /**
     * Saves a list of stocks to the repository.
     *
     * @param stockList the list of stocks to save
     * @throws IllegalArgumentException if the stock list is empty or null
     */
    public void saveAll(List<Stock> stockList) {
        if (stockList != null && !stockList.isEmpty()) {
            stockRepository.saveAll(stockList);
        } else {
            throw new IllegalArgumentException("Stock list is empty or null");
        }
    }

    /**
     * Retrieves a stock by its ID.
     *
     * @param id the ID of the stock to retrieve
     * @return an Optional containing the stock if found, otherwise empty
     */
    public Optional<Stock> getStockById(Long id) {
        return stockRepository.findById(id);
    }

    /**
     * Adds a new stock to the repository.
     *
     * @param stock the stock to add
     * @return the added stock
     * @throws InvalidExchangeIdException if the exchange ID is invalid
     */
    public Stock addStock(Stock stock) {
        Exchange exchange = adminExchangeFeignClient.getExchangeById(stock.getExchange().getId());
        if (exchange != null) {
            Stock newStock = stockRepository.save(stock);
            return newStock;
        } else {
            throw new InvalidExchangeIdException("Invalid Exchange ID: " + stock.getExchange().getId());
        }
    }

    /**
     * Updates an existing stock in the repository.
     *
     * @param id    the ID of the stock to update
     * @param stock the stock data to update
     * @return the updated stock
     * @throws StockNotFoundException if the stock does not exist
     * @throws InvalidExchangeIdException if the exchange ID is invalid
     */
    public Stock updateStock(Long id, Stock stock) {
        if (stockRepository.existsById(id)) {
            Exchange exchange = adminExchangeFeignClient.getExchangeById(stock.getExchange().getId());
            if (exchange != null) {
                stock.setId(id);
                return stockRepository.save(stock);
            } else {
                throw new InvalidExchangeIdException("Invalid Exchange ID: " + stock.getExchange().getId());
            }
        } else {
            throw new StockNotFoundException("Stock not found with ID: " + id); //direct throw an messge instead of creating a sepearte custom exception class
        }
    }

    /**
     * Deletes a stock from the repository by its ID.
     *
     * @param id the ID of the stock to delete
     * @throws StockNotFoundException if the stock does not exist
     */
    public void deleteStock(Long id) {
        if (stockRepository.existsById(id)) {
            stockRepository.deleteById(id);
        } else {
            throw new StockNotFoundException("Stock not found with ID: " + id);
        }
    }

    /**
     * Retrieves stocks by their exchange ID.
     *
     * @param exchangeId the ID of the exchange to filter stocks
     * @return a list of stocks associated with the exchange ID
     */
    public List<Stock> getStocksByExchangeId(Long exchangeId) {
        return stockRepository.findByExchangeId(exchangeId);
    }

    /**
     * Saves stock data from an Excel file.
     *
     * @param file the Excel file containing stock data
     * @throws IOException if an error occurs while processing the file
     */
    public void saveExcelData(MultipartFile file) throws IOException {
        List<Stock> stockList = extractStocksFromExcel(file);
        stockRepository.saveAll(stockList);
    }

    /**
     * Validates if the uploaded file is an Excel file.
     *
     * @param file the file to validate
     * @return true if the file is a valid Excel file, false otherwise
     */
    public boolean isValidExcelFile(MultipartFile file) {
        return file.getOriginalFilename().endsWith(".xlsx");
    }

    /**
     * Generates an Excel template for stock data.
     *
     * @return a ByteArrayInputStream containing the Excel template
     * @throws TemplateGenerationException if an error occurs while creating the template
     */
    public ByteArrayInputStream getTemplateExcelFile() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Stock Template");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Stock Name");
            headerRow.createCell(1).setCellValue("Stock Price");
            headerRow.createCell(2).setCellValue("Stock Exchange ID");

            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                workbook.write(out);
                return new ByteArrayInputStream(out.toByteArray());
            }
        } catch (IOException e) {
            throw new TemplateGenerationException("Error creating Excel template");
        }
    }

    /**
     * Extracts stock data from an uploaded Excel file.
     *
     * @param file the Excel file containing stock data
     * @return a list of stocks extracted from the file
     * @throws IOException if an error occurs while reading the file
     */
    private List<Stock> extractStocksFromExcel(MultipartFile file) throws IOException {
        List<Stock> stockList = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                Stock stock = new Stock();
                stock.setName(row.getCell(0).getStringCellValue());
                stock.setPrice(row.getCell(1).getNumericCellValue());

                // Create Exchange DTO and set exchange ID from the Excel sheet
                Exchange exchangeDto = new Exchange();
                exchangeDto.setId((long) row.getCell(2).getNumericCellValue());

                // Map the Exchange DTO to the Stock entity
                stock.setExchange(exchangeDto);  // Assuming your Stock entity accepts an Exchange DTO

                stockList.add(stock);
            }
        }
        return stockList;
    }


    /**
     * Generates stock performance data for charting based on a stock's ID.
     *
     * @param stockId the ID of the stock for which to generate performance data
     * @return a StockPerformance object containing the performance data
     * @throws StockNotFoundException if the stock does not exist
     */
    public StockPerformance generateStockChart(Long stockId) {
        Stock stock = stockRepository.findById(stockId).orElseThrow(() ->
                new StockNotFoundException("Stock not found with ID: " + stockId));

        // Creating StockPerformance for chart generation based on stock details
        return new StockPerformance(stockId, stock.getName(), stock.getPrice(), stock.getPrice()); // Using the same price for both opening and closing price
    }
}
