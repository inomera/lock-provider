package com.inomera.telco.commons.lock.reentrant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Serdar Kuzucu
 */
class CompositeLockMapKeyTest {

    @Test
    @DisplayName("Equals should return true when group name and lock name are equal")
    void equals_shouldReturnTrueWhenGroupNameAndLockNameAreEqual() {
        final CompositeLockMapKey key1 = new CompositeLockMapKey("group1", "lock1");
        final CompositeLockMapKey key2 = new CompositeLockMapKey("group1", "lock1");
        assertEquals(key1, key2);
    }

    @Test
    @DisplayName("Equals should return false when group names are different")
    void equals_shouldReturnFalseWhenGroupNamesAreDifferent() {
        final CompositeLockMapKey key1 = new CompositeLockMapKey("group1", "lock1");
        final CompositeLockMapKey key2 = new CompositeLockMapKey("group2", "lock1");
        assertNotEquals(key1, key2);
    }

    @Test
    @DisplayName("Equals should return false when lock names are different")
    void equals_shouldReturnFalseWhenLockNamesAreDifferent() {
        final CompositeLockMapKey key1 = new CompositeLockMapKey("group1", "lock1");
        final CompositeLockMapKey key2 = new CompositeLockMapKey("group1", "lock2");
        assertNotEquals(key1, key2);
    }

    @Test
    @DisplayName("Equals should return false when different class is provided")
    void equals_shouldReturnFalseWhenDifferentClassIsProvided() {
        final CompositeLockMapKey key1 = new CompositeLockMapKey("group1", "lock1");
        final SimpleLockMapKey key2 = new SimpleLockMapKey("lock1");
        assertNotEquals(key1, key2);
        assertNotEquals(key2, key1);
    }

    @Test
    @DisplayName("Equals should return true when same instance is used")
    void equals_shouldReturnTrueWhenSameInstanceIsUsed() {
        final CompositeLockMapKey key1 = new CompositeLockMapKey("group1", "lock1");
        assertEquals(key1, key1);
    }

    @Test
    @DisplayName("Equals should return false when null object is used")
    @SuppressWarnings({"ConstantConditions", "SimplifiableJUnitAssertion"})
    void equals_shouldReturnFalseWhenNullObjectIsUsed() {
        final CompositeLockMapKey key1 = new CompositeLockMapKey("group1", "lock1");
        assertFalse(key1.equals(null));
    }

    @Test
    @DisplayName("hashCode should return same when group name and lock name are equal")
    void hashCode_shouldReturnTrueWhenGroupNameAndLockNameAreEqual() {
        final CompositeLockMapKey key1 = new CompositeLockMapKey("group1", "lock1");
        final CompositeLockMapKey key2 = new CompositeLockMapKey("group1", "lock1");
        assertEquals(key1.hashCode(), key2.hashCode());
    }

    @Test
    @DisplayName("hashCode should return different when group names are different")
    void hashCode_shouldReturnFalseWhenGroupNamesAreDifferent() {
        final CompositeLockMapKey key1 = new CompositeLockMapKey("group1", "lock1");
        final CompositeLockMapKey key2 = new CompositeLockMapKey("group2", "lock1");
        assertNotEquals(key1.hashCode(), key2.hashCode());
    }

    @Test
    @DisplayName("hashCode should return different when lock names are different")
    void hashCode_shouldReturnFalseWhenLockNamesAreDifferent() {
        final CompositeLockMapKey key1 = new CompositeLockMapKey("group1", "lock1");
        final CompositeLockMapKey key2 = new CompositeLockMapKey("group1", "lock2");
        assertNotEquals(key1.hashCode(), key2.hashCode());
    }

    @Test
    @DisplayName("toString should contain both group name and lock name")
    void toString_shouldContainBothGroupNameAndLockName() {
        final CompositeLockMapKey key = new CompositeLockMapKey("group1", "lock1");
        assertThat(key.toString(), containsString("group1"));
        assertThat(key.toString(), containsString("lock1"));
    }
}
