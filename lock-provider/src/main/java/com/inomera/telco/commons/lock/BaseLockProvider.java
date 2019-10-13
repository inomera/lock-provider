package com.inomera.telco.commons.lock;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Serdar Kuzucu
 */
public abstract class BaseLockProvider implements LockProvider {
    @Override
    public void executeInLock(String lockMap, String lockKey, Runnable runnable) {
        final Locked locked = lock(lockMap, lockKey);
        executeAndUnlock(runnable, locked);
    }

    @Override
    public <T> T executeInLock(String lockMap, String lockKey, Supplier<T> supplier) {
        final Locked locked = lock(lockMap, lockKey);
        return executeAndUnlock(supplier, locked);
    }

    @Override
    public void executeInLock(String lockName, Runnable runnable) {
        final Locked locked = lock(lockName);
        executeAndUnlock(runnable, locked);
    }

    @Override
    public <T> T executeInLock(String lockName, Supplier<T> supplier) {
        final Locked locked = lock(lockName);
        return executeAndUnlock(supplier, locked);
    }

    @Override
    public void executeInTryLock(String lockMap, String lockKey, Runnable runnable) {
        final Optional<Locked> lockedOptional = tryLock(lockMap, lockKey);
        lockedOptional.ifPresent(locked -> executeAndUnlock(runnable, locked));
    }

    @Override
    public <T> T executeInTryLock(String lockMap, String lockKey, Supplier<T> supplier) {
        final Optional<Locked> lockedOptional = tryLock(lockMap, lockKey);
        return lockedOptional.map(locked -> executeAndUnlock(supplier, locked)).orElse(null);
    }

    @Override
    public void executeInTryLock(String lockName, Runnable runnable) {
        final Optional<Locked> lockedOptional = tryLock(lockName);
        lockedOptional.ifPresent(locked -> executeAndUnlock(runnable, locked));
    }

    @Override
    public <T> T executeInTryLock(String lockName, Supplier<T> supplier) {
        final Optional<Locked> lockedOptional = tryLock(lockName);
        return lockedOptional.map(locked -> executeAndUnlock(supplier, locked)).orElse(null);
    }

    private void executeAndUnlock(Runnable runnable, Locked locked) {
        try {
            runnable.run();
        } finally {
            locked.unlock();
        }
    }

    private <T> T executeAndUnlock(Supplier<T> supplier, Locked locked) {
        try {
            return supplier.get();
        } finally {
            locked.unlock();
        }
    }
}
