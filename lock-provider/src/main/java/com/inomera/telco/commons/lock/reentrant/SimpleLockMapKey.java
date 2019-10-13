package com.inomera.telco.commons.lock.reentrant;

import java.util.Objects;

/**
 * @author Serdar Kuzucu
 */
public class SimpleLockMapKey implements LockMapKey {
    private final String lockName;

    SimpleLockMapKey(String lockName) {
        this.lockName = lockName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SimpleLockMapKey that = (SimpleLockMapKey) o;
        return lockName.equals(that.lockName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lockName);
    }

    @Override
    public String toString() {
        return lockName;
    }
}
