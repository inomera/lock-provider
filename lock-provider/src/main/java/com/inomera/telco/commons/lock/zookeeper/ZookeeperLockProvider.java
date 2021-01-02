package com.inomera.telco.commons.lock.zookeeper;

import com.inomera.telco.commons.lock.BaseLockProvider;
import com.inomera.telco.commons.lock.LockProvider;
import com.inomera.telco.commons.lock.Locked;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import java.io.File;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Use to lock in a distributed environment
 *
 * @author Turgay Can
 */
public class ZookeeperLockProvider extends BaseLockProvider implements LockProvider {
    private static final String DEFAULT_LOCK_PATH = "/locks";
    private static final int DEFAULT_LOCK_WAIT_TIME_MS = 500;

    private final CuratorFramework curatorClient;
    private final String defaultLockPath;

    /**
     * Creates a new CuratorFramework with given CuratorFramework and
     * "ZookeeperLockProvider_DefaultLockPath" as default lock path.
     *
     * @param curatorClient Zookeeper to get lock maps
     */
    public ZookeeperLockProvider(CuratorFramework curatorClient) {
        this.curatorClient = Objects.requireNonNull(curatorClient, "CuratorFramework is required!");
        this.defaultLockPath = DEFAULT_LOCK_PATH;
    }

    /**
     * Creates a new ZookeeperLockProvider with given Zookeeper and
     * given default lock map name.
     *
     * @param curatorClient   CuratorFramework to get lock client
     * @param defaultLockPath name of the default lock path
     */
    public ZookeeperLockProvider(CuratorFramework curatorClient, String defaultLockPath) {
        this.curatorClient = curatorClient;
        this.defaultLockPath = Objects.requireNonNull(defaultLockPath, "Default lock path name cannot be null");
    }

    @Override
    public Locked lock(String lockMap, String lockKey) {
        InterProcessLock lock = getMutex(lockMap, lockKey);
        try {
            lock.acquire();
        } finally {
            return () -> {
                try {
                    lock.release();
                } catch (Exception e) {
                }
            };
        }
    }

    @Override
    public Optional<Locked> tryLock(String lockMap, String lockKey) {
        InterProcessLock lock = getMutex(lockMap, lockKey);
        try {
            if (lock.acquire(DEFAULT_LOCK_WAIT_TIME_MS, TimeUnit.MILLISECONDS)) {
                return Optional.of(() -> {
                    try {
                        lock.release();
                    } catch (Exception e) {
                    }
                });
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Locked lock(String lockName) {
        return lock(defaultLockPath, lockName);
    }

    @Override
    public Optional<Locked> tryLock(String lockName) {
        return tryLock(defaultLockPath, lockName);
    }

    private InterProcessLock getMutex(String lockMap, String lockKey) {
        return new InterProcessMutex(curatorClient, lockMap.concat(File.separator).concat(lockKey));
    }

}
