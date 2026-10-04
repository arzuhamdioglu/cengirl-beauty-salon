package com.cengirl.beautysalon.domain.model;

import com.cengirl.beautysalon.domain.enums.UserRole;

/**
 * Salon manager with administrative privileges.
 */
public class Manager extends User {

    public Manager(String id, String username, String password, String fullName,
                   String email, String phone) {
        super(id, UserRole.MANAGER, username, password, fullName, email, phone);
    }
}
