package com.inomera.telco.commons.lock.reentrant;

import com.inomera.telco.commons.lock.Locked;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.util.concurrent.Uninterruptibles.awaitUninterruptibly;
import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Serdar Kuzucu
 */
class LocalReentrantLockProviderTest {
    private static final String KEY1 = "key1";
    private static final String KEY2 = "key2";
    private static final String LOCK_MAP = "lockMap";

    @Test
    @DisplayName("Test lock(lockName)")
    void lockWithLockName() {
        final LocalReentrantLockProvider lockProvider = new LocalReentrantLockProvider();

        assertTimeoutPreemptively(Duration.ofSeconds(20), () -> {
            final Locked lockedKey1 = lockProvider.lock(KEY1);
            assertEquals(1, lockProvider.size());

            final Locked lockedKey2 = lockProvider.lock(KEY2);
            assertEquals(2, lockProvider.size());

            lockedKey1.unlock();
            lockedKey2.unlock();
            assertEquals(0, lockProvider.size());

            final AtomicReference<Throwable> uncaughtException = new AtomicReference<>();
            final AtomicInteger counter = new AtomicInteger(0);
            final CountDownLatch thread1Locked = new CountDownLatch(1);
            final CountDownLatch thread1Continue = new CountDownLatch(1);

            final Thread thread1 = new Thread(() -> {
                final Locked lockedKey1Again = lockProvider.lock(KEY1);
                thread1Locked.countDown();

                counter.incrementAndGet();
                awaitUninterruptibly(thread1Continue);

                sleepUninterruptibly(500, TimeUnit.MILLISECONDS);

                lockedKey1Again.unlock();
            });

            thread1.setUncaughtExceptionHandler((th, ex) -> uncaughtException.set(ex));
            thread1.start();

            assertTrue(thread1Locked.await(3, TimeUnit.SECONDS));

            final CountDownLatch thread2AtLock = new CountDownLatch(1);
            final Thread thread2 = new Thread(() -> {
                thread2AtLock.countDown();
                final Locked lockedKey1SecondTime = lockProvider.lock(KEY1);

                counter.incrementAndGet();

                lockedKey1SecondTime.unlock();
            });

            thread2.setUncaughtExceptionHandler((th, ex) -> uncaughtException.set(ex));
            thread2.start();

            assertTrue(thread1Locked.await(3, TimeUnit.SECONDS));
            thread1Continue.countDown();

            thread1.join(3000);
            assertFalse(thread1.isAlive());

            thread2.join(3000);
            assertFalse(thread2.isAlive());

            if (uncaughtException.get() != null) {
                throw new AssertionError("Thread threw exception", uncaughtException.get());
            }

            assertEquals(2, counter.get());
            assertEquals(0, lockProvider.size());
        });
    }

    @Test
    @DisplayName("Test tryLock(lockName)")
    void tryLockWithLockName() {
        final LocalReentrantLockProvider lockProvider = new LocalReentrantLockProvider();

        assertTimeoutPreemptively(Duration.ofSeconds(20), () -> {
            final Optional<Locked> lockedOptional = lockProvider.tryLock(KEY1);
            assertTrue(lockedOptional.isPresent());
            assertEquals(1, lockProvider.size());

            lockedOptional.ifPresent(Locked::unlock);
            assertEquals(0, lockProvider.size());

            final Optional<Locked> lockedOptionalSecondTime = lockProvider.tryLock(KEY1);
            assertTrue(lockedOptionalSecondTime.isPresent());
            assertEquals(1, lockProvider.size());

            final AtomicReference<Throwable> uncaughtException = new AtomicReference<>();
            final CountDownLatch thread1AtTryLock = new CountDownLatch(1);
            final Thread thread1 = new Thread(() -> {
                thread1AtTryLock.countDown();

                final Optional<Locked> lockedOptionalThird = lockProvider.tryLock(KEY1);
                assertFalse(lockedOptionalThird.isPresent());
                assertEquals(1, lockProvider.size());

                final Optional<Locked> lockedOptionalFourth = lockProvider.tryLock(KEY1);
                assertFalse(lockedOptionalFourth.isPresent());
                assertEquals(1, lockProvider.size());
            });

            thread1.setUncaughtExceptionHandler((th, ex) -> uncaughtException.set(ex));
            thread1.start();

            assertTrue(thread1AtTryLock.await(3, TimeUnit.SECONDS));

            thread1.join(3000);
            assertFalse(thread1.isAlive());

            if (uncaughtException.get() != null) {
                throw new AssertionError("Thread threw exception", uncaughtException.get());
            }

            lockedOptionalSecondTime.ifPresent(Locked::unlock);

            assertEquals(0, lockProvider.size());
        });
    }

