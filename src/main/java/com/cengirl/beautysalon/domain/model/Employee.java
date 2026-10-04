package com.cengirl.beautysalon.domain.model;

import com.cengirl.beautysalon.domain.enums.UserRole;
import com.cengirl.beautysalon.domain.valueobject.EmployeeCalendar;
import com.cengirl.beautysalon.domain.valueobject.WorkSchedule;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Salon staff member with calendar and salary tracking.
 */
public class Employee extends User {

    private final EmployeeCalendar calendar;
    private BigDecimal monthlySalary;
    private final Map<YearMonth, Boolean> salaryPaidByMonth = new HashMap<>();

    public Employee(String id, String username, String password, String fullName,
                    String email, String phone, WorkSchedule workSchedule, BigDecimal monthlySalary) {
        super(id, UserRole.EMPLOYEE, username, password, fullName, email, phone);
        this.calendar = new EmployeeCalendar(Objects.requireNonNull(workSchedule));
        this.monthlySalary = monthlySalary == null ? BigDecimal.ZERO : monthlySalary;
    }

    public EmployeeCalendar getCalendar() {
        return calendar;
    }

    public BigDecimal getMonthlySalary() {
        return monthlySalary;
    }

    public void setMonthlySalary(BigDecimal monthlySalary) {
        this.monthlySalary = monthlySalary == null ? BigDecimal.ZERO : monthlySalary;
    }

    public boolean isSalaryPaidFor(YearMonth month) {
        return Boolean.TRUE.equals(salaryPaidByMonth.get(month));
    }

    public void markSalaryPaid(YearMonth month) {
        salaryPaidByMonth.put(month, true);
    }
}
