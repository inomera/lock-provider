package com.inomera.telco.commons.lock.reentrant;

import com.inomera.telco.commons.lock.BaseLockProvider;
import com.inomera.telco.commons.lock.LockProvider;
import com.inomera.telco.commons.lock.Locked;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Non-distributed local implementation of LockProvider which uses ReentrantLock
 *
 * @author Serdar Kuzucu
 */
public class LocalReentrantLockProvider extends BaseLockProvider implements LockProvider {
    private final Map<LockMapKey, CountingReentrantLock> lockMap = new ConcurrentHashMap<>();

    @Override
    public Locked lock(String lockMap, String lockKey) {
        return lock(new CompositeLockMapKey(lockMap, lockKey));
    }

    @Override
    public Optional<Locked> tryLock(String lockMap, String lockKey) {
        return tryLock(new CompositeLockMapKey(lockMap, lockKey));
    }

    @Override
    public Locked lock(String lockName) {
        return lock(new SimpleLockMapKey(lockName));
    }

    @Override
    public Optional<Locked> tryLock(String lockName) {
        return tryLock(new SimpleLockMapKey(lockName));
    }

    private Locked lock(LockMapKey lockName) {
        return doLock(lockName, (lock) -> {
            lock.lock();
            return () -> unlock(lockName);
        });
    }

    private Optional<Locked> tryLock(LockMapKey lockName) {
        return doLock(lockName, (lock) -> {
            boolean locked = lock.tryLock();
            if (!locked) {
                lock.decrementCounterAndGetValue();
                return Optional.empty();
            }
            return Optional.of(() -> unlock(lockName));
        });
    }

    private <T> T doLock(LockMapKey lockKey, Function<CountingReentrantLock, T> lockFunction) {
        final CountingReentrantLock lock;

        synchronized (lockMap) {
            lock = lockMap.get(lockKey);
            if (lock == null) {
                CountingReentrantLock newLock = new CountingReentrantLock();
                lockMap.put(lockKey, newLock);
                return lockFunction.apply(newLock);
            } else {
                lock.incrementCounterAndGetValue();
            }
        }

        return lockFunction.apply(lock);
    }

    private void unlock(LockMapKey lockKey) {
        final CountingReentrantLock lock;

        synchronized (lockMap) {
            lock = lockMap.get(lockKey);
            if (lock != null) {
                if (lock.decrementCounterAndGetValue() <= 0) {
                    lockMap.remove(lockKey);
                }
            } else {
                throw new IllegalMonitorStateException("Lock for key " + lockKey + " is not owned by the current thread");
            }
        }

        lock.unlock();
    }

    public int size() {
        synchronized (lockMap) {
            return lockMap.size();
        }
    }
}
