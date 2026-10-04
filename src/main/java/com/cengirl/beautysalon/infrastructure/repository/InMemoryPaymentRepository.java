package com.cengirl.beautysalon.infrastructure.repository;

import com.cengirl.beautysalon.domain.model.Payment;
import com.cengirl.beautysalon.domain.repository.PaymentRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryPaymentRepository implements PaymentRepository {

    private final Map<String, Payment> store = new HashMap<>();

    @Override
    public void save(Payment payment) {
        store.put(payment.getId(), payment);
    }

    @Override
    public Optional<Payment> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Payment> findByReferenceId(String referenceId) {
        return store.values().stream()
                .filter(p -> referenceId.equals(p.getReferenceId()))
                .toList();
    }

    @Override
    public List<Payment> findAll() {
        return new ArrayList<>(store.values());
    }
}
