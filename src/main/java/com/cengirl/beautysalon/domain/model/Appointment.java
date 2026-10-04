package com.cengirl.beautysalon.domain.model;

import com.cengirl.beautysalon.domain.enums.AppointmentStatus;
import com.cengirl.beautysalon.domain.exception.InvalidStateTransitionException;
import com.cengirl.beautysalon.domain.valueobject.TimeSlot;

import java.util.Objects;

/**
 * Central domain entity linking customer, employee, service, and payment lifecycle.
 */
public class Appointment {

    private final String id;
    private final String customerId;
    private final String employeeId;
    private final String serviceId;
    private final TimeSlot timeSlot;
    private AppointmentStatus status;
    private String paymentId;

    public Appointment(String id, String customerId, String employeeId, String serviceId, TimeSlot timeSlot) {
        this.id = Objects.requireNonNull(id);
        this.customerId = Objects.requireNonNull(customerId);
        this.employeeId = Objects.requireNonNull(employeeId);
        this.serviceId = Objects.requireNonNull(serviceId);
        this.timeSlot = Objects.requireNonNull(timeSlot);
        this.status = AppointmentStatus.SCHEDULED;
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void markCompleted() {
        transition(AppointmentStatus.SCHEDULED, AppointmentStatus.COMPLETED);
    }

    public void markNoShow() {
        transition(AppointmentStatus.SCHEDULED, AppointmentStatus.NO_SHOW);
    }

    public void cancel() {
        if (status != AppointmentStatus.SCHEDULED) {
            throw InvalidStateTransitionException.forAppointment(status, AppointmentStatus.CANCELLED);
        }
        status = AppointmentStatus.CANCELLED;
    }

    public void markPaid() {
        transition(AppointmentStatus.COMPLETED, AppointmentStatus.PAID);
    }

    public void linkPayment(String paymentId) {
        this.paymentId = paymentId;
    }

    public boolean isActive() {
        return status == AppointmentStatus.SCHEDULED;
    }

    private void transition(AppointmentStatus expected, AppointmentStatus next) {
        if (status != expected) {
            throw InvalidStateTransitionException.forAppointment(status, next);
        }
        status = next;
    }

    @Override
    public String toString() {
        return "Appointment{" + id + ", status=" + status + ", slot=" + timeSlot + '}';
    }
}
