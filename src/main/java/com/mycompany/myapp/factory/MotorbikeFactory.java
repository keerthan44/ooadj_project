package com.mycompany.myapp.factory;

import com.mycompany.myapp.domain.Motorbike;
import com.mycompany.myapp.domain.enumeration.BikeStatus;

public class MotorbikeFactory {

    public static Motorbike createMotorbike(String make, String model) {
        Motorbike motorbike = new Motorbike();
        motorbike.setMake(make);
        motorbike.setModel(model);
        motorbike.setStatus(BikeStatus.AVAILABLE);
        return motorbike;
    }
}
