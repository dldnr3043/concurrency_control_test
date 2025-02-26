package com.example.demo.repository;

import com.example.demo.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.NoSuchElementException;

public interface StockRepository extends JpaRepository<Stock, Long> {

}
