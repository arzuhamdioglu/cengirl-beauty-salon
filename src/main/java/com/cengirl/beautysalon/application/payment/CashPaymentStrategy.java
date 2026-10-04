package com.cengirl.beautysalon.application.payment;

import com.cengirl.beautysalon.domain.model.Payment;

public class CashPaymentStrategy implements PaymentStrategy {

    @Override
    public String getName() {
        return "Cash";
    }

    @Override
    public boolean process(Payment payment) {
        return true;
    }
}
