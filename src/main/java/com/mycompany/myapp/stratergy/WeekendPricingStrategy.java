package com.mycompany.myapp.stratergy;

import com.mycompany.myapp.domain.Rental;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Component;

@Component("weekendPricing")
public class WeekendPricingStrategy implements PricingStrategy1 {

    @Override
    public BigDecimal calculatePrice(Rental rental) {
        long days = ChronoUnit.DAYS.between(rental.getStartDate(), rental.getDueDate());
        return BigDecimal.valueOf(days).multiply(new BigDecimal("25.00")); // $25 per day for weekends
    }
}
