package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MaintenanceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Maintenance getMaintenanceSample1() {
        return new Maintenance().id(1L).description("description1");
    }

    public static Maintenance getMaintenanceSample2() {
        return new Maintenance().id(2L).description("description2");
    }

    public static Maintenance getMaintenanceRandomSampleGenerator() {
        return new Maintenance().id(longCount.incrementAndGet()).description(UUID.randomUUID().toString());
    }
}
