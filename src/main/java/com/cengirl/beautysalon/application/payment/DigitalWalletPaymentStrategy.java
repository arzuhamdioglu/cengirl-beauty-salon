package com.cengirl.beautysalon.application.payment;

import com.cengirl.beautysalon.domain.model.Payment;

public class DigitalWalletPaymentStrategy implements PaymentStrategy {

    @Override
    public String getName() {
        return "Digital Wallet";
    }

    @Override
    public boolean process(Payment payment) {
        return payment.getAmount().signum() > 0;
    }
}
