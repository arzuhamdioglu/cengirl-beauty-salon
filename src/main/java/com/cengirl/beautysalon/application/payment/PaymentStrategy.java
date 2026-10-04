package com.cengirl.beautysalon.application.payment;

import com.cengirl.beautysalon.domain.model.Payment;

/**
 * Strategy interface for processing different payment methods.
 */
public interface PaymentStrategy {

    String getName();

    boolean process(Payment payment);
}
