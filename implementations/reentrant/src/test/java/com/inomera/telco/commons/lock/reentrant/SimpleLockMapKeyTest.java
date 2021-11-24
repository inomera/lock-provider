package com.inomera.telco.commons.lock.reentrant;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Serdar Kuzucu
 */
class SimpleLockMapKeyTest {
    @Test
    @DisplayName("Equals should return true when lock names are equal")
    void equals_shouldReturnTrueWhenLockNamesAreEqual() {
        final SimpleLockMapKey key1 = new SimpleLockMapKey("lock1");
        final SimpleLockMapKey key2 = new SimpleLockMapKey("lock1");
        assertEquals(key1, key2);
    }

    @Test
    @DisplayName("Equals should return true when lock names are equal")
    void equals_shouldReturnTrueWhenSameInstanceIsPassed() {
        final SimpleLockMapKey key1 = new SimpleLockMapKey("lock1");
        assertEquals(key1, key1);
    }

    @Test
    @DisplayName("Equals should return false when lock names are different")
    void equals_shouldReturnFalseWhenLockNamesAreDifferent() {
        final SimpleLockMapKey key1 = new SimpleLockMapKey("lock1");
        final SimpleLockMapKey key2 = new SimpleLockMapKey("lock2");
        assertNotEquals(key1, key2);
    }

    @Test
    @DisplayName("Equals should return false when different class is used")
    void equals_shouldReturnFalseWhenDifferentClassIsUsed() {
        final LockMapKey key1 = new SimpleLockMapKey("lock1");
        final LockMapKey key2 = new CompositeLockMapKey("test1", "lock2");
        assertNotEquals(key1, key2);
        assertNotEquals(key2, key1);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    @DisplayName("Equals should return false when null passed")
    void equals_shouldReturnFalseWhenNullPassed() {
        final LockMapKey key1 = new SimpleLockMapKey("lock1");
        final boolean equalsResult = key1.equals(null);
        assertFalse(equalsResult);
    }

    @Test
    @DisplayName("hashCode should return same when lock names are equal")
    void hashCode_shouldReturnTrueWhenLockNamesAreEqual() {
        final SimpleLockMapKey key1 = new SimpleLockMapKey("lock1");
        final SimpleLockMapKey key2 = new SimpleLockMapKey("lock1");
        assertEquals(key1.hashCode(), key2.hashCode());
    }

    @Test
    @DisplayName("hashCode should return different when lock names are different")
    void hashCode_shouldReturnFalseWhenLockNamesAreDifferent() {
        final SimpleLockMapKey key1 = new SimpleLockMapKey("lock1");
        final SimpleLockMapKey key2 = new SimpleLockMapKey("lock2");
        assertNotEquals(key1.hashCode(), key2.hashCode());
    }

    @Test
    @DisplayName("toString should contain lock name")
    void toString_shouldContainLockName() {
        final SimpleLockMapKey key = new SimpleLockMapKey("lock1");
        MatcherAssert.assertThat(key.toString(), Matchers.containsString("lock1"));
    }
}
