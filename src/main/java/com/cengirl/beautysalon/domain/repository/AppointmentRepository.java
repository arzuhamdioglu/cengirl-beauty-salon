package com.cengirl.beautysalon.domain.repository;

import com.cengirl.beautysalon.domain.model.Appointment;

import java.util.List;
import java.util.Optional;

public interface AppointmentRepository {

    void save(Appointment appointment);

    Optional<Appointment> findById(String id);

    List<Appointment> findByCustomerId(String customerId);

    List<Appointment> findByEmployeeId(String employeeId);

    List<Appointment> findAll();
}
