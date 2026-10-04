package com.cengirl.beautysalon.presentation.menu;

import com.cengirl.beautysalon.application.ApplicationContext;
import com.cengirl.beautysalon.application.payment.PaymentStrategy;
import com.cengirl.beautysalon.domain.model.BeautyService;
import com.cengirl.beautysalon.domain.model.Employee;
import com.cengirl.beautysalon.domain.model.Payment;
import com.cengirl.beautysalon.domain.model.User;
import com.cengirl.beautysalon.domain.valueobject.WorkSchedule;
import com.cengirl.beautysalon.infrastructure.util.DateTimeUtil;
import com.cengirl.beautysalon.presentation.util.ConsoleIO;
import com.cengirl.beautysalon.presentation.util.ConsoleUI;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ManagerMenu {

    private final ApplicationContext context;
    private final User user;
    private final Runnable onLogout;

    public ManagerMenu(ApplicationContext context, User user) {
        this(context, user, () -> {});
    }

    public ManagerMenu(ApplicationContext context, User user, Runnable onLogout) {
        this.context = context;
        this.user = user;
        this.onLogout = onLogout;
    }

    public boolean run() {
        ConsoleUI.printManagerDashboard(user.getFullName());
        int choice = ConsoleIO.readInt(null);
        switch (choice) {
            case 1 -> addService();
            case 2 -> updateService();
            case 3 -> deleteService();
            case 4 -> listServices();
            case 5 -> addEmployee();
            case 6 -> listEmployees();
            case 7 -> setWorkingHours();
            case 8 -> addDayOff();
            case 9 -> paySalary();
            case 0 -> onLogout.run();
            default -> ConsoleUI.printWarning("Invalid option. Please try again.");
        }
        return choice != 0;
    }

    private void addService() {
        ConsoleUI.printSection("Add Service");
        String name = ConsoleIO.readLine("Service name");
        int duration = ConsoleIO.readInt("Duration (minutes)");
        BigDecimal price = ConsoleIO.readBigDecimal("Price (TRY)");
        BeautyService service = context.getManagerService()
                .addService(user.getId(), name, duration, price);
        ConsoleUI.printSuccess("Service added: " + service.getId());
    }

    private void updateService() {
        ConsoleUI.printSection("Update Service");
        listServices();
        String id = ConsoleIO.readLine("Service ID");
        String name = ConsoleIO.readLine("New name");
        int duration = ConsoleIO.readInt("New duration (minutes)");
        BigDecimal price = ConsoleIO.readBigDecimal("New price (TRY)");
        context.getManagerService().updateService(user.getId(), id, name, duration, price);
        ConsoleUI.printSuccess("Service updated.");
    }

    private void deleteService() {
        ConsoleUI.printSection("Delete Service");
        listServices();
        String id = ConsoleIO.readLine("Service ID to delete");
        context.getManagerService().deleteService(user.getId(), id);
        ConsoleUI.printSuccess("Service deleted.");
    }

    private void listServices() {
        ConsoleUI.printTableHeader("ID", "Service", "Details");
        context.getManagerService().listServices()
                .forEach(s -> ConsoleUI.printRow(s.getId(), s.getName(),
                        s.getDurationMinutes() + " min · " + s.getPrice() + " TRY"));
    }

    private void addEmployee() {
        ConsoleUI.printSection("Add Employee");
        String username = ConsoleIO.readLine("Username");
        String password = ConsoleIO.readLine("Password");
        String fullName = ConsoleIO.readLine("Full name");
        String email = ConsoleIO.readEmail(true);
        String phone = ConsoleIO.readPhone(true);
        BigDecimal salary = ConsoleIO.readBigDecimal("Monthly salary (TRY)");
        WorkSchedule schedule = new WorkSchedule();
        for (DayOfWeek day : DayOfWeek.values()) {
            if (day != DayOfWeek.SUNDAY) {
                schedule.setWorkingHours(day, LocalTime.of(9, 0), LocalTime.of(18, 0));
            }
        }
        Employee employee = context.getManagerService().addEmployee(
                user.getId(), username, password, fullName, email, phone, salary, schedule);
        ConsoleUI.printSuccess("Employee added: " + employee.getId());
    }

    private void listEmployees() {
        ConsoleUI.printTableHeader("ID", "Name", "Salary");
        context.getManagerService().listEmployees()
                .forEach(e -> ConsoleUI.printRow(e.getId(), e.getFullName(),
                        e.getMonthlySalary() + " TRY"));
    }

    private void setWorkingHours() {
        ConsoleUI.printSection("Set Working Hours");
        listEmployees();
        String employeeId = ConsoleIO.readLine("Employee ID");
        int dayNum = ConsoleIO.readInt("Day (1=Mon ... 7=Sun)");
        LocalTime open = LocalTime.parse(ConsoleIO.readLine("Open (HH:mm)"));
        LocalTime close = LocalTime.parse(ConsoleIO.readLine("Close (HH:mm)"));
        context.getManagerService().setWorkingHours(
                user.getId(), employeeId, DayOfWeek.of(dayNum), open, close);
        ConsoleUI.printSuccess("Working hours updated.");
    }

    private void addDayOff() {
        ConsoleUI.printSection("Add Day Off");
        listEmployees();
        String employeeId = ConsoleIO.readLine("Employee ID");
        LocalDate date = DateTimeUtil.parseDate(ConsoleIO.readLine("Day off (yyyy-MM-dd)"));
        context.getManagerService().addDayOff(user.getId(), employeeId, date);
        ConsoleUI.printSuccess("Day off recorded.");
    }

    private void paySalary() {
        ConsoleUI.printSection("Pay Salary");
        listEmployees();
        String employeeId = ConsoleIO.readLine("Employee ID");
        printPaymentMethods();
        int method = ConsoleIO.readInt("Payment method");
        Payment payment = context.getPaymentService().paySalary(user.getId(), employeeId, method);
        ConsoleUI.printSuccess("Salary payment: " + payment.getStatus());
    }

    private void printPaymentMethods() {
        ConsoleUI.printSection("Payment Methods");
        List<PaymentStrategy> strategies = context.getPaymentService().getAvailableStrategies();
        for (int i = 0; i < strategies.size(); i++) {
            ConsoleUI.printRow(String.valueOf(i + 1), strategies.get(i).getName());
        }
    }
}
