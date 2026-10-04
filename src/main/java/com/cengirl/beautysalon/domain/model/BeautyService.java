package com.cengirl.beautysalon.domain.model;

import com.cengirl.beautysalon.domain.exception.ValidationException;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Catalog item offered by the salon (name, duration, price).
 */
public class BeautyService {

    private final String id;
    private String name;
    private int durationMinutes;
    private BigDecimal price;

    public BeautyService(String id, String name, int durationMinutes, BigDecimal price) {
        this.id = Objects.requireNonNull(id);
        update(name, durationMinutes, price);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void update(String name, int durationMinutes, BigDecimal price) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Service name is required.");
        }
        if (durationMinutes <= 0) {
            throw new ValidationException("Duration must be positive.");
        }
        if (price == null || price.signum() < 0) {
            throw new ValidationException("Price cannot be negative.");
        }
        this.name = name.trim();
        this.durationMinutes = durationMinutes;
        this.price = price;
    }

    @Override
    public String toString() {
        return name + " (" + durationMinutes + " min, " + price + " TRY)";
    }
}
