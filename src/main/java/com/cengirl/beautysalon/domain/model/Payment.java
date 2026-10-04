package com.cengirl.beautysalon.domain.model;

import com.cengirl.beautysalon.domain.enums.PaymentStatus;
import com.cengirl.beautysalon.domain.enums.PaymentType;
import com.cengirl.beautysalon.domain.exception.InvalidStateTransitionException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Financial transaction for appointments or employee salaries.
 */
public class Payment {

    private final String id;
    private final PaymentType type;
    private final BigDecimal amount;
    private final String referenceId;
    private final LocalDateTime createdAt;
    private PaymentStatus status;
    private String paymentMethod;

    public Payment(String id, PaymentType type, BigDecimal amount, String referenceId) {
        this.id = Objects.requireNonNull(id);
        this.type = Objects.requireNonNull(type);
        this.amount = amount == null ? BigDecimal.ZERO : amount;
        this.referenceId = referenceId;
        this.createdAt = LocalDateTime.now();
        this.status = PaymentStatus.PENDING;
    }

    public String getId() {
        return id;
    }

    public PaymentType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void complete(String method) {
        if (status != PaymentStatus.PENDING && status != PaymentStatus.FAILED) {
            throw InvalidStateTransitionException.forPayment(status, PaymentStatus.COMPLETED);
        }
        this.paymentMethod = method;
        status = PaymentStatus.COMPLETED;
    }

    public void fail() {
        if (status != PaymentStatus.PENDING) {
            throw InvalidStateTransitionException.forPayment(status, PaymentStatus.FAILED);
        }
        status = PaymentStatus.FAILED;
    }

    public void refund() {
        if (status != PaymentStatus.COMPLETED) {
            throw InvalidStateTransitionException.forPayment(status, PaymentStatus.REFUNDED);
        }
        status = PaymentStatus.REFUNDED;
    }
}
