package com.cengirl.beautysalon.domain.valueobject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tracks booked slots and derives available windows for an employee.
 */
public class EmployeeCalendar {

    private final WorkSchedule workSchedule;
    private final List<TimeSlot> bookedSlots = new ArrayList<>();

    public EmployeeCalendar(WorkSchedule workSchedule) {
        this.workSchedule = workSchedule;
    }

    public WorkSchedule getWorkSchedule() {
        return workSchedule;
    }

    public List<TimeSlot> getBookedSlots() {
        return Collections.unmodifiableList(bookedSlots);
    }

    public void bookSlot(TimeSlot slot) {
        if (bookedSlots.stream().anyMatch(existing -> existing.overlaps(slot))) {
            throw new IllegalStateException("Time slot already booked.");
        }
        bookedSlots.add(slot);
    }

    public void releaseSlot(TimeSlot slot) {
        bookedSlots.removeIf(existing -> existing.equals(slot));
    }

    public List<TimeSlot> findAvailableSlots(LocalDate date, int durationMinutes) {
        if (!workSchedule.isWorkingDay(date)) {
            return List.of();
        }
        LocalTime open = workSchedule.getOpenTime(date);
        LocalTime close = workSchedule.getCloseTime(date);
        if (open == null || close == null) {
            return List.of();
        }

        List<TimeSlot> available = new ArrayList<>();
        LocalDateTime cursor = LocalDateTime.of(date, open);
        LocalDateTime dayEnd = LocalDateTime.of(date, close);

        while (cursor.plusMinutes(durationMinutes).isBefore(dayEnd)
                || cursor.plusMinutes(durationMinutes).equals(dayEnd)) {
            TimeSlot candidate = new TimeSlot(cursor, cursor.plusMinutes(durationMinutes));
            boolean conflict = bookedSlots.stream().anyMatch(candidate::overlaps);
            if (!conflict) {
                available.add(candidate);
            }
            cursor = cursor.plusMinutes(30);
        }
        return available;
    }
}
