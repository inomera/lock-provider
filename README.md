# Lock Provider

![Build](https://github.com/inomera/lock-provider/workflows/Build/badge.svg)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.inomera.telco.commons/lock-provider/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.inomera.telco.commons/lock-provider)

# Usage

## With Maven

```xml
<dependency>
  <groupId>com.inomera.telco.commons</groupId>
  <artifactId>lock-provider</artifactId>
  <version>1.3.0</version>
</dependency>
```

## With Gradle

```groovy
implementation 'com.inomera.telco.commons:lock-provider:1.3.0'
```

## Create an Instance

### With Hazelcast

```java
final LockProvider lockProvider = new HazelcastLockProvider(hazelcastInstance);
```

### With Redis

```java
final LockProvider lockProvider = new RedisLockProvider(redissonClient);
```

### Non-distributed Lock Provider

```java
final LockProvider lockProvider = new LocalReentrantLockProvider();
```

## Optimistic Lock

### Default Lock Map - Manual Unlock

Returns empty optional if lock is not acquired.

```java
final Optional<Locked> maybeLocked = lockProvider.tryLock("lockKey");
maybeLocked.ifPresent(locked -> {
  try {
    // Do things in lock
  } finally {
    locked.unlock();
  }
});
```

### Custom Lock Map - Manual Unlock

Returns empty optional if lock is not acquired.

```java
final Optional<Locked> maybeLocked = lockProvider.tryLock("lockMapName", "lockKey");
maybeLocked.ifPresent(locked -> {
  try {
    // Do things in lock
  } finally {
    locked.unlock();
  }
});
```

### Default Lock Map - Auto Unlock

```java
lockProvider.executeInTryLock("lockKey", () -> {
  // Do stuff in lock
});
```

### Custom Lock Map - Auto Unlock

```java
lockProvider.executeInTryLock("lockMapName", "lockKey", () -> {
  // Do stuff in lock
});
```

### Default Lock Map - Auto Unlock - Return Value

Returns null if lock is not acquired.

```java
final String result = lockProvider.executeInTryLock("lockKey", () -> {
  // Do stuff in lock
  return "result";
});
```

### Custom Lock Map - Auto Unlock - Return Value

Returns null if lock is not acquired.

```java
final String result = lockProvider.executeInTryLock("lockMapName", "lockKey", () -> {
  // Do stuff in lock
  return "result";
});
```

## Pesimistic Lock

### Default Lock Map - Manual Unlock

```java
final Locked locked = lockProvider.lock("lockKey");
try {
  // Do stuff in lock
} finally {
  locked.unlock();
}
```

### Custom Lock Map - Manual Unlock

```java
final Locked locked = lockProvider.lock("lockMapName", "lockKey");
try {
  // Do stuff in lock
} finally {
  locked.unlock();
}
```

### Default Lock Map - Auto Unlock

```java
lockProvider.executeInLock("lockKey", () -> {
  // Do stuff in lock
});
```

### Custom Lock Map - Auto Unlock

```java
lockProvider.executeInLock("lockMapName", "lockKey", () -> {
  // Do stuff in lock
});
```

### Default Lock Map - Auto Unlock - Return Value

```java
final String result = lockProvider.executeInLock("lockKey", () -> {
  // Do stuff in lock
  return "result";
});
```

### Custom Lock Map - Auto Unlock - Return Value

```java
final String result = lockProvider.executeInLock("lockMapName", "lockKey", () -> {
  // Do stuff in lock
  return "result";
});
```

## Publishing

To publish a version to maven repository, 
you should create a gradle.properties file in the root directory of this project.

The file is: `/path-to-project/gradle.properties`

This file is included in .gitignore file. 
You should not commit it since it contains sensitive information.

Add credentials for maven repository to `gradle.properties` file.

Example `gradle.properties` file:

```
mavenReleaseUrl=https://oss.sonatype.org/service/local/staging/deploy/maven2/
mavenSnapshotUrl=https://oss.sonatype.org/content/repositories/snapshots/
mavenUsername=************************
mavenPassword=************************
mavenPackageGroup=com.inomera

signing.keyId=******
signing.password=******
signing.secretKeyRingFile=******.gpg
```

Then you need to invoke `release.sh` script in the project root directory.

```sh
# When the latest VERSION is 1.1.1
./release.sh patch
# New version is 1.1.2

./release.sh minor
# New version is 1.2.0

./release.sh major
# New version is 2.0.0
```

To publish a snapshot release, use `--snapshot` flag as follows:

```sh
./release.sh latest --snapshot
```

Please change the version wisely.

## Redisson client, why we choose?

Redisson is more appropriate for our interface and also more mature according to the Jedis.

redisson github : https://github.com/redisson
