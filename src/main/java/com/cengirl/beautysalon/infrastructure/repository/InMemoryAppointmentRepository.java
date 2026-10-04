package com.cengirl.beautysalon.infrastructure.repository;

import com.cengirl.beautysalon.domain.model.Appointment;
import com.cengirl.beautysalon.domain.repository.AppointmentRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryAppointmentRepository implements AppointmentRepository {

    private final Map<String, Appointment> store = new HashMap<>();

    @Override
    public void save(Appointment appointment) {
        store.put(appointment.getId(), appointment);
    }

    @Override
    public Optional<Appointment> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Appointment> findByCustomerId(String customerId) {
        return store.values().stream()
                .filter(a -> a.getCustomerId().equals(customerId))
                .toList();
    }

    @Override
    public List<Appointment> findByEmployeeId(String employeeId) {
        return store.values().stream()
                .filter(a -> a.getEmployeeId().equals(employeeId))
                .toList();
    }

    @Override
    public List<Appointment> findAll() {
        return new ArrayList<>(store.values());
    }
}
