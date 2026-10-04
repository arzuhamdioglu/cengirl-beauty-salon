package com.cengirl.beautysalon.application.service;

import com.cengirl.beautysalon.domain.enums.UserRole;
import com.cengirl.beautysalon.domain.exception.EntityNotFoundException;
import com.cengirl.beautysalon.domain.exception.ValidationException;
//import com.cengirl.beautysalon.domain.factory.UserFactory;
import com.cengirl.beautysalon.domain.model.BeautyService;
import com.cengirl.beautysalon.domain.model.Employee;
import com.cengirl.beautysalon.domain.model.Manager;
import com.cengirl.beautysalon.domain.model.User;
import com.cengirl.beautysalon.domain.repository.BeautyServiceRepository;
import com.cengirl.beautysalon.domain.repository.UserRepository;
import com.cengirl.beautysalon.domain.valueobject.WorkSchedule;
import com.cengirl.beautysalon.infrastructure.util.IdGenerator;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Manager operations: catalog, staff, and working hours.
 */
public class ManagerService {

    private final UserRepository userRepository;
    private final BeautyServiceRepository beautyServiceRepository;
    private final AuthService authService;

    public ManagerService(UserRepository userRepository,
                          BeautyServiceRepository beautyServiceRepository,
                          AuthService authService) {
        this.userRepository = userRepository;
        this.beautyServiceRepository = beautyServiceRepository;
        this.authService = authService;
    }

    public void verifyManager(String managerId) {
        User user = userRepository.findById(managerId)
                .orElseThrow(() -> new EntityNotFoundException("Manager", managerId));
        if (!(user instanceof Manager)) {
            throw new ValidationException("Manager privileges required.");
        }
    }

    public BeautyService addService(String managerId, String name, int duration, BigDecimal price) {
        verifyManager(managerId);
        BeautyService service = new BeautyService(IdGenerator.next("SRV"), name, duration, price);
        beautyServiceRepository.save(service);
        return service;
    }

    public BeautyService updateService(String managerId, String serviceId,
                                       String name, int duration, BigDecimal price) {
        verifyManager(managerId);
        BeautyService service = beautyServiceRepository.findById(serviceId)
                .orElseThrow(() -> new EntityNotFoundException("Service", serviceId));
        service.update(name, duration, price);
        beautyServiceRepository.save(service);
        return service;
    }

    public void deleteService(String managerId, String serviceId) {
        verifyManager(managerId);
        beautyServiceRepository.deleteById(serviceId);
    }

    public List<BeautyService> listServices() {
        return beautyServiceRepository.findAll();
    }

    public Employee addEmployee(String managerId, String username, String password,
                                String fullName, String email, String phone,
                                BigDecimal salary, WorkSchedule schedule) {
        verifyManager(managerId);
        User user = authService.register(UserRole.EMPLOYEE, username, password,
                fullName, email, phone, schedule, salary);
        return (Employee) user;
    }

    public List<Employee> listEmployees() {
        return userRepository.findByRole(UserRole.EMPLOYEE).stream()
                .map(Employee.class::cast)
                .toList();
    }

    public void setWorkingHours(String managerId, String employeeId,
                               DayOfWeek day, LocalTime open, LocalTime close) {
        verifyManager(managerId);
        Employee employee = getEmployee(employeeId);
        employee.getCalendar().getWorkSchedule().setWorkingHours(day, open, close);
        userRepository.save(employee);
    }

    public void addDayOff(String managerId, String employeeId, LocalDate date) {
        verifyManager(managerId);
        Employee employee = getEmployee(employeeId);
        employee.getCalendar().getWorkSchedule().addDayOff(date);
        userRepository.save(employee);
    }

    public void updateEmployeeSalary(String managerId, String employeeId, BigDecimal salary) {
        verifyManager(managerId);
        Employee employee = getEmployee(employeeId);
        employee.setMonthlySalary(salary);
        userRepository.save(employee);
    }

    private Employee getEmployee(String employeeId) {
        User user = userRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee", employeeId));
        if (!(user instanceof Employee employee)) {
            throw new ValidationException("User is not an employee.");
        }
        return employee;
    }
}
