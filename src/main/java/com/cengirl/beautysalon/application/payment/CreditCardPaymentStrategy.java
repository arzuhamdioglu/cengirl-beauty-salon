package com.cengirl.beautysalon.application.payment;

import com.cengirl.beautysalon.domain.model.Payment;

public class CreditCardPaymentStrategy implements PaymentStrategy {

    @Override
    public String getName() {
        return "Credit Card";
    }

    @Override
    public boolean process(Payment payment) {
        return payment.getAmount().compareTo(java.math.BigDecimal.valueOf(50000)) <= 0;
    }
}
