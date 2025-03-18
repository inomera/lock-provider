# Lock Provider

![Build](https://github.com/inomera/lock-provider/workflows/Build/badge.svg)

# Version Compatability

Compatability Matrix

| Version | JDK | Hazelcast | Redisson | Zookeeper |
|---------|-----|---------|----------|-----------|
| v3.x.x  | 17  | 4,5+    | 3.14.0   | 5.3.0     |
| v2.x.x  | 8   | 3,4,5   | 3.14.0   | 5.1.0     |

## Subprojects

| Artifact                   | Version                                                                                                                                                                                                                                                  |
|----------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| lock-provider-api          | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.inomera.telco.commons/lock-provider-api/badge.svg?version=3.0.0)](https://maven-badges.herokuapp.com/maven-central/com.inomera.telco.commons/lock-provider-api)                   |
| lock-provider-reentrant    | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.inomera.telco.commons/lock-provider-reentrant/badge.svg?version=3.0.0)](https://maven-badges.herokuapp.com/maven-central/com.inomera.telco.commons/lock-provider-reentrant)       |
| lock-provider-hazelcast-4x | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.inomera.telco.commons/lock-provider-hazelcast-4x/badge.svg?version=3.0.0)](https://maven-badges.herokuapp.com/maven-central/com.inomera.telco.commons/lock-provider-hazelcast-4x) |
| lock-provider-hazelcast-5x | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.inomera.telco.commons/lock-provider-hazelcast-5x/badge.svg?version=3.0.0)](https://maven-badges.herokuapp.com/maven-central/com.inomera.telco.commons/lock-provider-hazelcast-5x) |
| lock-provider-redis        | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.inomera.telco.commons/lock-provider-redis/badge.svg?version=3.0.0)](https://maven-badges.herokuapp.com/maven-central/com.inomera.telco.commons/lock-provider-redis)               |
| lock-provider-zookeeper    | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.inomera.telco.commons/lock-provider-zookeeper/badge.svg?version=3.0.0)](https://maven-badges.herokuapp.com/maven-central/com.inomera.telco.commons/lock-provider-zookeeper)       |
|----------------------------| --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------             |
| lock-provider-api          | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.inomera.telco.commons/lock-provider-api/badge.svg?version=2.0.0)](https://maven-badges.herokuapp.com/maven-central/com.inomera.telco.commons/lock-provider-api)                   |
| lock-provider-reentrant    | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.inomera.telco.commons/lock-provider-reentrant/badge.svg?version=2.0.0)](https://maven-badges.herokuapp.com/maven-central/com.inomera.telco.commons/lock-provider-reentrant)       |
| lock-provider-hazelcast-3x | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.inomera.telco.commons/lock-provider-hazelcast-3x/badge.svg?version=2.0.0)](https://maven-badges.herokuapp.com/maven-central/com.inomera.telco.commons/lock-provider-hazelcast-3x) |
| lock-provider-hazelcast-4x | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.inomera.telco.commons/lock-provider-hazelcast-4x/badge.svg?version=2.0.0)](https://maven-badges.herokuapp.com/maven-central/com.inomera.telco.commons/lock-provider-hazelcast-4x) |
| lock-provider-hazelcast-5x | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.inomera.telco.commons/lock-provider-hazelcast-5x/badge.svg?version=2.0.0)](https://maven-badges.herokuapp.com/maven-central/com.inomera.telco.commons/lock-provider-hazelcast-5x) |
| lock-provider-redis        | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.inomera.telco.commons/lock-provider-redis/badge.svg?version=2.0.0)](https://maven-badges.herokuapp.com/maven-central/com.inomera.telco.commons/lock-provider-redis)               |
| lock-provider-zookeeper    | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.inomera.telco.commons/lock-provider-zookeeper/badge.svg?version=2.0.0)](https://maven-badges.herokuapp.com/maven-central/com.inomera.telco.commons/lock-provider-zookeeper)       |


# Usage

## With Maven
---

JDK 17 Support

```xml
<dependency>
  <groupId>com.inomera.telco.commons</groupId>
  <artifactId>lock-provider-reentrant</artifactId>
  <version>3.0.0</version>
</dependency>

<dependency>
  <groupId>com.inomera.telco.commons</groupId>
  <artifactId>lock-provider-hazelcast-3x</artifactId>
  <version>3.0.0</version>
</dependency>

<dependency>
  <groupId>com.inomera.telco.commons</groupId>
  <artifactId>lock-provider-hazelcast-4x</artifactId>
  <version>3.0.0</version>
</dependency>

<dependency>
<groupId>com.inomera.telco.commons</groupId>
<artifactId>lock-provider-hazelcast-5x</artifactId>
<version>3.0.0</version>
</dependency>

<dependency>
  <groupId>com.inomera.telco.commons</groupId>
  <artifactId>lock-provider-redis</artifactId>
  <version>3.0.0</version>
</dependency>

<dependency>
  <groupId>com.inomera.telco.commons</groupId>
  <artifactId>lock-provider-zookeeper</artifactId>
  <version>3.0.0</version>
</dependency>
```
---

JDK 8 Support

```xml
<dependency>
  <groupId>com.inomera.telco.commons</groupId>
  <artifactId>lock-provider-reentrant</artifactId>
  <version>2.0.0</version>
</dependency>

<dependency>
  <groupId>com.inomera.telco.commons</groupId>
  <artifactId>lock-provider-hazelcast-3x</artifactId>
  <version>2.0.0</version>
</dependency>

<dependency>
  <groupId>com.inomera.telco.commons</groupId>
  <artifactId>lock-provider-hazelcast-4x</artifactId>
  <version>2.0.0</version>
</dependency>

<dependency>
<groupId>com.inomera.telco.commons</groupId>
<artifactId>lock-provider-hazelcast-5x</artifactId>
<version>2.0.0</version>
</dependency>

<dependency>
  <groupId>com.inomera.telco.commons</groupId>
  <artifactId>lock-provider-redis</artifactId>
  <version>2.0.0</version>
</dependency>

<dependency>
  <groupId>com.inomera.telco.commons</groupId>
  <artifactId>lock-provider-zookeeper</artifactId>
  <version>2.0.0</version>
</dependency>
```

## With Gradle


JDK 17 Support

```groovy
implementation 'com.inomera.telco.commons:lock-provider-reentrant:3.0.0'
implementation 'com.inomera.telco.commons:lock-provider-hazelcast-3x:3.0.0'
implementation 'com.inomera.telco.commons:lock-provider-hazelcast-4x:3.0.0'
implementation 'com.inomera.telco.commons:lock-provider-hazelcast-5x:3.0.0'
implementation 'com.inomera.telco.commons:lock-provider-redis:3.0.0'
implementation 'com.inomera.telco.commons:lock-provider-zookeeper:3.0.0'
```

JDK 8 Support

```groovy
implementation 'com.inomera.telco.commons:lock-provider-reentrant:2.0.0'
implementation 'com.inomera.telco.commons:lock-provider-hazelcast-3x:2.0.0'
implementation 'com.inomera.telco.commons:lock-provider-hazelcast-4x:2.0.0'
implementation 'com.inomera.telco.commons:lock-provider-hazelcast-5x:2.0.0'
implementation 'com.inomera.telco.commons:lock-provider-redis:2.0.0'
implementation 'com.inomera.telco.commons:lock-provider-zookeeper:2.0.0'
```

## Create an Instance

### With Hazelcast 3.x.x

#### Dependency

```groovy
implementation 'com.inomera.telco.commons:lock-provider-hazelcast-3x:2.0.0'
```

#### Instance

```java
import com.inomera.telco.commons.lock.hazelcast.HazelcastLockProvider;

final LockProvider lockProvider = new HazelcastLockProvider(hazelcastInstance);
```


### With Hazelcast 4.x.x

#### Dependency

```groovy
implementation 'com.inomera.telco.commons:lock-provider-hazelcast-4x:2.0.0'
```


### With Hazelcast 5.x.x

#### Dependency

```groovy
implementation 'com.inomera.telco.commons:lock-provider-hazelcast-5x:2.0.0'
```

#### Instance

```java
import com.inomera.telco.commons.lock.hazelcast.HazelcastLockProvider;

final LockProvider lockProvider = new HazelcastLockProvider(hazelcastInstance);
```


### With Redis

#### Dependency

```groovy
implementation 'com.inomera.telco.commons:lock-provider-redis:2.0.0'
```

#### Instance

```java
import com.inomera.telco.commons.lock.redis.RedisLockProvider;

final LockProvider lockProvider = new RedisLockProvider(redissonClient);
```


### With Zookeeper

#### Dependency

```groovy
implementation 'com.inomera.telco.commons:lock-provider-zookeeper:2.0.0'
```

#### Instance

```java
import com.inomera.telco.commons.lock.zookeeper.ZookeeperLockProvider;

final LockProvider lockProvider = new ZookeeperLockProvider(curatorClient);
```


### Non-distributed Lock Provider

#### Dependency

```groovy
implementation 'com.inomera.telco.commons:lock-provider-reentrant:2.0.0'
```

#### Instance

```java
import com.inomera.telco.commons.lock.reentrant.LocalReentrantLockProvider;

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

./release.sh --release-type patch --project $projectName
# New version is 1.1.2

./release.sh --release-type minor --project $projectName
# New version is 1.2.0

./release.sh --release-type major --project $projectName
# New version is 2.0.0
```

To publish a snapshot release, use `--snapshot` flag as follows:

```sh
./release.sh --release-type latest --project $projectName --snapshot
```

Please change the version wisely.

## Redisson client, why we choose?

Redisson is more appropriate for our interface and also more mature according to the Jedis.

redisson github : https://github.com/redisson

## CuratorFramework client, why we choose?

CuratorFramework is more appropriate for our interface and also more mature according to the Zookeper Java client.

curator github : https://github.com/apache/curator
