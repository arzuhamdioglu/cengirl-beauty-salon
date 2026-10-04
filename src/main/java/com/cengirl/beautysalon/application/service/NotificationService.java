package com.cengirl.beautysalon.application.service;

import com.cengirl.beautysalon.domain.model.Appointment;
import com.cengirl.beautysalon.domain.observer.AppointmentObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer-based notification dispatcher for appointment events.
 */
public class NotificationService implements AppointmentObserver {

    private final List<String> notificationLog = new ArrayList<>();

    @Override
    public void onAppointmentCreated(Appointment appointment) {
        String message = "[NOTIFICATION] Booking confirmed for appointment " + appointment.getId()
                + " | customer=" + appointment.getCustomerId()
                + ", employee=" + appointment.getEmployeeId()
                + ", slot=" + appointment.getTimeSlot();
        notificationLog.add(message);
        System.out.println(message);
    }

    @Override
    public void onAppointmentCancelled(Appointment appointment) {
        String message = "[NOTIFICATION] Appointment " + appointment.getId()
                + " cancelled. Slot released: " + appointment.getTimeSlot();
        notificationLog.add(message);
        System.out.println(message);
    }

    public List<String> getNotificationLog() {
        return List.copyOf(notificationLog);
    }
}
