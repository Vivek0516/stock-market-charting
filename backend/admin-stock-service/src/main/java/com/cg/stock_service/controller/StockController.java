package com.cg.stock_service.controller;

import com.cg.stock_service.dto.FileUploadResponse;
import com.cg.stock_service.exception.InvalidExcelFileException;
import com.cg.stock_service.exception.TemplateGenerationException;
import com.cg.stock_service.model.Stock;
import com.cg.stock_service.dto.StockPerformance;
import com.cg.stock_service.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controller for managing stock-related operations.
 */
@RestController
@RequestMapping("/stocks")
@Tag(name = "Admin Stock Service", description = "Endpoints for managing stocks")
public class StockController {

    @Autowired
    private StockService stockService;

    /**
     * Retrieves all stocks.
     *
     * @return A list of all stocks.
     */
    @Operation(summary = "Get all stocks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved stocks")
    })
    @GetMapping
    public ResponseEntity<List<Stock>> getAllStocks() {
        List<Stock> stocks = stockService.getAllStocks();
        return ResponseEntity.ok(stocks);
    }

    /**
     * Retrieves a stock by its ID.
     *
     * @param id The ID of the stock to retrieve.
     * @return The stock with the given ID, or a 404 status if not found.
     */
    @Operation(summary = "Get stock by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved stock"),
            @ApiResponse(responseCode = "404", description = "Stock not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Stock> getStockById(@PathVariable Long id) {
        Optional<Stock> stock = stockService.getStockById(id);
        return stock.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Adds a new stock.
     *
     * @param stock The stock to add.
     * @return The newly created stock.
     */
    @Operation(summary = "Add a new stock", description = "Create a new stock entry")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created stock"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<Stock> addStock(@RequestBody Stock stock) {
        try {
            Stock newStock = stockService.addStock(stock);
            return ResponseEntity.status(HttpStatus.CREATED).body(newStock);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Updates an existing stock.
     *
     * @param id    The ID of the stock to update.
     * @param stock The stock data to update.
     * @return The updated stock.
     */
    @Operation(summary = "Update an existing stock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated stock"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Stock> updateStock(@PathVariable Long id, @RequestBody Stock stock) {
        try {
            Stock updatedStock = stockService.updateStock(id, stock);
            return ResponseEntity.ok(updatedStock);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Deletes a stock by its ID.
     *
     * @param id The ID of the stock to delete.
     * @return A 204 status if the deletion was successful, or a 404 status if the stock was not found.
     */
    @Operation(summary = "Delete a stock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted stock"),
            @ApiResponse(responseCode = "404", description = "Stock not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStock(@PathVariable Long id) {
        if (stockService.getStockById(id).isPresent()) {
            stockService.deleteStock(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Retrieves stocks by exchange ID.
     *
     * @param exchangeId The ID of the exchange.
     * @return A list of stocks associated with the given exchange.
     */
    @Operation(summary = "Get stocks by exchange ID", description = "Retrieve all stocks for a specific exchange")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved stocks for the exchange")
    })
    @GetMapping("/exchange/{exchangeId}")
    public ResponseEntity<List<Stock>> getStocksByExchangeId(@PathVariable Long exchangeId) {
        List<Stock> stocks = stockService.getStocksByExchangeId(exchangeId);
        return ResponseEntity.ok(stocks);
    }

    /**
     * Saves a batch of stock data.
     *
     * @param stockList A list of stocks to save.
     * @return A message indicating the result of the operation.
     */
    @Operation(summary = "Save a batch of stock data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stocks saved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/bulk")
    public ResponseEntity<String> saveStockData(@RequestBody List<Stock> stockList) {
        try {
            stockService.saveAll(stockList);
            return ResponseEntity.ok("Stocks saved successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to save stocks");
        }
    }

    /**
     * Uploads an Excel file containing stock data.
     *
     * @param file The Excel file to upload.
     * @return A response indicating the result of the file upload.
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestPart("file") MultipartFile file) {
        try {
            if (stockService.isValidExcelFile(file)) {
                stockService.saveExcelData(file);
                FileUploadResponse response = new FileUploadResponse("File uploaded successfully", file.getSize());
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                throw new InvalidExcelFileException("Invalid Excel file");
            }
        } catch (InvalidExcelFileException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new FileUploadResponse(e.getMessage(), file.getSize()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new FileUploadResponse("Error processing file", file.getSize()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new FileUploadResponse("An unexpected error occurred", file.getSize()));
        }
    }

    /**
     * Generates a stock performance chart for a specific stock ID.
     *
     * @param id The ID of the stock for which to generate the chart.
     * @return The stock performance data for the specified stock.
     */
    @Operation(summary = "Generate Stock Performance Chart", description = "Generates a stock performance chart for a specific stock ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully generated stock performance chart"),
            @ApiResponse(responseCode = "404", description = "Stock not found")
    })
    @GetMapping("/{id}/chart")
    public ResponseEntity<StockPerformance> generateStockChart(@PathVariable Long id) {
        StockPerformance stockPerformance = stockService.generateStockChart(id);
        return ResponseEntity.ok(stockPerformance);
    }

    /**
     * Downloads the stock template Excel file.
     *
     * @return A response containing the Excel file as a byte array.
     */
    @GetMapping("/download-template")
    public ResponseEntity<byte[]> downloadTemplate() {
        ByteArrayInputStream stream = null;
        try {
            stream = stockService.getTemplateExcelFile();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", "template.xlsx");
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return ResponseEntity.ok().headers(headers).body(stream.readAllBytes());
        } catch (Exception e) {
            throw new TemplateGenerationException("Error generating Excel template");
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
