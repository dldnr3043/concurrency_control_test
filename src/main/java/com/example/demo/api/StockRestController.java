package com.example.demo.api;

import com.example.demo.app.StockService;
import com.example.demo.util.RedissonLockStockFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class StockRestController {
    private final StockService stockService;
    private final RedissonLockStockFacade redissonLockStockFacade;
    @GetMapping("/stocks/{id}/decrease")
    public void decreaseStock(@PathVariable("id") Long id) {
        stockService.decreaseSynchronized(id);
    }

    @GetMapping("/stocks/{id}/redis/decrease")
    public void decreaseStockRedis(@PathVariable("id") Long id) throws InterruptedException {
        redissonLockStockFacade.decrease(id);
    }
}
