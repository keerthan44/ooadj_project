package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.CustomerTestSamples.*;
import static com.mycompany.myapp.domain.RentalTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CustomerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Customer.class);
        Customer customer1 = getCustomerSample1();
        Customer customer2 = new Customer();
        assertThat(customer1).isNotEqualTo(customer2);

        customer2.setId(customer1.getId());
        assertThat(customer1).isEqualTo(customer2);

        customer2 = getCustomerSample2();
        assertThat(customer1).isNotEqualTo(customer2);
    }

    @Test
    void rentalTest() throws Exception {
        Customer customer = getCustomerRandomSampleGenerator();
        Rental rentalBack = getRentalRandomSampleGenerator();

        customer.addRental(rentalBack);
        assertThat(customer.getRentals()).containsOnly(rentalBack);
        assertThat(rentalBack.getCustomer()).isEqualTo(customer);

        customer.removeRental(rentalBack);
        assertThat(customer.getRentals()).doesNotContain(rentalBack);
        assertThat(rentalBack.getCustomer()).isNull();

        customer.rentals(new HashSet<>(Set.of(rentalBack)));
        assertThat(customer.getRentals()).containsOnly(rentalBack);
        assertThat(rentalBack.getCustomer()).isEqualTo(customer);

        customer.setRentals(new HashSet<>());
        assertThat(customer.getRentals()).doesNotContain(rentalBack);
        assertThat(rentalBack.getCustomer()).isNull();
    }
}
