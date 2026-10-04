package com.cengirl.beautysalon.domain.repository;

import com.cengirl.beautysalon.domain.model.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {

    void save(Payment payment);

    Optional<Payment> findById(String id);

    List<Payment> findByReferenceId(String referenceId);

    List<Payment> findAll();
}
