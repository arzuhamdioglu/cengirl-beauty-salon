package com.cengirl.beautysalon.presentation.menu;

import com.cengirl.beautysalon.application.ApplicationContext;
import com.cengirl.beautysalon.domain.model.User;
import com.cengirl.beautysalon.presentation.util.ConsoleIO;
import com.cengirl.beautysalon.presentation.util.ConsoleUI;

/**
 * Top-level menu: registration, login, and role-based dashboards.
 */
public class MainMenu {

    private final ApplicationContext context;
    private User currentUser;

    public MainMenu(ApplicationContext context) {
        this.context = context;
    }

    /**
     * @return false only when the user chooses Exit on the guest (login) menu
     */
    public boolean show() {
        if (currentUser == null) {
            return showGuestMenu();
        }
        showRoleMenu();
        return true;
    }

    private boolean showGuestMenu() {
        ConsoleUI.printMainMenu();
        int choice = ConsoleIO.readInt(null);
        return switch (choice) {
            case 1 -> {
                login();
                yield true;
            }
            case 2 -> {
                registerCustomer();
                yield true;
            }
            case 0 -> false;
            default -> {
                ConsoleUI.printWarning("Invalid option. Please try again.");
                yield true;
            }
        };
    }

    private void showRoleMenu() {
        Runnable logout = () -> {
            currentUser = null;
            ConsoleUI.printInfo("You have been logged out. Returning to main menu...");
        };
        switch (currentUser.getRole()) {
            case CUSTOMER -> new CustomerMenu(context, currentUser, logout).run();
            case EMPLOYEE -> new EmployeeMenu(context, currentUser, logout).run();
            case MANAGER -> new ManagerMenu(context, currentUser, logout).run();
        }
    }

    private void login() {
        ConsoleUI.printSection("Login");
        String username = ConsoleIO.readLine("Username");
        String password = ConsoleIO.readLine("Password");
        currentUser = context.getAuthService().login(username, password);
        ConsoleUI.printWelcome(currentUser.getFullName(), currentUser.getRole().name());
    }

    private void registerCustomer() {
        ConsoleUI.printSection("New Customer Registration");
        String username = ConsoleIO.readLine("Username");
        String password = ConsoleIO.readLine("Password");
        String fullName = ConsoleIO.readLine("Full name");
        String email = ConsoleIO.readEmail(true);
        String phone = ConsoleIO.readPhone(true);
        User user = context.getAuthService().registerCustomer(username, password, fullName, email, phone);
        ConsoleUI.printSuccess("Registration complete! Your user ID: " + user.getId());
    }

    public void logout() {
        currentUser = null;
        ConsoleUI.printInfo("Logged out.");
    }
}
