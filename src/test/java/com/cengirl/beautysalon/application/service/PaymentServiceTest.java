package com.cengirl.beautysalon.application.service;

import com.cengirl.beautysalon.domain.enums.PaymentStatus;
import com.cengirl.beautysalon.domain.enums.UserRole;
import com.cengirl.beautysalon.domain.factory.UserFactory;
import com.cengirl.beautysalon.domain.model.Appointment;
import com.cengirl.beautysalon.domain.model.BeautyService;
import com.cengirl.beautysalon.domain.model.Customer;
import com.cengirl.beautysalon.domain.model.Manager;
import com.cengirl.beautysalon.domain.valueobject.TimeSlot;
import com.cengirl.beautysalon.infrastructure.repository.InMemoryAppointmentRepository;
import com.cengirl.beautysalon.infrastructure.repository.InMemoryBeautyServiceRepository;
import com.cengirl.beautysalon.infrastructure.repository.InMemoryPaymentRepository;
import com.cengirl.beautysalon.infrastructure.repository.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentServiceTest {

    private PaymentService paymentService;
    private String customerId;
    private String appointmentId;

    @BeforeEach
    void setUp() {
        InMemoryUserRepository userRepository = new InMemoryUserRepository();
        InMemoryAppointmentRepository appointmentRepository = new InMemoryAppointmentRepository();
        InMemoryBeautyServiceRepository serviceRepository = new InMemoryBeautyServiceRepository();
        InMemoryPaymentRepository paymentRepository = new InMemoryPaymentRepository();

        paymentService = new PaymentService(paymentRepository, appointmentRepository,
                serviceRepository, userRepository);

        Customer customer = (Customer) UserFactory.create(UserRole.CUSTOMER, "C1",
                "cust", "pass12", "Cust", "c@t.com", "1", null, null);
        Manager manager = (Manager) UserFactory.create(UserRole.MANAGER, "M1",
                "mgr", "pass12", "Mgr", "m@t.com", "2", null, null);
        BeautyService service = new BeautyService("S1", "Cut", 60, BigDecimal.valueOf(150));
        userRepository.save(customer);
        userRepository.save(manager);
        serviceRepository.save(service);

        TimeSlot slot = new TimeSlot(
                LocalDateTime.of(2026, 5, 21, 10, 0),
                LocalDateTime.of(2026, 5, 21, 11, 0));
        Appointment appointment = new Appointment("APT-1", customer.getId(), "E-X",
                service.getId(), slot);
        appointment.markCompleted();
        appointmentRepository.save(appointment);

        customerId = customer.getId();
        appointmentId = appointment.getId();
    }

    @Test
    void shouldProcessAppointmentPayment() {
        var payment = paymentService.processAppointmentPayment(
                customerId, appointmentId, 1);
        assertEquals(PaymentStatus.COMPLETED, payment.getStatus());
    }
}
