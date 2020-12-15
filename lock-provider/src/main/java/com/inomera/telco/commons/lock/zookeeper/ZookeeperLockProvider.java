package com.inomera.telco.commons.lock.zookeeper;

import com.inomera.telco.commons.lock.BaseLockProvider;
import com.inomera.telco.commons.lock.LockProvider;
import com.inomera.telco.commons.lock.Locked;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

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

        InterProcessLock lock = new InterProcessMutex(curatorClient, lockMap);
        try {
            lock.acquire();
        } catch (Exception ex) {
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
        InterProcessLock lock = new InterProcessMutex(curatorClient, lockMap);

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
        } catch (Exception ex) {
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

    public static void main(String[] args) {
        String hosts = "localhost:2181";
        int baseSleepTimeMills = 1000;
        int maxRetries = 3;
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMills, maxRetries);
        CuratorFramework client = CuratorFrameworkFactory.newClient(hosts, retryPolicy);
        try {
            client.start();

            if (doStuff(client)) {
                return;
            }
        } finally {
            if (client != null) {
                client.close();
            }
            System.exit(0);
        }
    }

    private static boolean doStuff(CuratorFramework client) {
        //Zookeeper version :  3.5.8
        LockProvider lockProvider = new ZookeeperLockProvider(client);
        final Locked locked = lockProvider.lock("zoo1");
        if (locked != null) {
            System.out.println("zoo1 Locked acquired!!");
            LockProvider lockProvider11 = new ZookeeperLockProvider(client);
            final Optional<Locked> locked2Instance = lockProvider11.tryLock("zoo1");
            if (!locked2Instance.isPresent()) {
                System.out.println("zoo1 Locked cannot acquired!");
            }
            locked.unlock();
            System.out.println("zoo1 Locked is released");
            final Optional<Locked> locked3 = lockProvider.tryLock("zoo1");
            if (locked3.isPresent()) {
                System.out.println("zoo1 Locked is acquired!");
            }
            LockProvider lockProvider2 = new ZookeeperLockProvider(client, "/locks2");
            final Optional<Locked> zoo1DiffPathLocked = lockProvider2.tryLock("zoo1");
            if (zoo1DiffPathLocked.isPresent()) {
                System.out.println("zoo1 is Locked acquired with diff path!");
            }
            zoo1DiffPathLocked.get().unlock();
            locked3.get().unlock();
            System.exit(0);
            return true;
        }
        System.out.println("Cannot locked");
        return false;
    }

}
