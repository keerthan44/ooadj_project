package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.MaintenanceTestSamples.*;
import static com.mycompany.myapp.domain.MotorbikeTestSamples.*;
import static com.mycompany.myapp.domain.RentalTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MotorbikeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Motorbike.class);
        Motorbike motorbike1 = getMotorbikeSample1();
        Motorbike motorbike2 = new Motorbike();
        assertThat(motorbike1).isNotEqualTo(motorbike2);

        motorbike2.setId(motorbike1.getId());
        assertThat(motorbike1).isEqualTo(motorbike2);

        motorbike2 = getMotorbikeSample2();
        assertThat(motorbike1).isNotEqualTo(motorbike2);
    }

    @Test
    void rentalTest() throws Exception {
        Motorbike motorbike = getMotorbikeRandomSampleGenerator();
        Rental rentalBack = getRentalRandomSampleGenerator();

        motorbike.addRental(rentalBack);
        assertThat(motorbike.getRentals()).containsOnly(rentalBack);
        assertThat(rentalBack.getMotorbike()).isEqualTo(motorbike);

        motorbike.removeRental(rentalBack);
        assertThat(motorbike.getRentals()).doesNotContain(rentalBack);
        assertThat(rentalBack.getMotorbike()).isNull();

        motorbike.rentals(new HashSet<>(Set.of(rentalBack)));
        assertThat(motorbike.getRentals()).containsOnly(rentalBack);
        assertThat(rentalBack.getMotorbike()).isEqualTo(motorbike);

        motorbike.setRentals(new HashSet<>());
        assertThat(motorbike.getRentals()).doesNotContain(rentalBack);
        assertThat(rentalBack.getMotorbike()).isNull();
    }

    @Test
    void maintenanceTest() throws Exception {
        Motorbike motorbike = getMotorbikeRandomSampleGenerator();
        Maintenance maintenanceBack = getMaintenanceRandomSampleGenerator();

        motorbike.addMaintenance(maintenanceBack);
        assertThat(motorbike.getMaintenances()).containsOnly(maintenanceBack);
        assertThat(maintenanceBack.getMotorbike()).isEqualTo(motorbike);

        motorbike.removeMaintenance(maintenanceBack);
        assertThat(motorbike.getMaintenances()).doesNotContain(maintenanceBack);
        assertThat(maintenanceBack.getMotorbike()).isNull();

        motorbike.maintenances(new HashSet<>(Set.of(maintenanceBack)));
        assertThat(motorbike.getMaintenances()).containsOnly(maintenanceBack);
        assertThat(maintenanceBack.getMotorbike()).isEqualTo(motorbike);

        motorbike.setMaintenances(new HashSet<>());
        assertThat(motorbike.getMaintenances()).doesNotContain(maintenanceBack);
        assertThat(maintenanceBack.getMotorbike()).isNull();
    }
}
