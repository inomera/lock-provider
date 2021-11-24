package com.inomera.telco.commons.lock.redis;

import com.inomera.telco.commons.lock.Locked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
class RedisLockProviderTest {
    private static final String DEFAULT_MAP_NAME = "defMapName";

    private RedisLockProvider lockProvider;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RMap<String, Object> mockMap;

    @Mock
    private RLock pessimisticLock;

    @Mock
    private RLock optimisticLock;

    @BeforeEach
    void init() {
        lockProvider = new RedisLockProvider(redissonClient, DEFAULT_MAP_NAME);
        when(mockMap.getFairLock(anyString())).thenReturn(pessimisticLock);
        when(mockMap.getLock(anyString())).thenReturn(optimisticLock);
    }

    @Test
    @DisplayName("Constructor without default map name argument should initialize a default map name")
    void constructorWithoutDefaultMapName_shouldInitializeADefaultMapName() {
        when(redissonClient.<String, Object>getMap(any())).thenReturn(mockMap);

        final RedisLockProvider redisLockProvider = new RedisLockProvider(redissonClient);
        final Locked locked = redisLockProvider.lock("aLock");
        assertNotNull(locked);

        final ArgumentCaptor<String> mapNameCaptor = ArgumentCaptor.forClass(String.class);

        verify(redissonClient, times(1)).<String, Object>getMap(mapNameCaptor.capture());
        final String mapName = mapNameCaptor.getValue();
        assertNotNull(mapName);
        assertFalse(mapName.isEmpty());
    }

    @Test
    @DisplayName("lock with mapName and lockKey should lock on a map")
    void lock_shouldLockOnMap() {
        when(redissonClient.<String, Object>getMap("map1")).thenReturn(mockMap);

        final Locked locked = lockProvider.lock("map1", "lock1");
        assertNotNull(locked);

        verify(mockMap, times(1)).getFairLock("lock1");
    }

    @Test
    @DisplayName("lock with mapName and lockKey should unlock on a map")
    void lock_shouldUnlockOnMap() {
        when(redissonClient.<String, Object>getMap("map1")).thenReturn(mockMap);

        lockProvider.lock("map1", "lock1").unlock();

        verify(mockMap, times(1)).getFairLock("lock1");
    }

    @Test
    @DisplayName("lock without mapName and with lockKey should lock on default map")
    void lock_shouldLockWithDefaultMap() {
        when(redissonClient.<String, Object>getMap(DEFAULT_MAP_NAME)).thenReturn(mockMap);

        final Locked locked = lockProvider.lock("lock1");
        assertNotNull(locked);

        verify(mockMap, times(1)).getFairLock("lock1");
    }

    @Test
    @DisplayName("lock without mapName and with lockKey should unlock on default map")
    void lock_shouldUnlockOnILock() {
        when(redissonClient.<String, Object>getMap(DEFAULT_MAP_NAME)).thenReturn(mockMap);

        lockProvider.lock("lock1").unlock();

        verify(mockMap, times(1)).getFairLock("lock1");
    }

    @Test
    @DisplayName("tryLock with mapName and lockKey should lock on a map")
    void tryLock_shouldTryLockOnMap_returnFullOptional() {
        when(redissonClient.<String, Object>getMap("map1")).thenReturn(mockMap);
        when(mockMap.getLock("lock1")).thenReturn(optimisticLock);
        when(optimisticLock.tryLock()).thenReturn(true);

        final Optional<Locked> locked = lockProvider.tryLock("map1", "lock1");
        assertNotNull(locked);
        assertTrue(locked.isPresent());

        verify(mockMap, times(1)).getLock("lock1");
        verify(optimisticLock, times(1)).tryLock();
    }

