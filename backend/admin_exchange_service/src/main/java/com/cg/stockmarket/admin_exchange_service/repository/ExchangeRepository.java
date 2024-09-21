package com.cg.stockmarket.admin_exchange_service.repository;

import com.cg.stockmarket.admin_exchange_service.model.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, Long> {
    List<Exchange> findByCountry(String country);
}
