package com.mycompany.myapp.web.rest.controller;

import com.mycompany.myapp.domain.Rental;
import com.mycompany.myapp.facade.RentalServiceFacade;
import com.mycompany.myapp.service.RentalService;
import com.mycompany.myapp.stratergy.PricingStrategy1;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class RentalController {

    @Autowired
    private RentalService rentalService;

    @Autowired
    private RentalServiceFacade rentalServiceFacade;

    private final Map<String, PricingStrategy1> pricingStrategies;

    @Autowired
    public RentalController(Map<String, PricingStrategy1> pricingStrategies) {
        this.pricingStrategies = pricingStrategies;
    }

    @PostMapping("/rentals/price")
    public ResponseEntity<BigDecimal> calculateRentalPrice(@RequestBody Rental rental, @RequestParam(required = false) String strategy) {
        PricingStrategy1 pricingStrategy = pricingStrategies.getOrDefault(strategy, pricingStrategies.get("standardPricing"));
        BigDecimal price = pricingStrategy.calculatePrice(rental);
        return ResponseEntity.ok(price);
    }

    @GetMapping("/rentals")
    public List<Rental> getAllRentals() {
        return rentalService.findAll();
    }

    @PostMapping("/rentals")
    public ResponseEntity<Rental> createRental(
        @RequestParam Long customerId,
        @RequestParam Long motorbikeId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate
    ) {
        Rental rental = rentalServiceFacade.createRental(customerId, motorbikeId, startDate, dueDate);
        return ResponseEntity.status(HttpStatus.CREATED).body(rental);
    }
}
