package com.cengirl.beautysalon.domain.exception;

import com.cengirl.beautysalon.domain.enums.AppointmentStatus;
import com.cengirl.beautysalon.domain.enums.PaymentStatus;

public class InvalidStateTransitionException extends DomainException {

    public InvalidStateTransitionException(String entity, String from, String to) {
        super("Invalid " + entity + " transition: " + from + " -> " + to);
    }

    public static InvalidStateTransitionException forAppointment(AppointmentStatus from, AppointmentStatus to) {
        return new InvalidStateTransitionException("appointment", from.name(), to.name());
    }

    public static InvalidStateTransitionException forPayment(PaymentStatus from, PaymentStatus to) {
        return new InvalidStateTransitionException("payment", from.name(), to.name());
    }
}
