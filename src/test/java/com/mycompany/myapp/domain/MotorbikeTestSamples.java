package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MotorbikeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Motorbike getMotorbikeSample1() {
        return new Motorbike().id(1L).make("make1").model("model1");
    }

    public static Motorbike getMotorbikeSample2() {
        return new Motorbike().id(2L).make("make2").model("model2");
    }

    public static Motorbike getMotorbikeRandomSampleGenerator() {
        return new Motorbike().id(longCount.incrementAndGet()).make(UUID.randomUUID().toString()).model(UUID.randomUUID().toString());
    }
}
