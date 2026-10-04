package com.cengirl.beautysalon.presentation.menu;

import com.cengirl.beautysalon.application.ApplicationContext;
import com.cengirl.beautysalon.application.payment.PaymentStrategy;
import com.cengirl.beautysalon.domain.enums.AppointmentStatus;
import com.cengirl.beautysalon.domain.enums.UserRole;
import com.cengirl.beautysalon.domain.model.Appointment;
import com.cengirl.beautysalon.domain.model.Payment;
import com.cengirl.beautysalon.domain.model.User;
import com.cengirl.beautysalon.domain.valueobject.TimeSlot;
import com.cengirl.beautysalon.presentation.util.ConsoleIO;
import com.cengirl.beautysalon.presentation.util.ConsoleUI;

import java.time.LocalDate;
import java.util.List;

public class CustomerMenu {

    private final ApplicationContext context;
    private final User user;
    private final Runnable onLogout;

    public CustomerMenu(ApplicationContext context, User user) {
        this(context, user, () -> {});
    }

    public CustomerMenu(ApplicationContext context, User user, Runnable onLogout) {
        this.context = context;
        this.user = user;
        this.onLogout = onLogout;
    }

    public boolean run() {
        ConsoleUI.printCustomerDashboard(user.getFullName());
        int choice = ConsoleIO.readInt(null);
        switch (choice) {
            case 1 -> updateProfile();
            case 2 -> listServices();
            case 3 -> viewSlots();
            case 4 -> createAppointment();
            case 5 -> viewAppointments();
            case 6 -> cancelAppointment();
            case 7 -> makePayment();
            case 0 -> onLogout.run();
            default -> ConsoleUI.printWarning("Invalid option. Please try again.");
        }
        return choice != 0;
    }

    private void updateProfile() {
        ConsoleUI.printSection("Update Profile");
        String fullName = ConsoleIO.readLine("Full name (blank = keep)");
        String email = ConsoleIO.readEmail(false);
        String phone = ConsoleIO.readPhone(false);
        context.getUserProfileService().updateProfile(user.getId(), fullName, email, phone);
        ConsoleUI.printSuccess("Profile updated successfully.");
    }

    private void listServices() {
        ConsoleUI.printTableHeader("ID", "Service", "Details");
        context.getManagerService().listServices()
                .forEach(s -> ConsoleUI.printRow(s.getId(), s.getName(),
                        s.getDurationMinutes() + " min · " + s.getPrice() + " TRY"));
    }

    private void viewSlots() {
        ConsoleUI.printSection("Available Time Slots");
        listEmployees();
        String employeeId = ConsoleIO.readLine("Employee ID");
        listServices();
        String serviceId = ConsoleIO.readLine("Service ID");
        LocalDate date = ConsoleIO.readBookableDateSelection();
        List<TimeSlot> slots = context.getAppointmentService()
                .getAvailableSlots(employeeId, serviceId, date);
        if (slots.isEmpty()) {
            ConsoleUI.printWarning("No available slots for " + date + ".");
        } else {
            ConsoleUI.printInfo("Slots for " + date + ":");
            ConsoleUI.printTableHeader("#", "Time Slot");
            for (int i = 0; i < slots.size(); i++) {
                ConsoleUI.printRow(String.valueOf(i), slots.get(i).toString());
            }
        }
    }

    private void createAppointment() {
        ConsoleUI.printSection("Book Appointment");
        listEmployees();
        String employeeId = ConsoleIO.readLine("Employee ID");
        listServices();
        String serviceId = ConsoleIO.readLine("Service ID");
        LocalDate date = ConsoleIO.readBookableDateSelection();
        List<TimeSlot> slots = context.getAppointmentService()
                .getAvailableSlots(employeeId, serviceId, date);
        if (slots.isEmpty()) {
            ConsoleUI.printWarning("No available slots for this date.");
            return;
        }
        ConsoleUI.printTableHeader("#", "Time Slot");
        for (int i = 0; i < slots.size(); i++) {
            ConsoleUI.printRow(String.valueOf(i), slots.get(i).toString());
        }
        int index = ConsoleIO.readInt("Select slot index");
        if (index < 0 || index >= slots.size()) {
            ConsoleUI.printWarning("Invalid slot index.");
            return;
        }
        Appointment apt = context.getAppointmentService()
                .createAppointment(user.getId(), employeeId, serviceId, slots.get(index));
        ConsoleUI.printSuccess("Appointment created! ID: " + apt.getId());
    }

    private void viewAppointments() {
        ConsoleUI.printTableHeader("ID", "Status", "Time Slot");
        List<Appointment> list = context.getAppointmentService().getCustomerAppointments(user.getId());
        if (list.isEmpty()) {
            ConsoleUI.printInfo("You have no appointments yet.");
        } else {
            list.forEach(a -> ConsoleUI.printRow(a.getId(), a.getStatus().name(), a.getTimeSlot().toString()));
        }
    }

    private void cancelAppointment() {
        ConsoleUI.printSection("Cancel Appointment");
        viewAppointments();
        String id = ConsoleIO.readLine("Appointment ID to cancel");
        String confirm = ConsoleIO.readLine("Confirm cancellation (y/n)");
        if ("y".equalsIgnoreCase(confirm)) {
            context.getAppointmentService().cancelAppointment(user.getId(), id);
            ConsoleUI.printSuccess("Appointment cancelled.");
        } else {
            ConsoleUI.printInfo("Cancellation aborted.");
        }
    }

    private void makePayment() {
        ConsoleUI.printSection("Make Payment");
        List<Appointment> completed = context.getAppointmentService()
                .getCustomerAppointments(user.getId()).stream()
                .filter(a -> a.getStatus() == AppointmentStatus.COMPLETED)
                .toList();
        if (completed.isEmpty()) {
            ConsoleUI.printWarning("No completed appointments awaiting payment.");
            ConsoleUI.printInfo("An employee must mark your appointment as Completed first "
                    + "(Employee login → Update Status → Completed), then you can pay here.");
            return;
        }
        ConsoleUI.printTableHeader("ID", "Time Slot");
        completed.forEach(a -> ConsoleUI.printRow(a.getId(), a.getTimeSlot().toString()));
        String appointmentId = ConsoleIO.readLine("Appointment ID");
        printPaymentMethods();
        int method = ConsoleIO.readInt("Payment method");
        Payment payment = context.getPaymentService()
                .processAppointmentPayment(user.getId(), appointmentId, method);
        ConsoleUI.printSuccess("Payment " + payment.getStatus()
                + " via " + payment.getPaymentMethod());
    }

    private void listEmployees() {
        ConsoleUI.printTableHeader("ID", "Stylist");
        context.getUserRepository().findByRole(UserRole.EMPLOYEE)
                .forEach(e -> ConsoleUI.printRow(e.getId(), e.getFullName()));
    }

    private void printPaymentMethods() {
        ConsoleUI.printSection("Payment Methods");
        List<PaymentStrategy> strategies = context.getPaymentService().getAvailableStrategies();
        for (int i = 0; i < strategies.size(); i++) {
            ConsoleUI.printRow(String.valueOf(i + 1), strategies.get(i).getName());
        }
    }
}
