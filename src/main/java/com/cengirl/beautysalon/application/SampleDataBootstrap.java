package com.cengirl.beautysalon.application;

import com.cengirl.beautysalon.domain.enums.UserRole;
import com.cengirl.beautysalon.domain.factory.UserFactory;
import com.cengirl.beautysalon.domain.model.BeautyService;
import com.cengirl.beautysalon.domain.model.Employee;
import com.cengirl.beautysalon.domain.valueobject.WorkSchedule;
import com.cengirl.beautysalon.infrastructure.util.IdGenerator;
import com.cengirl.beautysalon.infrastructure.util.InputValidator;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Seeds demo data for console demonstration and testing.
 */
final class SampleDataBootstrap {

    private SampleDataBootstrap() {
    }

    static void seed(ApplicationContext context) {
        WorkSchedule schedule = new WorkSchedule();
        for (DayOfWeek day : DayOfWeek.values()) {
            if (day != DayOfWeek.SUNDAY) {
                schedule.setWorkingHours(day, LocalTime.of(9, 0), LocalTime.of(18, 0));
            }
        }

        context.getUserRepository().save(
                UserFactory.create(UserRole.MANAGER, IdGenerator.next("USR"),
                        "manager", "admin1", "Salon Manager", "manager@cengirl.com",
                        InputValidator.validateAndNormalizePhone("0532 000 00 01"),
                        null, null));

        Employee emp1 = (Employee) UserFactory.create(UserRole.EMPLOYEE, IdGenerator.next("USR"),
                "ayse", "pass12", "Ayşe Yılmaz", "ayse@cengirl.com",
                InputValidator.validateAndNormalizePhone("0532 111 00 01"), schedule,
                BigDecimal.valueOf(25000));
        Employee emp2 = (Employee) UserFactory.create(UserRole.EMPLOYEE, IdGenerator.next("USR"),
                "elif", "pass12", "Elif Demir", "elif@cengirl.com",
                InputValidator.validateAndNormalizePhone("0532 111 00 02"), schedule,
                BigDecimal.valueOf(24000));
        context.getUserRepository().save(emp1);
        context.getUserRepository().save(emp2);

        context.getAuthService().registerCustomer("musteri1", "pass12",
                "Zeynep Kaya", "zeynep@mail.com", "0532 222 00 01");

        context.getBeautyServiceRepository().save(
                new BeautyService(IdGenerator.next("SRV"), "Haircut", 45, BigDecimal.valueOf(350)));
        context.getBeautyServiceRepository().save(
                new BeautyService(IdGenerator.next("SRV"), "Manicure", 30, BigDecimal.valueOf(200)));
        context.getBeautyServiceRepository().save(
                new BeautyService(IdGenerator.next("SRV"), "Hair Coloring", 90, BigDecimal.valueOf(900)));
    }
}
