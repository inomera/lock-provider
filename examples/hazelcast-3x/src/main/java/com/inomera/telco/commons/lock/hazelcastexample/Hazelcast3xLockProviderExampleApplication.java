package com.inomera.telco.commons.lock.hazelcastexample;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.inomera.telco.commons.lock.LockProvider;
import com.inomera.telco.commons.lock.hazelcast.HazelcastLockProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author Serdar Kuzucu
 */
@SpringBootApplication
public class Hazelcast3xLockProviderExampleApplication {
    private static final String HZ_INSTANCE_NAME = "hazelcast-3x-lock-provider-example-instance";

    public static void main(String[] args) {
        SpringApplication.run(Hazelcast3xLockProviderExampleApplication.class, args);
    }

    @Bean
    public LockProvider lockProvider() {
        return new HazelcastLockProvider(hazelcastInstance());
    }

    @Bean
    public HazelcastInstance hazelcastInstance() {
        final Config config = new Config(HZ_INSTANCE_NAME);
        config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true);
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        return Hazelcast.newHazelcastInstance(config);
    }
}
