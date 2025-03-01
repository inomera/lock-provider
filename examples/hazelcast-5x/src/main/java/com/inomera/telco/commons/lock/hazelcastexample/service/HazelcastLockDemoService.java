package com.inomera.telco.commons.lock.hazelcastexample.service;

import com.inomera.telco.commons.lock.LockProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ebru Zorlu
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HazelcastLockDemoService {
    private static final String LOCK_MAP_NAME = "lock-map-1";
    private static final String PESSIMISTIC_LOCK_KEY = "lock-key-1";
    private static final String OPTIMISTIC_LOCK_KEY = "lock-key-2";

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final LockProvider lockProvider;

    @PostConstruct
    public void startDemo() {
        for (int i = 0; i < 5; i++) {
            executorService.submit(this::sleepInPessimisticLock);
            executorService.submit(this::sleepInOptimisticLock);
        }
    }

    private void sleepInPessimisticLock() {
        while (running.get()) {
            lockProvider.executeInLock(LOCK_MAP_NAME, PESSIMISTIC_LOCK_KEY, () -> {
                try {
                    LOG.info("{} in pessimistic lock", Thread.currentThread().getName());
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    LOG.debug("Thread is interrupted. Exiting.");
                    Thread.currentThread().interrupt();
                }
            });
        }
    }

    private void sleepInOptimisticLock() {
        while (running.get()) {
            lockProvider.executeInTryLock(LOCK_MAP_NAME, OPTIMISTIC_LOCK_KEY, () -> {
                try {
                    LOG.info("{} in optimistic lock", Thread.currentThread().getName());
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    LOG.debug("Thread is interrupted. Exiting.");
                    Thread.currentThread().interrupt();
                }
            });
        }
    }

    @PreDestroy
    public void stop() throws InterruptedException {
        running.set(false);
        this.executorService.shutdownNow();
        this.executorService.awaitTermination(1, TimeUnit.MINUTES);
    }
}
