package com.cengirl.beautysalon.domain.factory;

import com.cengirl.beautysalon.domain.enums.UserRole;
import com.cengirl.beautysalon.domain.model.Customer;
import com.cengirl.beautysalon.domain.model.Employee;
import com.cengirl.beautysalon.domain.model.Manager;
import com.cengirl.beautysalon.domain.model.User;
import com.cengirl.beautysalon.domain.valueobject.WorkSchedule;

import java.math.BigDecimal;

/**
 * Factory Method: creates concrete user types without exposing construction details to services.
 */
public final class UserFactory {

    private UserFactory() {
    }

    public static User create(UserRole role, String id, String username, String password,
                              String fullName, String email, String phone,
                              WorkSchedule schedule, BigDecimal salary) {
        return switch (role) {
            case CUSTOMER -> new Customer(id, username, password, fullName, email, phone);
            case EMPLOYEE -> new Employee(id, username, password, fullName, email, phone, schedule, salary);
            case MANAGER -> new Manager(id, username, password, fullName, email, phone);
        };
    }
}
