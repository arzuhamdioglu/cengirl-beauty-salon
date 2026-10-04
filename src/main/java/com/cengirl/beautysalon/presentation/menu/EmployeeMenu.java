package com.cengirl.beautysalon.presentation.menu;

import com.cengirl.beautysalon.application.ApplicationContext;
import com.cengirl.beautysalon.domain.enums.AppointmentStatus;
import com.cengirl.beautysalon.domain.model.Appointment;
import com.cengirl.beautysalon.domain.model.Payment;
import com.cengirl.beautysalon.domain.model.User;
import com.cengirl.beautysalon.presentation.util.ConsoleIO;
import com.cengirl.beautysalon.presentation.util.ConsoleUI;

import java.util.List;

public class EmployeeMenu {

    private final ApplicationContext context;
    private final User user;
    private final Runnable onLogout;

    public EmployeeMenu(ApplicationContext context, User user) {
        this(context, user, () -> {});
    }

    public EmployeeMenu(ApplicationContext context, User user, Runnable onLogout) {
        this.context = context;
        this.user = user;
        this.onLogout = onLogout;
    }

    public boolean run() {
        ConsoleUI.printEmployeeDashboard(user.getFullName());
        int choice = ConsoleIO.readInt(null);
        switch (choice) {
            case 1 -> viewCalendar();
            case 2 -> updateStatus();
            case 0 -> onLogout.run();
            default -> ConsoleUI.printWarning("Invalid option. Please try again.");
        }
        return choice != 0;
    }

    private void viewCalendar() {
        ConsoleUI.printTableHeader("ID", "Status", "Time Slot");
        List<Appointment> appointments = context.getAppointmentService()
                .getEmployeeAppointments(user.getId());
        if (appointments.isEmpty()) {
            ConsoleUI.printInfo("Your calendar is empty.");
        } else {
            appointments.forEach(a ->
                    ConsoleUI.printRow(a.getId(), a.getStatus().name(), a.getTimeSlot().toString()));
        }
    }

    private void updateStatus() {
        ConsoleUI.printSection("Update Appointment Status");
        viewCalendar();
        String appointmentId = ConsoleIO.readLine("Appointment ID");
        ConsoleUI.printInfo("1 = Completed  |  2 = No-Show  |  3 = Cancelled");
        int statusChoice = ConsoleIO.readInt("Status choice");
        AppointmentStatus target = switch (statusChoice) {
            case 1 -> AppointmentStatus.COMPLETED;
            case 2 -> AppointmentStatus.NO_SHOW;
            case 3 -> AppointmentStatus.CANCELLED;
            default -> throw new com.cengirl.beautysalon.domain.exception.ValidationException("Invalid status.");
        };
        context.getAppointmentService().updateStatusByEmployee(user.getId(), appointmentId, target);
        if (target == AppointmentStatus.COMPLETED) {
            Payment pending = context.getPaymentService()
                    .createPendingPaymentForAppointment(appointmentId);
            ConsoleUI.printInfo("Pending payment: " + pending.getId() + " · " + pending.getAmount() + " TRY");
        }
        ConsoleUI.printSuccess("Status updated to " + target.name());
    }
}
