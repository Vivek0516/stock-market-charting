package com.cg.stock_service.repository;

import com.cg.stock_service.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    @Query("SELECT s FROM Stock s WHERE s.exchangeId = :exchangeId")
    List<Stock> findByExchangeId(Long exchangeId);
}
