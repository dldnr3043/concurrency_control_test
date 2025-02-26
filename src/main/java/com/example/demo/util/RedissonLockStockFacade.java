package com.example.demo.util;

import com.example.demo.app.StockService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
public class RedissonLockStockFacade {
    private final RedissonClient redissonClient;
    private final StockService stockService;

    public void decrease(Long id) throws InterruptedException {
        RLock lock = redissonClient.getLock(id.toString());

        try {
            boolean acquireLock = lock.tryLock(10, 1, TimeUnit.SECONDS);
            if(!acquireLock) {
                System.out.println("Fail to get Lock !!!");
                return;
            }
            stockService.decrease(id);
        }
        catch(InterruptedException e) {
            throw e;
        }
        finally {
            lock.unlock();
        }
    }
}
