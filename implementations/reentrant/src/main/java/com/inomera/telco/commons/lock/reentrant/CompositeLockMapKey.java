package com.inomera.telco.commons.lock.reentrant;

import java.util.Objects;

/**
 * @author Serdar Kuzucu
 */
public class CompositeLockMapKey implements LockMapKey {
    private final String lockGroup;
    private final String lockName;

    CompositeLockMapKey(String lockGroup, String lockName) {
        this.lockGroup = lockGroup;
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

        final CompositeLockMapKey that = (CompositeLockMapKey) o;
        return lockGroup.equals(that.lockGroup) && lockName.equals(that.lockName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lockGroup, lockName);
    }

    @Override
    public String toString() {
        return lockGroup + ">" + lockName;
    }
}
