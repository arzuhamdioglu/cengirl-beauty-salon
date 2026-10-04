package com.cengirl.beautysalon.domain.observer;

import com.cengirl.beautysalon.domain.model.Appointment;

/**
 * Observer contract for appointment lifecycle events.
 */
public interface AppointmentObserver {

    void onAppointmentCreated(Appointment appointment);

    void onAppointmentCancelled(Appointment appointment);
}
