package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.CustomerTestSamples.*;
import static com.mycompany.myapp.domain.MotorbikeTestSamples.*;
import static com.mycompany.myapp.domain.RentalTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RentalTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Rental.class);
        Rental rental1 = getRentalSample1();
        Rental rental2 = new Rental();
        assertThat(rental1).isNotEqualTo(rental2);

        rental2.setId(rental1.getId());
        assertThat(rental1).isEqualTo(rental2);

        rental2 = getRentalSample2();
        assertThat(rental1).isNotEqualTo(rental2);
    }

    @Test
    void customerTest() throws Exception {
        Rental rental = getRentalRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        rental.setCustomer(customerBack);
        assertThat(rental.getCustomer()).isEqualTo(customerBack);

        rental.customer(null);
        assertThat(rental.getCustomer()).isNull();
    }

    @Test
    void motorbikeTest() throws Exception {
        Rental rental = getRentalRandomSampleGenerator();
        Motorbike motorbikeBack = getMotorbikeRandomSampleGenerator();

        rental.setMotorbike(motorbikeBack);
        assertThat(rental.getMotorbike()).isEqualTo(motorbikeBack);

        rental.motorbike(null);
        assertThat(rental.getMotorbike()).isNull();
    }
}
