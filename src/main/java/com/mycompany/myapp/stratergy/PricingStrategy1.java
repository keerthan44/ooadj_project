package com.mycompany.myapp.stratergy;

import com.mycompany.myapp.domain.Rental;
import java.math.BigDecimal;

public interface PricingStrategy1 {
    BigDecimal calculatePrice(Rental rental);
}
