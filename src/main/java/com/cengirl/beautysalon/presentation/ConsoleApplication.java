package com.cengirl.beautysalon.presentation;

import com.cengirl.beautysalon.application.ApplicationContext;
import com.cengirl.beautysalon.domain.exception.DomainException;
import com.cengirl.beautysalon.presentation.menu.MainMenu;
import com.cengirl.beautysalon.presentation.util.ConsoleUI;

/**
 * Entry point for the console-based CenGIRL system.
 */
public final class ConsoleApplication {

    public static void main(String[] args) {
        ApplicationContext context = new ApplicationContext();
        MainMenu menu = new MainMenu(context);
        ConsoleUI.printStartupBanner();
        while (true) {
            try {
                if (!menu.show()) {
                    ConsoleUI.printGoodbye();
                    break;
                }
            } catch (DomainException ex) {
                ConsoleUI.printError(ex.getMessage());
            } catch (Exception ex) {
                ConsoleUI.printError("Unexpected error: " + ex.getMessage());
            }
        }
    }
}
