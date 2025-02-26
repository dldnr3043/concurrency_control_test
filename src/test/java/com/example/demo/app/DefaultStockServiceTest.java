package com.example.demo.app;

import com.example.demo.domain.Stock;
import com.example.demo.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DefaultStockServiceTest {

    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private StockService stockService;
    private Long stockId;

    @BeforeEach
    void setUp() {
        stockId = stockRepository.save(new Stock(1L, 100)).getId();
    }

    @AfterEach
    void testDown() {
        stockRepository.deleteAll();
    }

    @Test
    void decrease() {
        // given
        stockService.decrease(stockId);

        // when
        Stock stock = stockRepository.findById(stockId).orElseThrow(NoSuchElementException::new);

        // then
        assertEquals(stock.getQuantity(), 99);
    }

    @Test
    void decrease_100() throws InterruptedException {
        // given
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for(int i=0;i<threadCount;i++) {
            executorService.submit(() -> {
                stockService.decrease(stockId);
                latch.countDown();
            });
        }
        latch.await();

        // then
        Stock stock = stockRepository.findById(stockId).orElseThrow(NoSuchElementException::new);
        assertEquals(0, stock.getQuantity());
    }

    @Test
    void decrease_100_synchronized() throws InterruptedException {
        // given
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for(int i=0;i<threadCount;i++) {
            executorService.submit(() -> {
                stockService.decreaseSynchronized(stockId);
                latch.countDown();
            });
        }
        latch.await();

        // then
        Stock stock = stockRepository.findById(stockId).orElseThrow(NoSuchElementException::new);
        assertEquals(0, stock.getQuantity());
    }

    @Test
    void decrease_100_syncronized_api() throws InterruptedException {
        // given
        int threadCount = 100;
        RestTemplate restTemplate = new RestTemplate();
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                int port = 8080;
                ResponseEntity<Void> forEntity = restTemplate.getForEntity("http://localhost:" + port + "/stocks/1/decrease", Void.class);
                latch.countDown();
            });
        }
        latch.await();

        // then
        Stock stock = stockRepository.findById(stockId).orElseThrow(NoSuchElementException::new);
        assertEquals(0, stock.getQuantity());
    }

    @Test
    void decrease_100_syncronized_api_multi() throws InterruptedException {
        // given
        int threadCount = 100;
        RestTemplate restTemplate = new RestTemplate();
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            final int ii = i;

            executorService.submit(() -> {
                int port = (ii % 2 == 0) ? 8080 : 8081;
                ResponseEntity<Void> forEntity = restTemplate.getForEntity("http://localhost:" + port + "/stocks/1/decrease", Void.class);
                latch.countDown();
            });
        }
        latch.await();

        // then
        Stock stock = stockRepository.findById(stockId).orElseThrow(NoSuchElementException::new);
        assertEquals(0, stock.getQuantity());
    }

    @Test
    void decrease_100_syncronized_api_multi_redis() throws InterruptedException {
        // given
        int threadCount = 100;
        RestTemplate restTemplate = new RestTemplate();
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            final int ii = i;

            executorService.submit(() -> {
                int port = (ii % 2 == 0) ? 8080 : 8081;
                ResponseEntity<Void> forEntity = restTemplate.getForEntity("http://localhost:" + port + "/stocks/1/redis/decrease", Void.class);
                latch.countDown();
            });
        }
        latch.await();

        // then
        Stock stock = stockRepository.findById(stockId).orElseThrow(NoSuchElementException::new);
        assertEquals(0, stock.getQuantity());
    }
}