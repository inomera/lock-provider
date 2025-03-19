package com.inomera.telco.commons.lock;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Base implementation of the {@link LockProvider} interface.
 * This class provides common locking functionality, including executing operations within locks
 * and handling both mandatory and optional (try-lock) locking mechanisms.
 *
 * It ensures that locks are released properly, even in case of exceptions.
 *
 * @author Serdar Kuzucu
 */
public abstract class BaseLockProvider implements LockProvider {

    /**
     * Executes a runnable within a lock. The lock is acquired using a lock map and a lock key.
     *
     * @param lockMap  the name of the lock map
     * @param lockKey  the key used to acquire the lock
     * @param runnable the task to be executed within the lock
     */
    @Override
    public void executeInLock(String lockMap, String lockKey, Runnable runnable) {
        final Locked locked = lock(lockMap, lockKey);
        executeAndUnlock(runnable, locked);
    }

    /**
     * Executes a supplier within a lock and returns the result. The lock is acquired using a lock map and a lock key.
     *
     * @param lockMap  the name of the lock map
     * @param lockKey  the key used to acquire the lock
     * @param supplier the supplier to be executed within the lock
     * @param <T>      the type of the returned result
     * @return the result produced by the supplier
     */
    @Override
    public <T> T executeInLock(String lockMap, String lockKey, Supplier<T> supplier) {
        final Locked locked = lock(lockMap, lockKey);
        return executeAndUnlock(supplier, locked);
    }

    /**
     * Executes a runnable within a lock, using only a lock name.
     *
     * @param lockName the name of the lock
     * @param runnable the task to be executed within the lock
     */
    @Override
    public void executeInLock(String lockName, Runnable runnable) {
        final Locked locked = lock(lockName);
        executeAndUnlock(runnable, locked);
    }

    /**
     * Executes a supplier within a lock and returns the result, using only a lock name.
     *
     * @param lockName the name of the lock
     * @param supplier the supplier to be executed within the lock
     * @param <T>      the type of the returned result
     * @return the result produced by the supplier
     */
    @Override
    public <T> T executeInLock(String lockName, Supplier<T> supplier) {
        final Locked locked = lock(lockName);
        return executeAndUnlock(supplier, locked);
    }

    /**
     * Attempts to acquire a lock and execute a runnable within it. If the lock cannot be acquired, the runnable is not executed.
     *
     * @param lockMap  the name of the lock map
     * @param lockKey  the key used to attempt acquiring the lock
     * @param runnable the task to be executed if the lock is acquired
     */
    @Override
    public void executeInTryLock(String lockMap, String lockKey, Runnable runnable) {
        final Optional<Locked> lockedOptional = tryLock(lockMap, lockKey);
        lockedOptional.ifPresent(locked -> executeAndUnlock(runnable, locked));
    }

    /**
     * Attempts to acquire a lock and execute a supplier within it, returning the result. If the lock cannot be acquired, returns null.
     *
     * @param lockMap  the name of the lock map
     * @param lockKey  the key used to attempt acquiring the lock
     * @param supplier the supplier to be executed if the lock is acquired
     * @param <T>      the type of the returned result
     * @return the result produced by the supplier, or null if the lock could not be acquired
     */
    @Override
    public <T> T executeInTryLock(String lockMap, String lockKey, Supplier<T> supplier) {
        final Optional<Locked> lockedOptional = tryLock(lockMap, lockKey);
        return lockedOptional.map(locked -> executeAndUnlock(supplier, locked)).orElse(null);
    }

    /**
     * Attempts to acquire a lock and execute a runnable within it, using only a lock name.
     *
     * @param lockName the name of the lock
     * @param runnable the task to be executed if the lock is acquired
     */
    @Override
    public void executeInTryLock(String lockName, Runnable runnable) {
        final Optional<Locked> lockedOptional = tryLock(lockName);
        lockedOptional.ifPresent(locked -> executeAndUnlock(runnable, locked));
    }

    /**
     * Attempts to acquire a lock and execute a supplier within it, using only a lock name. Returns null if the lock cannot be acquired.
     *
     * @param lockName the name of the lock
     * @param supplier the supplier to be executed if the lock is acquired
     * @param <T>      the type of the returned result
     * @return the result produced by the supplier, or null if the lock could not be acquired
     */
    @Override
    public <T> T executeInTryLock(String lockName, Supplier<T> supplier) {
        final Optional<Locked> lockedOptional = tryLock(lockName);
        return lockedOptional.map(locked -> executeAndUnlock(supplier, locked)).orElse(null);
    }

    /**
     * Executes the given runnable and ensures the lock is released afterward.
     *
     * @param runnable the task to be executed
     * @param locked   the acquired lock
     */
    private void executeAndUnlock(Runnable runnable, Locked locked) {
        try {
            runnable.run();
        } finally {
            locked.unlock();
        }
    }

    /**
     * Executes the given supplier and ensures the lock is released afterward, returning the result.
     *
     * @param supplier the supplier to be executed
     * @param locked   the acquired lock
     * @param <T>      the type of the returned result
     * @return the result produced by the supplier
     */
    private <T> T executeAndUnlock(Supplier<T> supplier, Locked locked) {
        try {
            return supplier.get();
        } finally {
            locked.unlock();
        }
    }
}
