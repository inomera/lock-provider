package com.inomera.telco.commons.lock.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
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

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Serdar Kuzucu
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HazelcastLockProviderTest {
    private static final String DEFAULT_MAP_NAME = "defMapName";

    private HazelcastLockProvider lockProvider;

    @Mock
    private HazelcastInstance hazelcastInstance;

    @Mock
    private IMap<String, Object> mockMap;

    @BeforeEach
    void init() {
        lockProvider = new HazelcastLockProvider(hazelcastInstance, DEFAULT_MAP_NAME);
    }

    @Test
    @DisplayName("Constructor without default map name argument should initialize a default map name")
    void constructorWithoutDefaultMapName_shouldInitializeADefaultMapName() {
        when(hazelcastInstance.<String, Object>getMap(any())).thenReturn(mockMap);

        final HazelcastLockProvider hazelcastLockProvider = new HazelcastLockProvider(hazelcastInstance);
        final Locked locked = hazelcastLockProvider.lock("aLock");
        assertNotNull(locked);

        final ArgumentCaptor<String> mapNameCaptor = ArgumentCaptor.forClass(String.class);

        verify(hazelcastInstance, times(1)).<String, Object>getMap(mapNameCaptor.capture());
        final String mapName = mapNameCaptor.getValue();
        assertNotNull(mapName);
        assertFalse(mapName.isEmpty());
    }

    @Test
    @DisplayName("lock with mapName and lockKey should lock on a map")
    void lock_shouldLockOnMap() {
        when(hazelcastInstance.<String, Object>getMap("map1")).thenReturn(mockMap);

        final Locked locked = lockProvider.lock("map1", "lock1");
        assertNotNull(locked);

        verify(mockMap, times(1)).lock("lock1");
    }

    @Test
    @DisplayName("lock with mapName and lockKey should unlock on a map")
    void lock_shouldUnlockOnMap() {
        when(hazelcastInstance.<String, Object>getMap("map1")).thenReturn(mockMap);

        lockProvider.lock("map1", "lock1").unlock();

        verify(mockMap, times(1)).unlock("lock1");
    }

    @Test
    @DisplayName("lock without mapName and with lockKey should lock on default map")
    void lock_shouldLockWithDefaultMap() {
        when(hazelcastInstance.<String, Object>getMap(DEFAULT_MAP_NAME)).thenReturn(mockMap);

        final Locked locked = lockProvider.lock("lock1");
        assertNotNull(locked);

        verify(mockMap, times(1)).lock("lock1");
    }

    @Test
    @DisplayName("lock without mapName and with lockKey should unlock on default map")
    void lock_shouldUnlockOnILock() {
        when(hazelcastInstance.<String, Object>getMap(DEFAULT_MAP_NAME)).thenReturn(mockMap);

        lockProvider.lock("lock1").unlock();

        verify(mockMap, times(1)).unlock("lock1");
    }

    @Test
    @DisplayName("tryLock with mapName and lockKey should lock on a map")
    void tryLock_shouldTryLockOnMap_returnFullOptional() {
        when(hazelcastInstance.<String, Object>getMap("map1")).thenReturn(mockMap);
        when(mockMap.tryLock("lock1")).thenReturn(true);

        final Optional<Locked> locked = lockProvider.tryLock("map1", "lock1");
        assertNotNull(locked);
        assertTrue(locked.isPresent());

        verify(mockMap, times(1)).tryLock("lock1");
    }

    @Test
    @DisplayName("tryLock with mapName and lockKey should unlock on a map")
    void tryLock_shouldUnlockOnMap() {
        when(hazelcastInstance.<String, Object>getMap("map1")).thenReturn(mockMap);
        when(mockMap.tryLock("lock1")).thenReturn(true);

        lockProvider.tryLock("map1", "lock1").ifPresent(Locked::unlock);

        verify(mockMap, times(1)).unlock("lock1");
    }

    @Test
    @DisplayName("tryLock with mapName and lockKey should return empty Optional when lock is owned")
    void tryLock_shouldTryLockOnMap_returnEmptyOptional() {
        when(hazelcastInstance.<String, Object>getMap("map1")).thenReturn(mockMap);
        when(mockMap.tryLock("lock1")).thenReturn(false);

        final Optional<Locked> locked = lockProvider.tryLock("map1", "lock1");
        assertNotNull(locked);
        assertFalse(locked.isPresent());

        verify(mockMap, times(1)).tryLock("lock1");
    }

    @Test
    @DisplayName("tryLock without mapName and with lockKey should lock on default map")
    void tryLock_shouldLockWithDefaultMap_returnFullOptional() {
        when(hazelcastInstance.<String, Object>getMap(DEFAULT_MAP_NAME)).thenReturn(mockMap);
        when(mockMap.tryLock("lock1")).thenReturn(true);

        final Optional<Locked> locked = lockProvider.tryLock("lock1");
        assertNotNull(locked);
        assertTrue(locked.isPresent());

        verify(mockMap, times(1)).tryLock("lock1");
    }

    @Test
    @DisplayName("tryLock without mapName and with lockKey should return empty Optional when lock is owned")
    void tryLock_shouldLockWithDefaultMap_returnEmptyOptional() {
        when(hazelcastInstance.<String, Object>getMap(DEFAULT_MAP_NAME)).thenReturn(mockMap);
        when(mockMap.tryLock("lock1")).thenReturn(false);

        final Optional<Locked> locked = lockProvider.tryLock("lock1");
        assertNotNull(locked);
        assertFalse(locked.isPresent());
    }

    @Test
    @DisplayName("lock without mapName and with lockKey should unlock on default map")
    void tryLock_shouldUnlockOnDefaultMap_returnFullOptional() {
        when(hazelcastInstance.<String, Object>getMap(DEFAULT_MAP_NAME)).thenReturn(mockMap);
        when(mockMap.tryLock("lock1")).thenReturn(true);

        lockProvider.tryLock("lock1").ifPresent(Locked::unlock);

        verify(mockMap, times(1)).unlock("lock1");
    }

    @Test
    @DisplayName("executeInLock should lock in HZ map and unlock after task finishes")
    void executeInLock_shouldLockInMapAndUnlockAfterRunnableFinishes() {
        when(hazelcastInstance.<String, Object>getMap("map1")).thenReturn(mockMap);

        final AtomicInteger counter = new AtomicInteger(0);
        lockProvider.executeInLock("map1", "key1", () -> {
            counter.incrementAndGet();
            verify(mockMap, times(1)).lock("key1");
            verify(mockMap, never()).unlock("key1");
        });

        verify(mockMap, times(1)).unlock("key1");
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInLock should lock in HZ map and unlock after task throws exception")
    void executeInLock_shouldLockInMapAndUnlockAfterRunnableThrowsException() {
        when(hazelcastInstance.<String, Object>getMap("map1")).thenReturn(mockMap);

        final AtomicInteger counter = new AtomicInteger(0);

        assertThrows(RuntimeException.class, () -> lockProvider.executeInLock("map1", "key1", (Runnable) () -> {
            verify(mockMap, times(1)).lock("key1");
            verify(mockMap, never()).unlock("key1");
            counter.incrementAndGet();
            throw new RuntimeException("test");
        }));

        verify(mockMap, times(1)).unlock("key1");
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInLock should lock in HZ map and unlock after task finishes and return the value")
    void executeInLock_shouldLockInMapAndUnlockAfterSupplierFinishes() {
        when(hazelcastInstance.<String, Object>getMap("map1")).thenReturn(mockMap);

        final AtomicInteger counter = new AtomicInteger(0);
        int returnValue = lockProvider.executeInLock("map1", "key1", () -> {
            counter.incrementAndGet();
            verify(mockMap, times(1)).lock("key1");
            verify(mockMap, never()).unlock("key1");
            return 3;
        });

        verify(mockMap, times(1)).unlock("key1");
        assertEquals(1, counter.get());
        assertEquals(3, returnValue);
    }

    @Test
    @DisplayName("executeInLock should lock in HZ map and unlock after supplier throws exception")
    void executeInLock_shouldLockInMapAndUnlockAfterSupplierThrowsException() {
        when(hazelcastInstance.<String, Object>getMap("map1")).thenReturn(mockMap);

        final AtomicInteger counter = new AtomicInteger(0);

        assertThrows(RuntimeException.class, () -> lockProvider.executeInLock("map1", "key1", (Supplier<String>) () -> {
            verify(mockMap, times(1)).lock("key1");
            verify(mockMap, never()).unlock("key1");
            counter.incrementAndGet();
            throw new RuntimeException("test");
        }));

        verify(mockMap, times(1)).unlock("key1");
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInLock should lock in HZ default map and unlock after task finishes")
    void executeInLock_shouldLockAndUnlockAfterRunnableFinishes() {
        when(hazelcastInstance.<String, Object>getMap(DEFAULT_MAP_NAME)).thenReturn(mockMap);

        final AtomicInteger counter = new AtomicInteger(0);
        lockProvider.executeInLock("key1", () -> {
            counter.incrementAndGet();
            verify(mockMap, times(1)).lock("key1");
            verify(mockMap, never()).unlock("key1");
        });

        verify(mockMap, times(1)).unlock("key1");
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInLock should lock in HZ default map and unlock after task throws exception")
    void executeInLock_shouldLockAndUnlockAfterRunnableThrowsException() {
        when(hazelcastInstance.<String, Object>getMap(DEFAULT_MAP_NAME)).thenReturn(mockMap);

        final AtomicInteger counter = new AtomicInteger(0);

        assertThrows(RuntimeException.class, () -> lockProvider.executeInLock("key1", (Runnable) () -> {
            verify(mockMap, times(1)).lock("key1");
            verify(mockMap, never()).unlock("key1");
            counter.incrementAndGet();
            throw new RuntimeException("test");
        }));

        verify(mockMap, times(1)).unlock("key1");
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInLock should lock in HZ default map and unlock after task finishes and return the value")
    void executeInLock_shouldLockAndUnlockAfterSupplierFinishes() {
        when(hazelcastInstance.<String, Object>getMap(DEFAULT_MAP_NAME)).thenReturn(mockMap);

        final AtomicInteger counter = new AtomicInteger(0);
        int returnValue = lockProvider.executeInLock("key1", () -> {
            counter.incrementAndGet();
            verify(mockMap, times(1)).lock("key1");
            verify(mockMap, never()).unlock("key1");
            return 3;
        });

        verify(mockMap, times(1)).unlock("key1");
        assertEquals(1, counter.get());
        assertEquals(3, returnValue);
    }

    @Test
    @DisplayName("executeInLock should lock in HZ default map and unlock after supplier throws exception")
    void executeInLock_shouldLockAndUnlockAfterSupplierThrowsException() {
        when(hazelcastInstance.<String, Object>getMap(DEFAULT_MAP_NAME)).thenReturn(mockMap);

        final AtomicInteger counter = new AtomicInteger(0);

        assertThrows(RuntimeException.class, () -> lockProvider.executeInLock("key1", (Supplier<String>) () -> {
            verify(mockMap, times(1)).lock("key1");
            verify(mockMap, never()).unlock("key1");
            counter.incrementAndGet();
            throw new RuntimeException("test");
        }));

        verify(mockMap, times(1)).unlock("key1");
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInTryLock should lock in HZ map and unlock after task finishes")
    void executeInTryLock_shouldLockInMapAndUnlockAfterRunnableFinishes() {
        when(hazelcastInstance.<String, Object>getMap("map1")).thenReturn(mockMap);
        when(mockMap.tryLock("key1")).thenReturn(true);

        final AtomicInteger counter = new AtomicInteger(0);
        lockProvider.executeInTryLock("map1", "key1", () -> {
            counter.incrementAndGet();
            verify(mockMap, times(1)).tryLock("key1");
            verify(mockMap, never()).unlock("key1");
        });

        verify(mockMap, times(1)).unlock("key1");
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInTryLock should lock in HZ map and unlock after task throws exception")
    void executeInTryLock_shouldLockInMapAndUnlockAfterRunnableThrowsException() {
        when(hazelcastInstance.<String, Object>getMap("map1")).thenReturn(mockMap);
        when(mockMap.tryLock("key1")).thenReturn(true);

        final AtomicInteger counter = new AtomicInteger(0);

        assertThrows(RuntimeException.class, () -> lockProvider.executeInTryLock("map1", "key1", (Runnable) () -> {
            verify(mockMap, times(1)).tryLock("key1");
            verify(mockMap, never()).unlock("key1");
            counter.incrementAndGet();
            throw new RuntimeException("test");
        }));

        verify(mockMap, times(1)).unlock("key1");
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInTryLock should not lock in HZ map and unlock when tryLock returns false")
    void executeInTryLock_shouldNotLockInMapAndUnlockWhenTryLockReturnsFalse() {
        when(hazelcastInstance.<String, Object>getMap("map1")).thenReturn(mockMap);
        when(mockMap.tryLock("key1")).thenReturn(false);

        final AtomicInteger counter = new AtomicInteger(0);

        lockProvider.executeInTryLock("map1", "key1", (Runnable) counter::incrementAndGet);

        verify(mockMap, times(1)).tryLock("key1");
        verify(mockMap, never()).unlock("key1");
        assertEquals(0, counter.get());
    }

    @Test
    @DisplayName("executeInTryLock should lock in HZ map and unlock after task finishes and return the value")
    void executeTryInLock_shouldLockInMapAndUnlockAfterSupplierFinishes() {
        when(hazelcastInstance.<String, Object>getMap("map1")).thenReturn(mockMap);
        when(mockMap.tryLock("key1")).thenReturn(true);

        final AtomicInteger counter = new AtomicInteger(0);
        int returnValue = lockProvider.executeInTryLock("map1", "key1", () -> {
            counter.incrementAndGet();
            verify(mockMap, times(1)).tryLock("key1");
            verify(mockMap, never()).unlock("key1");
            return 3;
        });

        verify(mockMap, times(1)).unlock("key1");
        assertEquals(1, counter.get());
        assertEquals(3, returnValue);
    }

    @Test
    @DisplayName("executeInTryLock should lock in HZ map and unlock after supplier throws exception")
    void executeInTryLock_shouldLockInMapAndUnlockAfterSupplierThrowsException() {
        when(hazelcastInstance.<String, Object>getMap("map1")).thenReturn(mockMap);
        when(mockMap.tryLock("key1")).thenReturn(true);

        final AtomicInteger counter = new AtomicInteger(0);

        assertThrows(RuntimeException.class, () -> lockProvider.executeInTryLock("map1", "key1", (Supplier<String>) () -> {
            verify(mockMap, times(1)).tryLock("key1");
            verify(mockMap, never()).unlock("key1");
            counter.incrementAndGet();
            throw new RuntimeException("test");
        }));

        verify(mockMap, times(1)).unlock("key1");
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInTryLock should not lock in HZ map and unlock when lock is not available")
    void executeTryInLock_shouldLockInMapAndUnlockWhenLockIsNotAvailable() {
        when(hazelcastInstance.<String, Object>getMap("map1")).thenReturn(mockMap);
        when(mockMap.tryLock("key1")).thenReturn(false);

        final AtomicInteger counter = new AtomicInteger(0);
        Integer returnValue = lockProvider.executeInTryLock("map1", "key1", () -> {
            counter.incrementAndGet();
            return 3;
        });

        verify(mockMap, times(1)).tryLock("key1");
        verify(mockMap, never()).unlock("key1");
        assertEquals(0, counter.get());
        assertNull(returnValue);
    }

    @Test
    @DisplayName("executeInTryLock should lock in HZ and unlock after task finishes")
    void executeInTryLock_shouldLockAndUnlockAfterRunnableFinishes() {
        when(hazelcastInstance.<String, Object>getMap(DEFAULT_MAP_NAME)).thenReturn(mockMap);
        when(mockMap.tryLock("key1")).thenReturn(true);

        final AtomicInteger counter = new AtomicInteger(0);
        lockProvider.executeInTryLock("key1", () -> {
            counter.incrementAndGet();
            verify(mockMap, times(1)).tryLock("key1");
            verify(mockMap, never()).unlock("key1");
        });

        verify(mockMap, times(1)).unlock("key1");
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInTryLock should lock in HZ and unlock after task throws exception")
    void executeInTryLock_shouldLockAndUnlockAfterRunnableThrowsException() {
        when(hazelcastInstance.<String, Object>getMap(DEFAULT_MAP_NAME)).thenReturn(mockMap);
        when(mockMap.tryLock("key1")).thenReturn(true);

        final AtomicInteger counter = new AtomicInteger(0);

        assertThrows(RuntimeException.class, () -> lockProvider.executeInTryLock("key1", (Runnable) () -> {
            verify(mockMap, times(1)).tryLock("key1");
            verify(mockMap, never()).unlock("key1");
            counter.incrementAndGet();
            throw new RuntimeException("test");
        }));

        verify(mockMap, times(1)).unlock("key1");
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInTryLock should not lock in HZ and unlock when the lock is not available")
    void executeInTryLock_shouldNotLockAndUnlockWhenLockIsNotAvailable() {
        when(hazelcastInstance.<String, Object>getMap(DEFAULT_MAP_NAME)).thenReturn(mockMap);
        when(mockMap.tryLock("key1")).thenReturn(false);

        final AtomicInteger counter = new AtomicInteger(0);
        lockProvider.executeInTryLock("key1", (Runnable) counter::incrementAndGet);

        verify(mockMap, times(1)).tryLock("key1");
        verify(mockMap, never()).unlock("key1");
        assertEquals(0, counter.get());
    }

    @Test
    @DisplayName("executeInTryLock should lock in HZ and unlock after task finishes and return the value")
    void executeInTryLock_shouldLockAndUnlockAfterSupplierFinishes() {
        when(hazelcastInstance.<String, Object>getMap(DEFAULT_MAP_NAME)).thenReturn(mockMap);
        when(mockMap.tryLock("key1")).thenReturn(true);

        final AtomicInteger counter = new AtomicInteger(0);
        int returnValue = lockProvider.executeInTryLock("key1", () -> {
            counter.incrementAndGet();
            verify(mockMap, times(1)).tryLock("key1");
            verify(mockMap, never()).unlock("key1");
            return 3;
        });

        verify(mockMap, times(1)).unlock("key1");
        assertEquals(1, counter.get());
        assertEquals(3, returnValue);
    }

    @Test
    @DisplayName("executeInTryLock should lock in HZ and unlock after supplier throws exception")
    void executeInTryLock_shouldLockAndUnlockAfterSupplierThrowsException() {
        when(hazelcastInstance.<String, Object>getMap(DEFAULT_MAP_NAME)).thenReturn(mockMap);
        when(mockMap.tryLock("key1")).thenReturn(true);

        final AtomicInteger counter = new AtomicInteger(0);

        assertThrows(RuntimeException.class, () -> lockProvider.executeInTryLock("key1", (Supplier<String>) () -> {
            verify(mockMap, times(1)).tryLock("key1");
            verify(mockMap, never()).unlock("key1");
            counter.incrementAndGet();
            throw new RuntimeException("test");
        }));

        verify(mockMap, times(1)).unlock("key1");
        assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("executeInTryLock should not lock in HZ and unlock when lock is not available and return null")
    void executeInTryLock_shouldNotLockAndUnlockWhenLockIsNotAvailableAndReturnNull() {
        when(hazelcastInstance.<String, Object>getMap(DEFAULT_MAP_NAME)).thenReturn(mockMap);
        when(mockMap.tryLock("key1")).thenReturn(false);

        final AtomicInteger counter = new AtomicInteger(0);
        final Integer returnValue = lockProvider.executeInTryLock("key1", () -> {
            counter.incrementAndGet();
            return 3;
        });

        verify(mockMap, times(1)).tryLock("key1");
        verify(mockMap, never()).unlock("key1");
        assertEquals(0, counter.get());
        assertNull(returnValue);
    }
}