    @Test
    @DisplayName("tryLock with mapName and lockKey should unlock on a map")
    void tryLock_shouldUnlockOnMap() {
        when(redissonClient.<String, Object>getMap("map1")).thenReturn(mockMap);
        when(mockMap.getLock("lock1")).thenReturn(optimisticLock);
        when(optimisticLock.tryLock()).thenReturn(true);

        lockProvider.tryLock("map1", "lock1").ifPresent(Locked::unlock);

        verify(mockMap, times(1)).getLock("lock1");
        verify(optimisticLock, times(1)).tryLock();
    }

    @Test
    @DisplayName("tryLock with mapName and lockKey should return empty Optional when lock is owned")
    void tryLock_shouldTryLockOnMap_returnEmptyOptional() {
        when(redissonClient.<String, Object>getMap("map1")).thenReturn(mockMap);
        when(mockMap.getLock("lock1")).thenReturn(optimisticLock);
        when(optimisticLock.tryLock()).thenReturn(false);

        final Optional<Locked> locked = lockProvider.tryLock("map1", "lock1");
        assertNotNull(locked);
        assertFalse(locked.isPresent());

        verify(mockMap, times(1)).getLock("lock1");
    }

    @Test
    @DisplayName("tryLock without mapName and with lockKey should lock on default map")
    void tryLock_shouldLockWithDefaultMap_returnFullOptional() {
        when(redissonClient.<String, Object>getMap(DEFAULT_MAP_NAME)).thenReturn(mockMap);
        when(mockMap.getLock("lock1")).thenReturn(optimisticLock);
        when(optimisticLock.tryLock()).thenReturn(true);

        final Optional<Locked> locked = lockProvider.tryLock("lock1");
        assertNotNull(locked);
        assertTrue(locked.isPresent());

        verify(mockMap, times(1)).getLock("lock1");
    }

    @Test
    @DisplayName("tryLock without mapName and with lockKey should return empty Optional when lock is owned")
    void tryLock_shouldLockWithDefaultMap_returnEmptyOptional() {
        when(redissonClient.<String, Object>getMap(DEFAULT_MAP_NAME)).thenReturn(mockMap);
        when(mockMap.getLock("lock1")).thenReturn(optimisticLock);
        when(optimisticLock.tryLock()).thenReturn(false);

        final Optional<Locked> locked = lockProvider.tryLock("lock1");
        assertNotNull(locked);
        assertFalse(locked.isPresent());
    }

    @Test
    @DisplayName("lock without mapName and with lockKey should unlock on default map")
    void tryLock_shouldUnlockOnDefaultMap_returnFullOptional() {
        when(redissonClient.<String, Object>getMap(DEFAULT_MAP_NAME)).thenReturn(mockMap);
        when(mockMap.getLock("lock1")).thenReturn(optimisticLock);
        when(optimisticLock.tryLock()).thenReturn(true);

        lockProvider.tryLock("lock1").ifPresent(Locked::unlock);

        verify(mockMap, times(1)).getLock("lock1");
        verify(optimisticLock, times(1)).unlock();
    }

