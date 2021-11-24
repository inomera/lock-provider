package com.inomera.telco.commons.lock.reentrant;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Serdar Kuzucu
 */
class CountingReentrantLock extends ReentrantLock {
    private static final long serialVersionUID = 1L;
    private final AtomicInteger counter = new AtomicInteger(1);

    CountingReentrantLock() {
        super(true);
    }

    int incrementCounterAndGetValue() {
        return counter.incrementAndGet();
    }

    int decrementCounterAndGetValue() {
        return counter.decrementAndGet();
    }
}
