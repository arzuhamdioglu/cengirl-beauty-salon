package com.cengirl.beautysalon.infrastructure.repository;

import com.cengirl.beautysalon.domain.enums.UserRole;
import com.cengirl.beautysalon.domain.model.User;
import com.cengirl.beautysalon.domain.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> byId = new HashMap<>();
    private final Map<String, String> usernameToId = new HashMap<>();

    @Override
    public void save(User user) {
        byId.put(user.getId(), user);
        usernameToId.put(user.getUsername().toLowerCase(), user.getId());
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String id = usernameToId.get(username.toLowerCase());
        return id == null ? Optional.empty() : findById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return usernameToId.containsKey(username.toLowerCase());
    }

    @Override
    public List<User> findByRole(UserRole role) {
        List<User> result = new ArrayList<>();
        for (User user : byId.values()) {
            if (user.getRole() == role) {
                result.add(user);
            }
        }
        return result;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(byId.values());
    }
}
