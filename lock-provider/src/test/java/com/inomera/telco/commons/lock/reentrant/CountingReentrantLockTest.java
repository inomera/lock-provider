package com.inomera.telco.commons.lock.reentrant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Serdar Kuzucu
 */
class CountingReentrantLockTest {
    @Test
    @DisplayName("Should start counting at 1 and return 2 on first increment")
    void shouldReturn2OnFirstIncrement() {
        final CountingReentrantLock countingReentrantLock = new CountingReentrantLock();
        assertEquals(2, countingReentrantLock.incrementCounterAndGetValue());
    }

    @Test
    @DisplayName("Should increment to 4 and reduce to 3 after 3 increment and 1 decrement")
    void shouldReturn3After3IncrementAndOneDecrement() {
        final CountingReentrantLock countingReentrantLock = new CountingReentrantLock();
        assertEquals(2, countingReentrantLock.incrementCounterAndGetValue());
        assertEquals(3, countingReentrantLock.incrementCounterAndGetValue());
        assertEquals(4, countingReentrantLock.incrementCounterAndGetValue());

        assertEquals(3, countingReentrantLock.decrementCounterAndGetValue());
    }
}
