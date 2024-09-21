package com.cg.stockmarket.admin_exchange_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockDTO {
    private Long id;
    private String name;
    private double price;
    private Long exchangeId;
}
