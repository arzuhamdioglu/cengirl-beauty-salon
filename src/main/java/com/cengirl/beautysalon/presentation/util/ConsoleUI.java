package com.cengirl.beautysalon.presentation.util;

/**
 * Console styling: banners, colored text, boxes and formatted tables (presentation only).
 */
public final class ConsoleUI {

    private static final boolean COLOR = supportsColor();

    private static final String RESET = ansi("\u001B[0m");
    private static final String BOLD = ansi("\u001B[1m");
    private static final String DIM = ansi("\u001B[2m");

    private static final String PINK = ansi("\u001B[38;5;213m");
    private static final String ROSE = ansi("\u001B[38;5;205m");
    private static final String GOLD = ansi("\u001B[38;5;220m");
    private static final String CYAN = ansi("\u001B[38;5;81m");
    private static final String GREEN = ansi("\u001B[38;5;82m");
    private static final String RED = ansi("\u001B[38;5;203m");
    private static final String PURPLE = ansi("\u001B[38;5;141m");
    private static final String WHITE = ansi("\u001B[38;5;255m");

    private static final int WIDTH = 62;

    private ConsoleUI() {
    }

    public static void printStartupBanner() {
        blankLine();
        println(center("✦ ✦ ✦ ✦ ✦ ✦ ✦ ✦ ✦ ✦ ✦ ✦ ✦ ✦ ✦"));
        println("");
        println(gradientLine("   ██████╗███████╗███╗   ██╗ ██████╗ ██╗██████╗ ██╗     "));
        println(gradientLine("  ██╔════╝██╔════╝████╗  ██║██╔════╝ ██║██╔══██╗██║     "));
        println(gradientLine("  ██║     █████╗  ██╔██╗ ██║██║  ███╗██║██████╔╝██║     "));
        println(gradientLine("  ██║     ██╔══╝  ██║╚██╗██║██║   ██║██║██╔══██╗██║     "));
        println(gradientLine("  ╚██████╗███████╗██║ ╚████║╚██████╔╝██║██║  ██║███████╗"));
        println(gradientLine("   ╚═════╝╚══════╝╚═╝  ╚═══╝ ╚═════╝ ╚═╝╚═╝  ╚═╝╚══════╝"));
        println("");
        boxLine(PINK + BOLD + "  ✿  Beauty Salon Management System  ✿  " + RESET);
        boxLine(DIM + "  Elegant appointments · Smart payments · Happy clients  " + RESET);
        println(bottomBorder());
        blankLine();
    }

    public static void printGoodbye() {
        blankLine();
        boxLine(GOLD + BOLD + "  Thank you for visiting CenGIRL! See you soon!  ✿  " + RESET);
        println(bottomBorder());
        blankLine();
    }

    public static void printMainMenu() {
        printMenuFrame("MAIN MENU", GOLD, new String[]{
                option(1, "Login", "Access your account"),
                option(2, "Register", "Create a new customer account"),
                option(0, "Exit", "Leave the salon system")
        });
    }

    public static void printCustomerDashboard(String userName) {
        printMenuFrame("CUSTOMER DASHBOARD  ·  " + userName, PINK, new String[]{
                option(1, "Profile", "View or update your details"),
                option(2, "Services", "Browse salon services"),
                option(3, "Time Slots", "See available appointment times"),
                option(4, "Book", "Create a new appointment"),
                option(5, "My Appointments", "View booking history"),
                option(6, "Cancel", "Cancel an active appointment"),
                option(7, "Payment", "Pay for completed services"),
                option(0, "Logout", "Sign out")
        });
    }

    public static void printEmployeeDashboard(String userName) {
        printMenuFrame("EMPLOYEE DASHBOARD  ·  " + userName, CYAN, new String[]{
                option(1, "My Calendar", "View your schedule"),
                option(2, "Update Status", "Complete / No-show / Cancel"),
                option(0, "Logout", "Sign out")
        });
    }

    public static void printManagerDashboard(String userName) {
        printMenuFrame("MANAGER DASHBOARD  ·  " + userName, PURPLE, new String[]{
                option(1, "Add Service", "Add a new beauty service"),
                option(2, "Update Service", "Edit service details"),
                option(3, "Delete Service", "Remove a service"),
                option(4, "List Services", "View service catalog"),
                option(5, "Add Employee", "Register new staff"),
                option(6, "List Employees", "View all employees"),
                option(7, "Working Hours", "Set employee schedule"),
                option(8, "Day Off", "Add employee day off"),
                option(9, "Pay Salary", "Process monthly payroll"),
                option(0, "Logout", "Sign out")
        });
    }

