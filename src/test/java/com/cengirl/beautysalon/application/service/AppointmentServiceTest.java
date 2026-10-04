package com.cengirl.beautysalon.application.service;

import com.cengirl.beautysalon.domain.enums.UserRole;
import com.cengirl.beautysalon.domain.exception.ValidationException;
import com.cengirl.beautysalon.domain.factory.UserFactory;
import com.cengirl.beautysalon.domain.model.Appointment;
import com.cengirl.beautysalon.domain.model.BeautyService;
import com.cengirl.beautysalon.domain.model.Customer;
import com.cengirl.beautysalon.domain.model.Employee;
import com.cengirl.beautysalon.domain.valueobject.TimeSlot;
import com.cengirl.beautysalon.domain.valueobject.WorkSchedule;
import com.cengirl.beautysalon.infrastructure.repository.InMemoryAppointmentRepository;
import com.cengirl.beautysalon.infrastructure.repository.InMemoryBeautyServiceRepository;
import com.cengirl.beautysalon.infrastructure.repository.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AppointmentServiceTest {

    private AppointmentService appointmentService;
    private String customerId;
    private String employeeId;
    private String serviceId;

    @BeforeEach
    void setUp() {
        InMemoryUserRepository userRepository = new InMemoryUserRepository();
        InMemoryAppointmentRepository appointmentRepository = new InMemoryAppointmentRepository();
        InMemoryBeautyServiceRepository serviceRepository = new InMemoryBeautyServiceRepository();
        NotificationService notificationService = new NotificationService();

        appointmentService = new AppointmentService(
                appointmentRepository, serviceRepository, userRepository, notificationService);

        WorkSchedule schedule = new WorkSchedule();
        schedule.setWorkingHours(DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(18, 0));

        Customer customer = (Customer) UserFactory.create(UserRole.CUSTOMER, "C1",
                "cust", "pass12", "Test Customer", "c@test.com", "1", null, null);
        Employee employee = (Employee) UserFactory.create(UserRole.EMPLOYEE, "E1",
                "emp", "pass12", "Test Employee", "e@test.com", "2", schedule, BigDecimal.TEN);
        BeautyService service = new BeautyService("S1", "Cut", 60, BigDecimal.valueOf(100));

        userRepository.save(customer);
        userRepository.save(employee);
        serviceRepository.save(service);

        customerId = customer.getId();
        employeeId = employee.getId();
        serviceId = service.getId();
    }

    @Test
    void shouldCreateAndCancelAppointment() {
        LocalDate date = LocalDate.of(2026, 5, 20);
        List<TimeSlot> slots = appointmentService.getAvailableSlots(employeeId, serviceId, date);
        assertFalse(slots.isEmpty());
        Appointment created = appointmentService.createAppointment(
                customerId, employeeId, serviceId, slots.get(0));

        appointmentService.cancelAppointment(customerId, created.getId());
        assertEquals(com.cengirl.beautysalon.domain.enums.AppointmentStatus.CANCELLED,
                appointmentService.getAppointment(created.getId()).getStatus());
    }

    @Test
    void shouldPreventDoubleBooking() {
        LocalDate date = LocalDate.of(2026, 5, 20);
        TimeSlot slot = appointmentService.getAvailableSlots(employeeId, serviceId, date).get(0);
        appointmentService.createAppointment(customerId, employeeId, serviceId, slot);

        assertThrows(ValidationException.class, () ->
                appointmentService.createAppointment(customerId, employeeId, serviceId, slot));
    }
}
