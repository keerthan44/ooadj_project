package com.mycompany.myapp.stratergy;

import com.mycompany.myapp.domain.Rental;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Component;

@Component("standardPricing")
public class StandardPricingStrategy implements PricingStrategy1 {

    @Override
    public BigDecimal calculatePrice(Rental rental) {
        long days = ChronoUnit.DAYS.between(rental.getStartDate(), rental.getDueDate());
        return BigDecimal.valueOf(days).multiply(new BigDecimal("20.00")); // $20 per day
    }
}