    public static void printSection(String title) {
        blankLine();
        println("  " + ROSE + BOLD + "▸ " + title + RESET);
        println("  " + DIM + repeat('─', WIDTH - 4) + RESET);
    }

    public static void printRow(String... columns) {
        println("  " + WHITE + String.join(DIM + "  │  " + RESET + WHITE, columns) + RESET);
    }

    public static void printTableHeader(String... headers) {
        printSection("Results");
        println("  " + GOLD + BOLD + String.join("  │  ", headers) + RESET);
        println("  " + DIM + repeat('─', WIDTH - 4) + RESET);
    }

    public static void printSuccess(String message) {
        blankLine();
        println("  " + GREEN + BOLD + "✔ SUCCESS" + RESET + "  " + message);
        pause();
    }

    public static void printInfo(String message) {
        println("  " + CYAN + "ℹ " + message + RESET);
    }

    public static void printWarning(String message) {
        println("  " + GOLD + "⚠ " + message + RESET);
    }

    public static void printError(String message) {
        blankLine();
        println("  " + RED + BOLD + "✖ ERROR" + RESET + "  " + message);
        pause();
    }

    public static void printWelcome(String fullName, String role) {
        blankLine();
        boxLine(GREEN + BOLD + "  Welcome back, " + fullName + "!  " + RESET + DIM + "(" + role + ")" + RESET);
        println(bottomBorder());
    }

    public static void printPrompt(String label) {
        System.out.print("  " + ROSE + "❯ " + RESET + BOLD + label + RESET + " ");
    }

    public static void printChoicePrompt() {
        System.out.print("  " + GOLD + BOLD + "❯ Your choice: " + RESET);
    }

    public static void pause() {
        ConsoleIO.pressEnterToContinue();
    }

    public static void blankLine() {
        System.out.println();
    }

    private static void printMenuFrame(String title, String accent, String[] options) {
        blankLine();
        println(topBorder(accent));
        println(side(accent + BOLD + "  ✿  " + title + RESET));
        println(side(DIM + repeat('·', WIDTH) + RESET));
        for (String opt : options) {
            println(side("  " + opt));
        }
        println(bottomBorder());
        blankLine();
    }

    private static String option(int num, String label, String hint) {
        String numStr = num == 0 ? GOLD + "[0]" + RESET : CYAN + "[" + num + "]" + RESET;
        return numStr + "  " + BOLD + label + RESET + DIM + "  —  " + hint + RESET;
    }

    private static String topBorder(String accent) {
        return "  " + accent + "╔" + repeat('═', WIDTH) + "╗" + RESET;
    }

    private static String bottomBorder() {
        return "  " + PINK + "╚" + repeat('═', WIDTH) + "╝" + RESET;
    }

    private static String boxLine(String content) {
        return side(content);
    }

    /**
     * Pads content to fixed WIDTH so the right border aligns on every row.
     */
    private static String side(String content) {
        String plain = stripAnsi(content);
        String inner = content;
        if (plain.length() > WIDTH) {
            inner = plain.substring(0, WIDTH);
        }
        int pad = Math.max(0, WIDTH - stripAnsi(inner).length());
        return "  " + PINK + "║" + RESET + inner + repeat(' ', pad) + PINK + "║" + RESET;
    }

    private static String gradientLine(String line) {
        return ROSE + line + RESET;
    }

    private static String center(String text) {
        int pad = Math.max(0, (WIDTH - text.length()) / 2);
        return "  " + GOLD + repeat(' ', pad) + text + RESET;
    }

    private static String repeat(char c, int count) {
        return String.valueOf(c).repeat(Math.max(0, count));
    }

    private static void println(String line) {
        System.out.println(line);
    }

    private static String ansi(String code) {
        return COLOR ? code : "";
    }

    private static boolean supportsColor() {
        String term = System.getenv("TERM");
        if (term != null && !term.isBlank()) {
            return true;
        }
        String eclipse = System.getenv("ECLIPSE_HOME");
        if (eclipse != null) {
            return true;
        }
        return System.console() != null || isWindowsAnsiCapable();
    }

    private static boolean isWindowsAnsiCapable() {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (!os.contains("win")) {
            return true;
        }
        String ver = System.getProperty("os.version", "10");
        try {
            return Double.parseDouble(ver) >= 10.0;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    private static String stripAnsi(String s) {
        return s.replaceAll("\u001B\\[[0-9;]*m", "");
    }
}
