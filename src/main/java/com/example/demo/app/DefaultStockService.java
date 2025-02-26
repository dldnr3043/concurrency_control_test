package com.example.demo.app;

import com.example.demo.domain.Stock;
import com.example.demo.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class DefaultStockService implements StockService {

    private final StockRepository stockRepository;

    @Override
    @Transactional
    public void decrease(Long id) {
        Stock stock = stockRepository.findById(id).orElseThrow(NoSuchElementException::new);
        stock.decrease();
        stockRepository.saveAndFlush(stock);
    }

    @Override
    public synchronized void decreaseSynchronized(Long id) {
        Stock stock = stockRepository.findById(id).orElseThrow(NoSuchElementException::new);
        stock.decrease();
        stockRepository.saveAndFlush(stock);
    }
}
