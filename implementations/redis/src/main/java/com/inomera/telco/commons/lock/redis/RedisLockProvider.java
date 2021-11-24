package com.inomera.telco.commons.lock.redis;

import com.inomera.telco.commons.lock.BaseLockProvider;
import com.inomera.telco.commons.lock.LockProvider;
import com.inomera.telco.commons.lock.Locked;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.Objects;
import java.util.Optional;

/**
 * Use to lock in a distributed environment
 *
 * @author Turgay Can
 */
public class RedisLockProvider extends BaseLockProvider implements LockProvider {
    private static final String DEFAULT_LOCK_MAP = "RedisLockProvider_DefaultLockMap";

    private final RedissonClient redisson;
    private final String defaultLockMap;

    /**
     * Creates a new RedisLockProvider with given RedissonClient and
     * "RedisLockProvider_DefaultLockMap" as default lock map.
     *
     * @param redisson RedissonClient to get lock maps
     */
    public RedisLockProvider(RedissonClient redisson) {
        this.redisson = Objects.requireNonNull(redisson, "RedissonClient is required!");
        this.defaultLockMap = DEFAULT_LOCK_MAP;
    }

    /**
     * Creates a new RedisLockProvider with given RedissonClient and
     * given default lock map name.
     *
     * @param redisson       RedissonClient to get lock maps
     * @param defaultLockMap name of the default lock map
     */
    public RedisLockProvider(RedissonClient redisson, String defaultLockMap) {
        this.redisson = redisson;
        this.defaultLockMap = Objects.requireNonNull(defaultLockMap, "Default redis lock map name cannot be null");
    }

    @Override
    public Locked lock(String lockMap, String lockKey) {
        final RMap<String, ?> lockMapInstance = getLockMap(lockMap);

        final RLock fairLock = lockMapInstance.getFairLock(lockKey);
        fairLock.lock();

        return fairLock::unlock;
    }

    @Override
    public Optional<Locked> tryLock(String lockMap, String lockKey) {
        final RMap<String, ?> lockMapInstance = getLockMap(lockMap);
        final RLock lock = lockMapInstance.getLock(lockKey);

        if (lock.tryLock()) {
            return Optional.of(lock::unlock);
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

    private RMap<String, ?> getLockMap(String mapName) {
        return redisson.getMap(mapName);
    }
}