    @Test
    @DisplayName("Test lock(lockMap, lockName)")
    void lockWithLockMapAndLockName() {
        final LocalReentrantLockProvider lockProvider = new LocalReentrantLockProvider();

        assertTimeoutPreemptively(Duration.ofSeconds(20), () -> {
            final Locked lockedKey1 = lockProvider.lock(LOCK_MAP, KEY1);
            assertEquals(1, lockProvider.size());

            final Locked lockedKey2 = lockProvider.lock(LOCK_MAP, KEY2);
            assertEquals(2, lockProvider.size());

            lockedKey1.unlock();
            lockedKey2.unlock();
            assertEquals(0, lockProvider.size());

            final AtomicReference<Throwable> uncaughtException = new AtomicReference<>();
            final AtomicInteger counter = new AtomicInteger(0);
            final CountDownLatch thread1Locked = new CountDownLatch(1);
            final CountDownLatch thread1Continue = new CountDownLatch(1);

            final Thread thread1 = new Thread(() -> {
                final Locked lockedKey1Again = lockProvider.lock(LOCK_MAP, KEY1);
                thread1Locked.countDown();

                counter.incrementAndGet();
                awaitUninterruptibly(thread1Continue);

                sleepUninterruptibly(500, TimeUnit.MILLISECONDS);

                lockedKey1Again.unlock();
            });

            thread1.setUncaughtExceptionHandler((th, ex) -> uncaughtException.set(ex));
            thread1.start();

            assertTrue(thread1Locked.await(3, TimeUnit.SECONDS));

            final CountDownLatch thread2AtLock = new CountDownLatch(1);
            final Thread thread2 = new Thread(() -> {
                thread2AtLock.countDown();
                final Locked lockedKey1SecondTime = lockProvider.lock(LOCK_MAP, KEY1);

                counter.incrementAndGet();

                lockedKey1SecondTime.unlock();
            });

            thread2.setUncaughtExceptionHandler((th, ex) -> uncaughtException.set(ex));
            thread2.start();

            assertTrue(thread1Locked.await(3, TimeUnit.SECONDS));
            thread1Continue.countDown();

            thread1.join(3000);
            assertFalse(thread1.isAlive());

            thread2.join(3000);
            assertFalse(thread2.isAlive());

            if (uncaughtException.get() != null) {
                throw new AssertionError("Thread threw exception", uncaughtException.get());
            }

            assertEquals(2, counter.get());
            assertEquals(0, lockProvider.size());
        });
    }

    @Test
    @DisplayName("Test tryLock(lockMap, lockName)")
    void tryLockWithLockMapAndLockName() {
        final LocalReentrantLockProvider lockProvider = new LocalReentrantLockProvider();

        assertTimeoutPreemptively(Duration.ofSeconds(20), () -> {
            final Optional<Locked> lockedOptional = lockProvider.tryLock(LOCK_MAP, KEY1);
            assertTrue(lockedOptional.isPresent());
            assertEquals(1, lockProvider.size());

            lockedOptional.ifPresent(Locked::unlock);
            assertEquals(0, lockProvider.size());

            final Optional<Locked> lockedOptionalSecondTime = lockProvider.tryLock(LOCK_MAP, KEY1);
            assertTrue(lockedOptionalSecondTime.isPresent());
            assertEquals(1, lockProvider.size());

            final AtomicReference<Throwable> uncaughtException = new AtomicReference<>();
            final CountDownLatch thread1AtTryLock = new CountDownLatch(1);
            final Thread thread1 = new Thread(() -> {
                thread1AtTryLock.countDown();

                final Optional<Locked> lockedOptionalThird = lockProvider.tryLock(LOCK_MAP, KEY1);
                assertFalse(lockedOptionalThird.isPresent());
                assertEquals(1, lockProvider.size());

                final Optional<Locked> lockedOptionalFourth = lockProvider.tryLock(LOCK_MAP, KEY1);
                assertFalse(lockedOptionalFourth.isPresent());
                assertEquals(1, lockProvider.size());
            });

            thread1.setUncaughtExceptionHandler((th, ex) -> uncaughtException.set(ex));
            thread1.start();

            assertTrue(thread1AtTryLock.await(3, TimeUnit.SECONDS));

            thread1.join(3000);
            assertFalse(thread1.isAlive());

            if (uncaughtException.get() != null) {
                throw new AssertionError("Thread threw exception", uncaughtException.get());
            }

            lockedOptionalSecondTime.ifPresent(Locked::unlock);

            assertEquals(0, lockProvider.size());
        });
    }

    @Test
    @DisplayName("Unlock should throw exception on second call")
    void unlock_shouldThrowExceptionOnSecondCall() {
        final LocalReentrantLockProvider lockProvider = new LocalReentrantLockProvider();

        final Locked lock = lockProvider.lock(KEY1);
        lock.unlock();

        assertThrows(IllegalMonitorStateException.class, lock::unlock);
    }
}
