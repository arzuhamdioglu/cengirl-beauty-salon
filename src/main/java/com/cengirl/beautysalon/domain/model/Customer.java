package com.cengirl.beautysalon.domain.model;

import com.cengirl.beautysalon.domain.enums.UserRole;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Salon customer who books appointments and makes payments.
 */
public class Customer extends User {

    private final List<String> appointmentIds = new ArrayList<>();

    public Customer(String id, String username, String password, String fullName,
                    String email, String phone) {
        super(id, UserRole.CUSTOMER, username, password, fullName, email, phone);
    }

    public List<String> getAppointmentIds() {
        return Collections.unmodifiableList(appointmentIds);
    }

    public void linkAppointment(String appointmentId) {
        if (!appointmentIds.contains(appointmentId)) {
            appointmentIds.add(appointmentId);
        }
    }
}
