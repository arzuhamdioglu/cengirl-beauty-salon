package com.cengirl.beautysalon.application.service;

import com.cengirl.beautysalon.domain.enums.AppointmentStatus;
import com.cengirl.beautysalon.domain.exception.EntityNotFoundException;
import com.cengirl.beautysalon.domain.exception.ValidationException;
import com.cengirl.beautysalon.domain.model.Appointment;
import com.cengirl.beautysalon.domain.model.BeautyService;
import com.cengirl.beautysalon.domain.model.Customer;
import com.cengirl.beautysalon.domain.model.Employee;
import com.cengirl.beautysalon.domain.model.User;
import com.cengirl.beautysalon.domain.repository.AppointmentRepository;
import com.cengirl.beautysalon.domain.repository.BeautyServiceRepository;
import com.cengirl.beautysalon.domain.repository.UserRepository;
import com.cengirl.beautysalon.domain.valueobject.TimeSlot;
import com.cengirl.beautysalon.infrastructure.util.IdGenerator;

import java.time.LocalDate;
import java.util.List;

/**
 * Orchestrates appointment lifecycle and calendar consistency.
 */
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final BeautyServiceRepository beautyServiceRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              BeautyServiceRepository beautyServiceRepository,
                              UserRepository userRepository,
                              NotificationService notificationService) {
        this.appointmentRepository = appointmentRepository;
        this.beautyServiceRepository = beautyServiceRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public List<TimeSlot> getAvailableSlots(String employeeId, String serviceId, LocalDate date) {
        Employee employee = getEmployee(employeeId);
        BeautyService service = getService(serviceId);
        return employee.getCalendar().findAvailableSlots(date, service.getDurationMinutes());
    }

    public Appointment createAppointment(String customerId, String employeeId,
                                         String serviceId, TimeSlot slot) {
        Customer customer = getCustomer(customerId);
        Employee employee = getEmployee(employeeId);
        BeautyService service = getService(serviceId);

        List<TimeSlot> available = employee.getCalendar()
                .findAvailableSlots(slot.getStart().toLocalDate(), service.getDurationMinutes());
        boolean slotAvailable = available.stream().anyMatch(s -> s.equals(slot));
        if (!slotAvailable) {
            throw new ValidationException("Selected time slot is not available.");
        }

        Appointment appointment = new Appointment(
                IdGenerator.next("APT"), customer.getId(), employee.getId(), service.getId(), slot);
        employee.getCalendar().bookSlot(slot);
        customer.linkAppointment(appointment.getId());
        appointmentRepository.save(appointment);
        notificationService.onAppointmentCreated(appointment);
        return appointment;
    }

    public void cancelAppointment(String customerId, String appointmentId) {
        Appointment appointment = getAppointment(appointmentId);
        if (!appointment.getCustomerId().equals(customerId)) {
            throw new ValidationException("You can only cancel your own appointments.");
        }
        if (!appointment.isActive()) {
            throw new ValidationException("Only scheduled appointments can be cancelled.");
        }
        Employee employee = getEmployee(appointment.getEmployeeId());
        appointment.cancel();
        employee.getCalendar().releaseSlot(appointment.getTimeSlot());
        appointmentRepository.save(appointment);
        notificationService.onAppointmentCancelled(appointment);
    }

    public void updateStatusByEmployee(String employeeId, String appointmentId, AppointmentStatus target) {
        Appointment appointment = getAppointment(appointmentId);
        if (!appointment.getEmployeeId().equals(employeeId)) {
            throw new ValidationException("Appointment is not assigned to this employee.");
        }
        switch (target) {
            case COMPLETED -> appointment.markCompleted();
            case NO_SHOW -> appointment.markNoShow();
            case CANCELLED -> {
                appointment.cancel();
                getEmployee(employeeId).getCalendar().releaseSlot(appointment.getTimeSlot());
            }
            default -> throw new ValidationException("Unsupported status update: " + target);
        }
        appointmentRepository.save(appointment);
    }

    public List<Appointment> getCustomerAppointments(String customerId) {
        return appointmentRepository.findByCustomerId(customerId);
    }

    public List<Appointment> getEmployeeAppointments(String employeeId) {
        return appointmentRepository.findByEmployeeId(employeeId);
    }

    public Appointment getAppointment(String id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appointment", id));
    }

    private Customer getCustomer(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer", id));
        if (!(user instanceof Customer customer)) {
            throw new ValidationException("User is not a customer.");
        }
        return customer;
    }

    private Employee getEmployee(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee", id));
        if (!(user instanceof Employee employee)) {
            throw new ValidationException("User is not an employee.");
        }
        return employee;
    }

    private BeautyService getService(String id) {
        return beautyServiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service", id));
    }
}
