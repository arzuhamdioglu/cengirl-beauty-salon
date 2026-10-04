package com.cengirl.beautysalon.application;

import com.cengirl.beautysalon.application.service.AppointmentService;
import com.cengirl.beautysalon.application.service.AuthService;
import com.cengirl.beautysalon.application.service.ManagerService;
import com.cengirl.beautysalon.application.service.NotificationService;
import com.cengirl.beautysalon.application.service.PaymentService;
import com.cengirl.beautysalon.application.service.UserProfileService;
import com.cengirl.beautysalon.domain.repository.AppointmentRepository;
import com.cengirl.beautysalon.domain.repository.BeautyServiceRepository;
import com.cengirl.beautysalon.domain.repository.PaymentRepository;
import com.cengirl.beautysalon.domain.repository.UserRepository;
import com.cengirl.beautysalon.infrastructure.repository.InMemoryAppointmentRepository;
import com.cengirl.beautysalon.infrastructure.repository.InMemoryBeautyServiceRepository;
import com.cengirl.beautysalon.infrastructure.repository.InMemoryPaymentRepository;
import com.cengirl.beautysalon.infrastructure.repository.InMemoryUserRepository;

/**
 * Simple composition root wiring all layers together.
 */
public final class ApplicationContext {

    private final UserRepository userRepository = new InMemoryUserRepository();
    private final AppointmentRepository appointmentRepository = new InMemoryAppointmentRepository();
    private final BeautyServiceRepository beautyServiceRepository = new InMemoryBeautyServiceRepository();
    private final PaymentRepository paymentRepository = new InMemoryPaymentRepository();

    private final NotificationService notificationService = new NotificationService();
    private final AuthService authService = new AuthService(userRepository);
    private final UserProfileService userProfileService = new UserProfileService(userRepository);
    private final AppointmentService appointmentService = new AppointmentService(
            appointmentRepository, beautyServiceRepository, userRepository, notificationService);
    private final PaymentService paymentService = new PaymentService(
            paymentRepository, appointmentRepository, beautyServiceRepository, userRepository);
    private final ManagerService managerService = new ManagerService(
            userRepository, beautyServiceRepository, authService);

    public ApplicationContext() {
        SampleDataBootstrap.seed(this);
    }

    public AuthService getAuthService() {
        return authService;
    }

    public UserProfileService getUserProfileService() {
        return userProfileService;
    }

    public AppointmentService getAppointmentService() {
        return appointmentService;
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public ManagerService getManagerService() {
        return managerService;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public BeautyServiceRepository getBeautyServiceRepository() {
        return beautyServiceRepository;
    }
}
