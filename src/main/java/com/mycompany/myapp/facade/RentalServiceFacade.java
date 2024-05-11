package com.mycompany.myapp.facade;

import com.mycompany.myapp.domain.Customer;
import com.mycompany.myapp.domain.Motorbike;
import com.mycompany.myapp.domain.Rental;
import com.mycompany.myapp.domain.enumeration.BikeStatus;
import com.mycompany.myapp.service.CustomerService;
import com.mycompany.myapp.service.MotorbikeService;
import com.mycompany.myapp.service.RentalService;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RentalServiceFacade {

    @Autowired
    private RentalService rentalService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private MotorbikeService motorbikeService;

    public Rental createRental(Long customerId, Long motorbikeId, LocalDate startDate, LocalDate dueDate) {
        Customer customer = customerService.findOne(customerId).orElseThrow();
        Motorbike motorbike = motorbikeService.findOne(motorbikeId).orElseThrow();

        if (motorbike.getStatus() != BikeStatus.AVAILABLE) {
            throw new IllegalStateException("Motorbike is not available.");
        }

        Rental rental = new Rental();
        rental.setCustomer(customer);
        rental.setMotorbike(motorbike);
        rental.setStartDate(startDate);
        rental.setDueDate(dueDate);

        return rentalService.save(rental);
    }
}
