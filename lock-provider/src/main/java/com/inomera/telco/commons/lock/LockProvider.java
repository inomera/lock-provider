package com.inomera.telco.commons.lock;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Interface defining the contract for a lock provider.
 * <p>
 * Provides methods to acquire, attempt, and execute code blocks within locks.
 * Supports both mandatory and optional (try-lock) mechanisms.
 *
 * @author Serdar Kuzucu
 * @author Turgay Can
 */
public interface LockProvider {

    /**
     * Acquires a lock using a lock map and lock key.
     *
     * @param lockMap the name of the lock map
     * @param lockKey the key used to acquire the lock
     * @return the acquired {@link Locked} object
     */
    Locked lock(String lockMap, String lockKey);

    /**
     * Attempts to acquire a lock using a lock map and lock key.
     *
     * @param lockMap the name of the lock map
     * @param lockKey the key used to attempt acquiring the lock
     * @return an {@link Optional} containing the acquired {@link Locked} object if successful, or an empty {@link Optional} if not
     */
    Optional<Locked> tryLock(String lockMap, String lockKey);

    /**
     * Acquires a lock using only a lock name.
     *
     * @param lockName the name of the lock
     * @return the acquired {@link Locked} object
     */
    Locked lock(String lockName);

    /**
     * Attempts to acquire a lock using only a lock name.
     *
     * @param lockName the name of the lock
     * @return an {@link Optional} containing the acquired {@link Locked} object if successful, or an empty {@link Optional} if not
     */
    Optional<Locked> tryLock(String lockName);

    /**
     * Executes a runnable within a lock acquired using a lock map and lock key.
     *
     * @param lockMap  the name of the lock map
     * @param lockKey  the key used to acquire the lock
     * @param runnable the task to be executed within the lock
     */
    void executeInLock(String lockMap, String lockKey, Runnable runnable);

    /**
     * Executes a supplier within a lock and returns the result.
     *
     * @param lockMap  the name of the lock map
     * @param lockKey  the key used to acquire the lock
     * @param supplier the supplier to be executed within the lock
     * @param <T>      the type of the returned result
     * @return the result produced by the supplier
     */
    <T> T executeInLock(String lockMap, String lockKey, Supplier<T> supplier);

    /**
     * Executes a runnable within a lock using only a lock name.
     *
     * @param lockName the name of the lock
     * @param runnable the task to be executed within the lock
     */
    void executeInLock(String lockName, Runnable runnable);

    /**
     * Executes a supplier within a lock and returns the result, using only a lock name.
     *
     * @param lockName the name of the lock
     * @param supplier the supplier to be executed within the lock
     * @param <T>      the type of the returned result
     * @return the result produced by the supplier
     */
    <T> T executeInLock(String lockName, Supplier<T> supplier);

    /**
     * Attempts to acquire a lock and execute a runnable within it. If the lock cannot be acquired, the runnable is not executed.
     *
     * @param lockMap  the name of the lock map
     * @param lockKey  the key used to attempt acquiring the lock
     * @param runnable the task to be executed if the lock is acquired
     */
    void executeInTryLock(String lockMap, String lockKey, Runnable runnable);

    /**
     * Attempts to acquire a lock and execute a supplier within it, returning the result. If the lock cannot be acquired, returns null.
     *
     * @param lockMap  the name of the lock map
     * @param lockKey  the key used to attempt acquiring the lock
     * @param supplier the supplier to be executed if the lock is acquired
     * @param <T>      the type of the returned result
     * @return the result produced by the supplier, or null if the lock could not be acquired
     */
    <T> T executeInTryLock(String lockMap, String lockKey, Supplier<T> supplier);

    /**
     * Attempts to acquire a lock and execute a runnable within it, using only a lock name.
     *
     * @param lockName the name of the lock
     * @param runnable the task to be executed if the lock is acquired
     */
    void executeInTryLock(String lockName, Runnable runnable);

    /**
     * Attempts to acquire a lock and execute a supplier within it, using only a lock name. Returns null if the lock cannot be acquired.
     *
     * @param lockName the name of the lock
     * @param supplier the supplier to be executed if the lock is acquired
     * @param <T>      the type of the returned result
     * @return the result produced by the supplier, or null if the lock could not be acquired
     */
    <T> T executeInTryLock(String lockName, Supplier<T> supplier);
}
