package com.inomera.telco.commons.lock.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.inomera.telco.commons.lock.BaseLockProvider;
import com.inomera.telco.commons.lock.LockProvider;
import com.inomera.telco.commons.lock.Locked;

import java.util.Objects;
import java.util.Optional;

/**
 * Use to lock in a distributed environment
 *
 * @author Serdar Kuzucu
 */
public class HazelcastLockProvider extends BaseLockProvider implements LockProvider {
    private static final String DEFAULT_LOCK_MAP = "HazelcastLockProvider_DefaultLockMap";

    private final HazelcastInstance hazelcastInstance;
    private final String defaultLockMap;

    /**
     * Creates a new HazelcastLockProvider with given HazelcastInstance and
     * "HazelcastLockProvider_DefaultLockMap" as default lock map.
     *
     * @param hazelcastInstance HazelcastInstance to get lock maps
     */
    public HazelcastLockProvider(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = Objects.requireNonNull(hazelcastInstance, "HazelcastInstance is required!");
        this.defaultLockMap = DEFAULT_LOCK_MAP;
    }

    /**
     * Creates a new HazelcastLockProvider with given HazelcastInstance and
     * given default lock map name.
     *
     * @param hazelcastInstance HazelcastInstance to get lock maps
     * @param defaultLockMap    name of the default lock map
     */
    public HazelcastLockProvider(HazelcastInstance hazelcastInstance, String defaultLockMap) {
        this.hazelcastInstance = hazelcastInstance;
        this.defaultLockMap = Objects.requireNonNull(defaultLockMap, "Default lock map name cannot be null");
    }

    @Override
    public Locked lock(String lockMap, String lockKey) {
        final IMap<String, ?> lockMapInstance = getLockMap(lockMap);

        lockMapInstance.lock(lockKey);

        return () -> lockMapInstance.unlock(lockKey);
    }

    @Override
    public Optional<Locked> tryLock(String lockMap, String lockKey) {
        final IMap<String, ?> lockMapInstance = getLockMap(lockMap);
        final boolean isLocked = lockMapInstance.tryLock(lockKey);

        if (isLocked) {
            return Optional.of(() -> lockMapInstance.unlock(lockKey));
        }

        return Optional.empty();
    }

    @Override
    public Locked lock(String lockName) {
        return lock(defaultLockMap, lockName);
    }

    @Override
    public Optional<Locked> tryLock(String lockName) {
        return tryLock(defaultLockMap, lockName);
    }

    private IMap<String, ?> getLockMap(String mapName) {
        return hazelcastInstance.getMap(mapName);
    }
}