    @Test
    @DisplayName("executeInLock should lock in Redis map and unlock after task finishes")
    void executeInLock_shouldLockInMapAndUnlockAfterRunnableFinishes() {
        when(redissonClient.<String, Object>getMap("map1")).thenReturn(mockMap);
        when(mockMap.getFairLock("key1")).thenReturn(pessimisticLock);

        final AtomicInteger counter = new AtomicInteger(0);
        lockProvider.executeInLock("map1", "key1", () -> {
            counter.incrementAndGet();
            verify(mockMap, times(1)).getFairLock("key1");
            verify(pessimisticLock, never()).unlock();

        });

        verify(mockMap, times(1)).getFairLock("key1");
        verify(pessimisticLock, times(1)).unlock();
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInLock should lock in Redis map and unlock after task throws exception")
    void executeInLock_shouldLockInMapAndUnlockAfterRunnableThrowsException() {
        when(redissonClient.<String, Object>getMap("map1")).thenReturn(mockMap);
        when(mockMap.getFairLock("key1")).thenReturn(pessimisticLock);

        final AtomicInteger counter = new AtomicInteger(0);

        assertThrows(RuntimeException.class, () -> lockProvider.executeInLock("map1", "key1", (Runnable) () -> {
            verify(mockMap, times(1)).getFairLock("key1");
            verify(pessimisticLock, never()).unlock();
            counter.incrementAndGet();
            throw new RuntimeException("test");
        }));

        verify(mockMap, times(1)).getFairLock("key1");
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInLock should lock in Redis map and unlock after task finishes and return the value")
    void executeInLock_shouldLockInMapAndUnlockAfterSupplierFinishes() {
        when(redissonClient.<String, Object>getMap("map1")).thenReturn(mockMap);
        when(mockMap.getFairLock("key1")).thenReturn(pessimisticLock);

        final AtomicInteger counter = new AtomicInteger(0);
        int returnValue = lockProvider.executeInLock("map1", "key1", () -> {
            counter.incrementAndGet();
            verify(mockMap, times(1)).getFairLock("key1");
            verify(pessimisticLock, never()).unlock();
            return 3;
        });

        verify(mockMap, times(1)).getFairLock("key1");
        verify(pessimisticLock, times(1)).unlock();
        assertEquals(1, counter.get());
        assertEquals(3, returnValue);
    }

    @Test
    @DisplayName("executeInLock should lock in Redis map and unlock after supplier throws exception")
    void executeInLock_shouldLockInMapAndUnlockAfterSupplierThrowsException() {
        when(redissonClient.<String, Object>getMap("map1")).thenReturn(mockMap);
        when(mockMap.getLock("key1")).thenReturn(pessimisticLock);

        final AtomicInteger counter = new AtomicInteger(0);

        assertThrows(RuntimeException.class, () -> lockProvider.executeInLock("map1", "key1", (Supplier<String>) () -> {
            verify(mockMap, times(1)).getFairLock("key1");
            verify(pessimisticLock, never()).unlock();
            counter.incrementAndGet();
            throw new RuntimeException("test");
        }));

        verify(mockMap, times(1)).getFairLock("key1");
        verify(pessimisticLock, times(1)).unlock();
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInLock should lock in Redis default map and unlock after task finishes")
    void executeInLock_shouldLockAndUnlockAfterRunnableFinishes() {
        when(redissonClient.<String, Object>getMap(DEFAULT_MAP_NAME)).thenReturn(mockMap);
        when(mockMap.getFairLock("key1")).thenReturn(pessimisticLock);

        final AtomicInteger counter = new AtomicInteger(0);
        lockProvider.executeInLock("key1", () -> {
            counter.incrementAndGet();
            verify(mockMap, times(1)).getFairLock("key1");
            verify(pessimisticLock, never()).unlock();
        });

        verify(mockMap, times(1)).getFairLock("key1");
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInLock should lock in Redis default map and unlock after task throws exception")
    void executeInLock_shouldLockAndUnlockAfterRunnableThrowsException() {
        when(redissonClient.<String, Object>getMap(DEFAULT_MAP_NAME)).thenReturn(mockMap);
        when(mockMap.getFairLock("key1")).thenReturn(pessimisticLock);

        final AtomicInteger counter = new AtomicInteger(0);

        assertThrows(RuntimeException.class, () -> lockProvider.executeInLock("key1", (Runnable) () -> {
            verify(mockMap, times(1)).getFairLock("key1");
            verify(pessimisticLock, never()).unlock();
            counter.incrementAndGet();
            throw new RuntimeException("test");
        }));

        verify(mockMap, times(1)).getFairLock("key1");
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInLock should lock in Redis default map and unlock after task finishes and return the value")
    void executeInLock_shouldLockAndUnlockAfterSupplierFinishes() {
        when(redissonClient.<String, Object>getMap(DEFAULT_MAP_NAME)).thenReturn(mockMap);
        when(mockMap.getFairLock("key1")).thenReturn(pessimisticLock);

        final AtomicInteger counter = new AtomicInteger(0);
        int returnValue = lockProvider.executeInLock("key1", () -> {
            counter.incrementAndGet();
            verify(mockMap, times(1)).getFairLock("key1");
            verify(pessimisticLock, never()).unlock();
            return 3;
        });

        verify(mockMap, times(1)).getFairLock("key1");
        assertEquals(1, counter.get());
        assertEquals(3, returnValue);
    }

    @Test
    @DisplayName("executeInLock should lock in Redis default map and unlock after supplier throws exception")
    void executeInLock_shouldLockAndUnlockAfterSupplierThrowsException() {
        when(redissonClient.<String, Object>getMap(DEFAULT_MAP_NAME)).thenReturn(mockMap);
        when(mockMap.getFairLock("key1")).thenReturn(pessimisticLock);

        final AtomicInteger counter = new AtomicInteger(0);

        assertThrows(RuntimeException.class, () -> lockProvider.executeInLock("key1", (Supplier<String>) () -> {
            verify(mockMap, times(1)).getFairLock("key1");
            verify(pessimisticLock, never()).unlock();
            counter.incrementAndGet();
            throw new RuntimeException("test");
        }));

        verify(mockMap, times(1)).getFairLock("key1");
        verify(pessimisticLock, times(1)).unlock();
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInTryLock should lock in Redis map and unlock after task finishes")
    void executeInTryLock_shouldLockInMapAndUnlockAfterRunnableFinishes() {
        when(redissonClient.<String, Object>getMap("map1")).thenReturn(mockMap);
        when(mockMap.getLock("key1")).thenReturn(optimisticLock);
        when(optimisticLock.tryLock()).thenReturn(true);

        final AtomicInteger counter = new AtomicInteger(0);
        lockProvider.executeInTryLock("map1", "key1", () -> {
            counter.incrementAndGet();
            verify(mockMap, times(1)).getLock("key1");
            verify(optimisticLock, times(1)).tryLock();
            verify(optimisticLock, never()).unlock();
        });

        verify(mockMap, times(1)).getLock("key1");
        verify(optimisticLock, times(1)).unlock();
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInTryLock should lock in Redis map and unlock after task throws exception")
    void executeInTryLock_shouldLockInMapAndUnlockAfterRunnableThrowsException() {
        when(redissonClient.<String, Object>getMap("map1")).thenReturn(mockMap);
        when(mockMap.getLock("key1")).thenReturn(optimisticLock);
        when(optimisticLock.tryLock()).thenReturn(true);

        final AtomicInteger counter = new AtomicInteger(0);

        assertThrows(RuntimeException.class, () -> lockProvider.executeInTryLock("map1", "key1", (Runnable) () -> {
            verify(mockMap, times(1)).getLock("key1");
            verify(optimisticLock, never()).unlock();
            counter.incrementAndGet();
            throw new RuntimeException("test");
        }));

        verify(mockMap, times(1)).getLock("key1");
        verify(optimisticLock, times(1)).unlock();
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInTryLock should not lock in Redis map and unlock when tryLock returns false")
    void executeInTryLock_shouldNotLockInMapAndUnlockWhenTryLockReturnsFalse() {
        when(redissonClient.<String, Object>getMap("map1")).thenReturn(mockMap);
        when(mockMap.getLock("key1")).thenReturn(optimisticLock);
        when(optimisticLock.tryLock()).thenReturn(false);

        final AtomicInteger counter = new AtomicInteger(0);

        lockProvider.executeInTryLock("map1", "key1", (Runnable) counter::incrementAndGet);

        verify(mockMap, times(1)).getLock("key1");
        verify(optimisticLock, never()).unlock();
        assertEquals(0, counter.get());
    }

    @Test
    @DisplayName("executeInTryLock should lock in Redis map and unlock after task finishes and return the value")
    void executeTryInLock_shouldLockInMapAndUnlockAfterSupplierFinishes() {
        when(redissonClient.<String, Object>getMap("map1")).thenReturn(mockMap);
        when(mockMap.getLock("key1")).thenReturn(optimisticLock);
        when(optimisticLock.tryLock()).thenReturn(true);

        final AtomicInteger counter = new AtomicInteger(0);
        int returnValue = lockProvider.executeInTryLock("map1", "key1", () -> {
            counter.incrementAndGet();
            verify(mockMap, times(1)).getLock("key1");
            verify(optimisticLock, never()).unlock();
            return 3;
        });

        verify(mockMap, times(1)).getLock("key1");
        verify(optimisticLock, times(1)).unlock();
        assertEquals(1, counter.get());
        assertEquals(3, returnValue);
    }

    @Test
    @DisplayName("executeInTryLock should lock in Redis map and unlock after supplier throws exception")
    void executeInTryLock_shouldLockInMapAndUnlockAfterSupplierThrowsException() {
        when(redissonClient.<String, Object>getMap("map1")).thenReturn(mockMap);
        when(mockMap.getLock("key1")).thenReturn(optimisticLock);
        when(optimisticLock.tryLock()).thenReturn(true);

        final AtomicInteger counter = new AtomicInteger(0);

        assertThrows(RuntimeException.class, () -> lockProvider.executeInTryLock("map1", "key1", (Supplier<String>) () -> {
            verify(mockMap, times(1)).getLock("key1");
            verify(optimisticLock, times(1)).tryLock();
            verify(optimisticLock, never()).unlock();
            counter.incrementAndGet();
            throw new RuntimeException("test");
        }));

        verify(mockMap, times(1)).getLock("key1");
        verify(optimisticLock, times(1)).unlock();
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInTryLock should not lock in Redis map and unlock when lock is not available")
    void executeTryInLock_shouldLockInMapAndUnlockWhenLockIsNotAvailable() {
        when(redissonClient.<String, Object>getMap("map1")).thenReturn(mockMap);
        when(mockMap.getLock("key1")).thenReturn(optimisticLock);
        when(optimisticLock.tryLock()).thenReturn(false);

        final AtomicInteger counter = new AtomicInteger(0);
        Integer returnValue = lockProvider.executeInTryLock("map1", "key1", () -> {
            counter.incrementAndGet();
            return 3;
        });

        verify(mockMap, times(1)).getLock("key1");
        verify(optimisticLock, never()).unlock();
        assertEquals(0, counter.get());
        assertNull(returnValue);
    }

    @Test
    @DisplayName("executeInTryLock should lock in Redis and unlock after task finishes")
    void executeInTryLock_shouldLockAndUnlockAfterRunnableFinishes() {
        when(redissonClient.<String, Object>getMap(DEFAULT_MAP_NAME)).thenReturn(mockMap);
        when(mockMap.getLock("key1")).thenReturn(optimisticLock);
        when(optimisticLock.tryLock()).thenReturn(true);

        final AtomicInteger counter = new AtomicInteger(0);
        lockProvider.executeInTryLock("key1", () -> {
            counter.incrementAndGet();
            verify(mockMap, times(1)).getLock("key1");
            verify(optimisticLock, never()).unlock();
        });

        verify(mockMap, times(1)).getLock("key1");
        verify(optimisticLock, times(1)).unlock();
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInTryLock should lock in Redis and unlock after task throws exception")
    void executeInTryLock_shouldLockAndUnlockAfterRunnableThrowsException() {
        when(redissonClient.<String, Object>getMap(DEFAULT_MAP_NAME)).thenReturn(mockMap);
        when(mockMap.getLock("key1")).thenReturn(optimisticLock);
        when(optimisticLock.tryLock()).thenReturn(true);

        final AtomicInteger counter = new AtomicInteger(0);

        assertThrows(RuntimeException.class, () -> lockProvider.executeInTryLock("key1", (Runnable) () -> {
            verify(mockMap, times(1)).getLock("key1");
            verify(optimisticLock, never()).unlock();
            counter.incrementAndGet();
            throw new RuntimeException("test");
        }));

        verify(mockMap, times(1)).getLock("key1");
        verify(optimisticLock, times(1)).unlock();
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInTryLock should not lock in Redis and unlock when the lock is not available")
    void executeInTryLock_shouldNotLockAndUnlockWhenLockIsNotAvailable() {
        when(redissonClient.<String, Object>getMap(DEFAULT_MAP_NAME)).thenReturn(mockMap);
        when(mockMap.getLock("key1")).thenReturn(optimisticLock);
        when(optimisticLock.tryLock()).thenReturn(false);

        final AtomicInteger counter = new AtomicInteger(0);
        lockProvider.executeInTryLock("key1", (Runnable) counter::incrementAndGet);

        verify(mockMap, times(1)).getLock("key1");
        verify(optimisticLock, never()).unlock();
        assertEquals(0, counter.get());
    }

    @Test
    @DisplayName("executeInTryLock should lock in Redis and unlock after task finishes and return the value")
    void executeInTryLock_shouldLockAndUnlockAfterSupplierFinishes() {
        when(redissonClient.<String, Object>getMap(DEFAULT_MAP_NAME)).thenReturn(mockMap);
        when(mockMap.getLock("key1")).thenReturn(optimisticLock);
        when(optimisticLock.tryLock()).thenReturn(true);

        final AtomicInteger counter = new AtomicInteger(0);
        int returnValue = lockProvider.executeInTryLock("key1", () -> {
            counter.incrementAndGet();
            verify(mockMap, times(1)).getLock("key1");
            verify(optimisticLock, never()).unlock();
            return 3;
        });

        verify(mockMap, times(1)).getLock("key1");
        verify(optimisticLock, times(1)).unlock();
        assertEquals(1, counter.get());
        assertEquals(3, returnValue);
    }

    @Test
    @DisplayName("executeInTryLock should lock in Redis and unlock after supplier throws exception")
    void executeInTryLock_shouldLockAndUnlockAfterSupplierThrowsException() {
        when(redissonClient.<String, Object>getMap(DEFAULT_MAP_NAME)).thenReturn(mockMap);
        when(mockMap.getLock("key1")).thenReturn(optimisticLock);
        when(optimisticLock.tryLock()).thenReturn(true);

        final AtomicInteger counter = new AtomicInteger(0);

        assertThrows(RuntimeException.class, () -> lockProvider.executeInTryLock("key1", (Supplier<String>) () -> {
            verify(mockMap, times(1)).getLock("key1");
            verify(optimisticLock, never()).unlock();
            counter.incrementAndGet();
            throw new RuntimeException("test");
        }));

        verify(mockMap, times(1)).getLock("key1");
        verify(optimisticLock, times(1)).unlock();
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInTryLock should not lock in Redis and unlock when lock is not available and return null")
    void executeInTryLock_shouldNotLockAndUnlockWhenLockIsNotAvailableAndReturnNull() {
        when(redissonClient.<String, Object>getMap(DEFAULT_MAP_NAME)).thenReturn(mockMap);
        when(mockMap.getLock("key1")).thenReturn(optimisticLock);
        when(optimisticLock.tryLock()).thenReturn(false);

        final AtomicInteger counter = new AtomicInteger(0);
        final Integer returnValue = lockProvider.executeInTryLock("key1", () -> {
            counter.incrementAndGet();
            return 3;
        });

        verify(mockMap, times(1)).getLock("key1");
        verify(optimisticLock, never()).unlock();
        assertEquals(0, counter.get());
        assertNull(returnValue);
    }
}
