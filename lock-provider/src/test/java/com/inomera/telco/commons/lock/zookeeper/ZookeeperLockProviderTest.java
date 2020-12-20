package com.inomera.telco.commons.lock.zookeeper;

import com.inomera.telco.commons.lock.Locked;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class ZookeeperLockProviderTest {

    private static final String DEFAULT_PATH = "/tmp/locks";

    private ZookeeperLockProvider lockProvider;

    TestingServer zkServer;
    CuratorFramework curatorClient;

    @BeforeEach
    void init() throws Exception {
        zkServer = new TestingServer(2181, new File(DEFAULT_PATH));
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        curatorClient = CuratorFrameworkFactory.newClient(zkServer.getConnectString(), retryPolicy);
        curatorClient.start();
        lockProvider = new ZookeeperLockProvider(curatorClient, DEFAULT_PATH);
    }

    @AfterEach
    void end() throws IOException {
        curatorClient.close();
        zkServer.stop();
    }

    @Test
    @DisplayName("Constructor without default path name argument should initialize a default path name")
    void constructorWithoutDefaultMapName_shouldInitializeADefaultMapName() {
        final Locked locked = lockProvider.lock("aLock");
        assertNotNull(locked);
        locked.unlock();
    }

    @Test
    @DisplayName("lock with custom path and lockKey should unlock on a path")
    void lock_shouldUnlockOnMap() {
        final Locked locked = lockProvider.lock("/tmp/locks2", "aLock");

        assertNotNull(locked);

        locked.unlock();
    }

    @Test
    @DisplayName("tryLock with default path and lockKey should lock on a path")
    void tryLock_shouldTryLockOnMap_returnFullOptional() {
        final Optional<Locked> locked = lockProvider.tryLock("aLock");

        assertNotNull(locked);
        assertTrue(locked.isPresent());
        locked.get().unlock();
    }

    @Test
    @DisplayName("tryLock with custom path and lockKey should unlock on a path")
    void tryLock_shouldUnlockOnMap() {

        final Optional<Locked> locked = lockProvider.tryLock("/tmp/locks2", "aLock");

        assertNotNull(locked);
        assertTrue(locked.isPresent());
        locked.get().unlock();
    }

    @Test
    @DisplayName("tryLock with custom path and lockKey should return empty Optional when lock is owned")
    void tryLock_shouldTryLockOnMap_returnEmptyOptional() {
        final Locked aLock = lockProvider.lock("/tmp/locks2", "aLock");
        final Optional<Locked> locked = lockProvider.tryLock("/tmp/locks2", "aLock");
        assertNotNull(locked);
        assertFalse(locked.isPresent());
        aLock.unlock();
    }


    @Test
    @DisplayName("executeInLock should lock in Zookeeper path and unlock after task finishes")
    void executeInLock_shouldLockInMapAndUnlockAfterRunnableFinishes() {
        final AtomicInteger counter = new AtomicInteger(0);
        lockProvider.executeInLock("/tmp/locks2", "aLock", () -> {
            counter.incrementAndGet();
            final Optional<Locked> lockedOptional = lockProvider.tryLock("/tmp/locks2", "aLock");
            assertNotNull(lockedOptional);
            assertFalse(lockedOptional.isPresent());
        });
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInLock should lock in Zookeeper path and unlock after task throws exception")
    void executeInLock_shouldLockInMapAndUnlockAfterRunnableThrowsException() {
        final AtomicInteger counter = new AtomicInteger(0);
        assertThrows(RuntimeException.class, () -> lockProvider.executeInLock("/tmp/locks2", "aLock", (Runnable) () -> {
            counter.incrementAndGet();
            throw new RuntimeException("test");
        }));

        assertEquals(1, counter.get());
        final Optional<Locked> lockedOptional = lockProvider.tryLock("/tmp/locks2", "aLock");
        assertNotNull(lockedOptional);
        assertTrue(lockedOptional.isPresent());
    }

    @Test
    @DisplayName("executeInLock should lock in Zookeeper path and unlock after task finishes and return the value")
    void executeInLock_shouldLockInMapAndUnlockAfterSupplierFinishes() {
        final AtomicInteger counter = new AtomicInteger(0);
        int returnValue = lockProvider.executeInLock("/tmp/locks2", "aLock", () -> {
            counter.incrementAndGet();
            return 3;
        });

        assertEquals(1, counter.get());
        assertEquals(3, returnValue);
    }

    @Test
    @DisplayName("executeInLock should lock in Zookeeper path and unlock after supplier throws exception")
    void executeInLock_shouldLockInMapAndUnlockAfterSupplierThrowsException() {
        final AtomicInteger counter = new AtomicInteger(0);
        assertThrows(RuntimeException.class, () -> lockProvider.executeInLock("/tmp/locks2", "aLock", (Supplier<String>) () -> {
            counter.incrementAndGet();
            throw new RuntimeException("test");
        }));

        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInLock should lock in Zookeeper default path and unlock after task finishes")
    void executeInLock_shouldLockAndUnlockAfterRunnableFinishes() {
        final AtomicInteger counter = new AtomicInteger(0);
        lockProvider.executeInLock("aLock", () -> {
            counter.incrementAndGet();
        });

        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInLock should lock in Zookeeper default path and unlock after task throws exception")
    void executeInLock_shouldLockAndUnlockAfterRunnableThrowsException() {
        final AtomicInteger counter = new AtomicInteger(0);
        assertThrows(RuntimeException.class, () -> lockProvider.executeInLock("aLock", (Runnable) () -> {
            counter.incrementAndGet();
            throw new RuntimeException("test");
        }));

        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInLock should lock in Zookeeper default path and unlock after task finishes and return the value")
    void executeInLock_shouldLockAndUnlockAfterSupplierFinishes() {
        final AtomicInteger counter = new AtomicInteger(0);
        int returnValue = lockProvider.executeInLock("aLock", () -> {
            counter.incrementAndGet();
            return 3;
        });

        assertEquals(1, counter.get());
        assertEquals(3, returnValue);
    }

    @Test
    @DisplayName("executeInLock should lock in Zookeeper default path and unlock after supplier throws exception")
    void executeInLock_shouldLockAndUnlockAfterSupplierThrowsException() {
        final AtomicInteger counter = new AtomicInteger(0);
        assertThrows(RuntimeException.class, () -> lockProvider.executeInLock("aLock", (Supplier<String>) () -> {
            counter.incrementAndGet();
            throw new RuntimeException("test");
        }));
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInTryLock should lock in Zookeeper path and unlock after task finishes")
    void executeInTryLock_shouldLockInMapAndUnlockAfterRunnableFinishes() {
        final AtomicInteger counter = new AtomicInteger(0);
        lockProvider.executeInTryLock("/tmp/locks2", "aLock", () -> {
            counter.incrementAndGet();
        });
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInTryLock should lock in Zookeeper path and unlock after task throws exception")
    void executeInTryLock_shouldLockInMapAndUnlockAfterRunnableThrowsException() {
        final AtomicInteger counter = new AtomicInteger(0);

        assertThrows(RuntimeException.class, () -> lockProvider.executeInTryLock("/tmp/locks2", "aLock", (Runnable) () -> {
            counter.incrementAndGet();
            throw new RuntimeException("test");
        }));

        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInTryLock should not lock in Zookeeper path and unlock when tryLock returns false")
    void executeInTryLock_shouldNotLockInMapAndUnlockWhenTryLockReturnsFalse() {
        final Locked lock = lockProvider.lock("/tmp/locks2", "aLock");
        final AtomicInteger counter = new AtomicInteger(0);

        lockProvider.executeInTryLock("/tmp/locks2", "aLock", (Runnable) counter::incrementAndGet);
        lock.unlock();

        assertEquals(0, counter.get());
    }

    @Test
    @DisplayName("executeInTryLock should lock in Zookeeper path and unlock after task finishes and return the value")
    void executeTryInLock_shouldLockInMapAndUnlockAfterSupplierFinishes() {
        final AtomicInteger counter = new AtomicInteger(0);
        int returnValue = lockProvider.executeInTryLock("/tmp/locks2", "aLock", () -> {
            counter.incrementAndGet();
            return 3;
        });

        assertEquals(1, counter.get());
        assertEquals(3, returnValue);
    }

    @Test
    @DisplayName("executeInTryLock should lock in Zookeeper path and unlock after supplier throws exception")
    void executeInTryLock_shouldLockInMapAndUnlockAfterSupplierThrowsException() {
        final AtomicInteger counter = new AtomicInteger(0);

        assertThrows(RuntimeException.class, () -> lockProvider.executeInTryLock("/tmp/locks2", "aLock", (Supplier<String>) () -> {
            counter.incrementAndGet();
            throw new RuntimeException("test");
        }));

        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInTryLock should not lock in Zookeeper path and unlock when lock is not available")
    void executeTryInLock_shouldLockInMapAndUnlockWhenLockIsNotAvailable() {
        final Locked lock = lockProvider.lock("/tmp/locks2", "aLock");
        final AtomicInteger counter = new AtomicInteger(0);
        Integer returnValue = lockProvider.executeInTryLock("/tmp/locks2", "aLock", () -> {
            counter.incrementAndGet();
            return 3;
        });
        lock.unlock();

        assertEquals(0, counter.get());
        assertNull(returnValue);
    }

    @Test
    @DisplayName("executeInTryLock should lock in Zookeeper and unlock after task finishes")
    void executeInTryLock_shouldLockAndUnlockAfterRunnableFinishes() {
        final AtomicInteger counter = new AtomicInteger(0);
        lockProvider.executeInTryLock("aLock", () -> {
            counter.incrementAndGet();
        });
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInTryLock should lock in Zookeeper and unlock after task throws exception")
    void executeInTryLock_shouldLockAndUnlockAfterRunnableThrowsException() {
        final AtomicInteger counter = new AtomicInteger(0);

        assertThrows(RuntimeException.class, () -> lockProvider.executeInTryLock("aLock", (Runnable) () -> {
            counter.incrementAndGet();
            throw new RuntimeException("test");
        }));

        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInTryLock should not lock in Zookeeper and unlock when the lock is not available")
    void executeInTryLock_shouldNotLockAndUnlockWhenLockIsNotAvailable() {
        final Locked lock = lockProvider.lock("aLock");
        final AtomicInteger counter = new AtomicInteger(0);
        lockProvider.executeInTryLock("aLock", (Runnable) counter::incrementAndGet);
        lock.unlock();
        assertEquals(0, counter.get());
    }

    @Test
    @DisplayName("executeInTryLock should lock in Zookeeper and unlock after task finishes and return the value")
    void executeInTryLock_shouldLockAndUnlockAfterSupplierFinishes() {
        final AtomicInteger counter = new AtomicInteger(0);
        int returnValue = lockProvider.executeInTryLock("aLock", () -> {
            counter.incrementAndGet();
            return 3;
        });

        assertEquals(1, counter.get());
        assertEquals(3, returnValue);
    }

    @Test
    @DisplayName("executeInTryLock should lock in Zookeeper and unlock after supplier throws exception")
    void executeInTryLock_shouldLockAndUnlockAfterSupplierThrowsException() {
        final AtomicInteger counter = new AtomicInteger(0);
        assertThrows(RuntimeException.class, () -> lockProvider.executeInTryLock("aLock", (Supplier<String>) () -> {
            counter.incrementAndGet();
            throw new RuntimeException("test");
        }));
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInTryLock should not lock in Zookeeper and unlock when lock is not available and return null")
    void executeInTryLock_shouldNotLockAndUnlockWhenLockIsNotAvailableAndReturnNull() {
        final Locked lock = lockProvider.lock("aLock");

        final AtomicInteger counter = new AtomicInteger(0);
        final Integer returnValue = lockProvider.executeInTryLock("aLock", () -> {
            counter.incrementAndGet();
            return 3;
        });

        lock.unlock();
        assertEquals(0, counter.get());
        assertNull(returnValue);
    }

}
