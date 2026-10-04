package com.cengirl.beautysalon.domain.model;

import com.cengirl.beautysalon.domain.enums.AppointmentStatus;
import com.cengirl.beautysalon.domain.exception.InvalidStateTransitionException;
import com.cengirl.beautysalon.domain.valueobject.TimeSlot;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AppointmentTest {

    @Test
    void shouldFollowValidLifecycle() {
        TimeSlot slot = new TimeSlot(
                LocalDateTime.of(2026, 5, 20, 10, 0),
                LocalDateTime.of(2026, 5, 20, 11, 0));
        Appointment appointment = new Appointment("APT-1", "C1", "E1", "S1", slot);

        assertEquals(AppointmentStatus.SCHEDULED, appointment.getStatus());
        appointment.markCompleted();
        assertEquals(AppointmentStatus.COMPLETED, appointment.getStatus());
        appointment.markPaid();
        assertEquals(AppointmentStatus.PAID, appointment.getStatus());
    }

    @Test
    void shouldRejectInvalidTransition() {
        TimeSlot slot = new TimeSlot(
                LocalDateTime.of(2026, 5, 20, 10, 0),
                LocalDateTime.of(2026, 5, 20, 11, 0));
        Appointment appointment = new Appointment("APT-2", "C1", "E1", "S1", slot);

        assertThrows(InvalidStateTransitionException.class, appointment::markPaid);
    }
}
