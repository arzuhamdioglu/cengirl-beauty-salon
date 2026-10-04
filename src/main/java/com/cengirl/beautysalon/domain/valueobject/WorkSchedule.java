package com.cengirl.beautysalon.domain.valueobject;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Employee working hours and explicit days off.
 */
public class WorkSchedule {

    private final Map<DayOfWeek, LocalTime[]> workingHoursByDay = new EnumMap<>(DayOfWeek.class);
    private final Set<LocalDate> daysOff = new HashSet<>();

    public void setWorkingHours(DayOfWeek day, LocalTime open, LocalTime close) {
        workingHoursByDay.put(day, new LocalTime[]{open, close});
    }

    public void addDayOff(LocalDate date) {
        daysOff.add(date);
    }

    public boolean isWorkingDay(LocalDate date) {
        if (daysOff.contains(date)) {
            return false;
        }
        return workingHoursByDay.containsKey(date.getDayOfWeek());
    }

    public LocalTime getOpenTime(LocalDate date) {
        LocalTime[] hours = workingHoursByDay.get(date.getDayOfWeek());
        return hours == null ? null : hours[0];
    }

    public LocalTime getCloseTime(LocalDate date) {
        LocalTime[] hours = workingHoursByDay.get(date.getDayOfWeek());
        return hours == null ? null : hours[1];
    }

    public Set<LocalDate> getDaysOff() {
        return Collections.unmodifiableSet(daysOff);
    }
}
