package com.inomera.telco.commons.lock;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Serdar Kuzucu
 */
public interface LockProvider {
    Locked lock(String lockMap, String lockKey);

    Optional<Locked> tryLock(String lockMap, String lockKey);

    Locked lock(String lockName);

    Optional<Locked> tryLock(String lockName);

    void executeInLock(String lockMap, String lockKey, Runnable runnable);

    <T> T executeInLock(String lockMap, String lockKey, Supplier<T> supplier);

    void executeInLock(String lockName, Runnable runnable);

    <T> T executeInLock(String lockName, Supplier<T> supplier);

    void executeInTryLock(String lockMap, String lockKey, Runnable runnable);

    <T> T executeInTryLock(String lockMap, String lockKey, Supplier<T> supplier);

    void executeInTryLock(String lockName, Runnable runnable);

    <T> T executeInTryLock(String lockName, Supplier<T> supplier);
}
