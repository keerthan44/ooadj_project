package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.MaintenanceTestSamples.*;
import static com.mycompany.myapp.domain.MotorbikeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MaintenanceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Maintenance.class);
        Maintenance maintenance1 = getMaintenanceSample1();
        Maintenance maintenance2 = new Maintenance();
        assertThat(maintenance1).isNotEqualTo(maintenance2);

        maintenance2.setId(maintenance1.getId());
        assertThat(maintenance1).isEqualTo(maintenance2);

        maintenance2 = getMaintenanceSample2();
        assertThat(maintenance1).isNotEqualTo(maintenance2);
    }

    @Test
    void motorbikeTest() throws Exception {
        Maintenance maintenance = getMaintenanceRandomSampleGenerator();
        Motorbike motorbikeBack = getMotorbikeRandomSampleGenerator();

        maintenance.setMotorbike(motorbikeBack);
        assertThat(maintenance.getMotorbike()).isEqualTo(motorbikeBack);

        maintenance.motorbike(null);
        assertThat(maintenance.getMotorbike()).isNull();
    }
}
