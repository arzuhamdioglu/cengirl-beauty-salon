package com.cengirl.beautysalon.application.service;

import com.cengirl.beautysalon.application.payment.CashPaymentStrategy;
import com.cengirl.beautysalon.application.payment.CreditCardPaymentStrategy;
import com.cengirl.beautysalon.application.payment.DigitalWalletPaymentStrategy;
import com.cengirl.beautysalon.application.payment.PaymentStrategy;
import com.cengirl.beautysalon.domain.enums.AppointmentStatus;
import com.cengirl.beautysalon.domain.enums.PaymentType;
import com.cengirl.beautysalon.domain.exception.EntityNotFoundException;
import com.cengirl.beautysalon.domain.exception.ValidationException;
import com.cengirl.beautysalon.domain.model.Appointment;
import com.cengirl.beautysalon.domain.model.BeautyService;
import com.cengirl.beautysalon.domain.model.Employee;
import com.cengirl.beautysalon.domain.model.Payment;
import com.cengirl.beautysalon.domain.model.User;
import com.cengirl.beautysalon.domain.repository.AppointmentRepository;
import com.cengirl.beautysalon.domain.repository.BeautyServiceRepository;
import com.cengirl.beautysalon.domain.repository.PaymentRepository;
import com.cengirl.beautysalon.domain.repository.UserRepository;
import com.cengirl.beautysalon.infrastructure.util.IdGenerator;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Processes appointment and salary payments using the Strategy pattern.
 */
public class PaymentService {

	private final PaymentRepository paymentRepository;
	private final AppointmentRepository appointmentRepository;
	private final BeautyServiceRepository beautyServiceRepository;
	private final UserRepository userRepository;
	private final Map<Integer, PaymentStrategy> strategies = new HashMap<>();

	public PaymentService(PaymentRepository paymentRepository, AppointmentRepository appointmentRepository,
			BeautyServiceRepository beautyServiceRepository, UserRepository userRepository) {
		this.paymentRepository = paymentRepository;
		this.appointmentRepository = appointmentRepository;
		this.beautyServiceRepository = beautyServiceRepository;
		this.userRepository = userRepository;
		strategies.put(1, new CashPaymentStrategy());
		strategies.put(2, new CreditCardPaymentStrategy());
		strategies.put(3, new DigitalWalletPaymentStrategy());
	}

	public List<PaymentStrategy> getAvailableStrategies() {
		return List.copyOf(strategies.values());
	}

	public PaymentStrategy getStrategy(int choice) {
		PaymentStrategy strategy = strategies.get(choice);
		if (strategy == null) {
			throw new ValidationException("Invalid payment method selection.");
		}
		return strategy;
	}

	public Payment createPendingPaymentForAppointment(String appointmentId) {
		Appointment appointment = appointmentRepository.findById(appointmentId)
				.orElseThrow(() -> new EntityNotFoundException("Appointment", appointmentId));
		if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
			throw new ValidationException("Payment can only be created for completed appointments.");
		}
		BeautyService service = beautyServiceRepository.findById(appointment.getServiceId())
				.orElseThrow(() -> new EntityNotFoundException("Service", appointment.getServiceId()));

		Payment payment = new Payment(IdGenerator.next("PAY"), PaymentType.APPOINTMENT, service.getPrice(),
				appointmentId);
		appointment.linkPayment(payment.getId());
		paymentRepository.save(payment);
		appointmentRepository.save(appointment);
		return payment;
	}

	public Payment processAppointmentPayment(String customerId, String appointmentId, int methodChoice) {
		Appointment appointment = appointmentRepository.findById(appointmentId)
				.orElseThrow(() -> new EntityNotFoundException("Appointment", appointmentId));
		if (!appointment.getCustomerId().equals(customerId)) {
			throw new ValidationException("You can only pay for your own appointments.");
		}
		if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
			throw new ValidationException("Appointment must be completed before payment.");
		}

		Payment payment = appointment.getPaymentId() == null ? createPendingPaymentForAppointment(appointmentId)
				: paymentRepository.findById(appointment.getPaymentId())
						.orElseThrow(() -> new EntityNotFoundException("Payment", appointment.getPaymentId()));

		PaymentStrategy strategy = getStrategy(methodChoice);
		boolean success = strategy.process(payment);
		if (success) {
			payment.complete(strategy.getName());
			appointment.markPaid();
		} else {
			payment.fail();
		}
		paymentRepository.save(payment);
		appointmentRepository.save(appointment);
		return payment;
	}

	public Payment paySalary(String managerId, String employeeId, int methodChoice) {
		verifyManager(managerId);
		Employee employee = getEmployee(employeeId);
		YearMonth current = YearMonth.now();
		if (employee.isSalaryPaidFor(current)) {
			throw new ValidationException("Salary already paid for " + current + ".");
		}

		Payment payment = new Payment(IdGenerator.next("PAY"), PaymentType.SALARY, employee.getMonthlySalary(),
				employeeId);
		PaymentStrategy strategy = getStrategy(methodChoice);
		boolean success = strategy.process(payment);
		if (success) {
			payment.complete(strategy.getName());
			employee.markSalaryPaid(current);
			userRepository.save(employee);
		} else {
			payment.fail();
		}
		paymentRepository.save(payment);
		return payment;
	}

	private void verifyManager(String managerId) {
		User user = userRepository.findById(managerId)
				.orElseThrow(() -> new EntityNotFoundException("Manager", managerId));
		if (!(user instanceof com.cengirl.beautysalon.domain.model.Manager)) {
			throw new ValidationException("Only managers can pay salaries.");
		}
	}

	private Employee getEmployee(String id) {
		User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Employee", id));
		if (!(user instanceof Employee employee)) {
			throw new ValidationException("User is not an employee.");
		}
		return employee;
	}
}
