# CenGIRL – Beauty Salon Management System
## Final Consolidated Report (Phase 1 + Phase 2 + Phase 3)

---

**Course:** CME2210 Object Oriented Analysis and Design  
**Institution:** Dokuz Eylül University, Faculty of Engineering, Department of Computer Engineering  
**Project:** CenGIRL – Beauty Salon Management System  
**Lecturer:** Prof. Dr. Semih Utku  

**Team Members:**

| Name | Student No |
|------|------------|
| Şengül Akkaya | 2023510121 |
| Arzu Hamdioğlu | 2023510054 |
| Zeliha Erdoğan | 2023510145 |

**Submission Date:** May 2026

---

## Table of Contents

1. [Introduction](#1-introduction)
2. [Phase 1 – Analysis](#2-phase-1--analysis)
3. [Phase 2 – Design](#3-phase-2--design)
4. [Phase 3 – Implementation](#4-phase-3--implementation)
5. [Testing Approach and Results](#5-testing-approach-and-results)
6. [Reflection and Future Improvements](#6-reflection-and-future-improvements)
7. [Conclusion](#7-conclusion)
8. [Appendix](#8-appendix)

---

## 1. Introduction

This document is the **final consolidated report** for the CenGIRL Beauty Salon Management System. It combines:

- **Phase 1** – Requirements analysis and use-case modelling  
- **Phase 2** – Architectural and detailed design (diagrams and patterns)  
- **Phase 3** – Java implementation, code structure, SOLID evaluation, and testing  

The implementation follows the design decisions documented in Phases 1 and 2: a console-based application, in-memory persistence, strict N-tier layering, and targeted use of design patterns.

---

## 2. Phase 1 – Analysis

### 2.1 System Definition

**CenGIRL – Beauty Salon Management System** is a software solution that digitizes appointment scheduling, staff calendars, service catalog management, and payment workflows for a beauty salon. It replaces manual processes that cause double-booking, unclear staff availability, and unstructured payments.

The system supports three actor types:

| Actor | Description |
|-------|-------------|
| **Customer** | Registers, books appointments, views services, pays for completed services |
| **Employee** | Manages personal calendar and updates appointment statuses |
| **Manager** | Manages services, employees, working hours, and payroll |

Two background services support core workflows:

- **Notification Service** – Confirms booking and cancellation events  
- **Payment Service** – Processes customer payments and employee salaries  

### 2.2 Project Objectives

| # | Objective |
|---|-----------|
| 1 | Prioritize software design quality over GUI or persistent database |
| 2 | Implement a medium-scale Java system with in-memory data structures |
| 3 | Apply OOAD principles and Clean Code practices |
| 4 | Enforce N-tier architecture: Presentation, Application, Domain, Infrastructure |
| 5 | Integrate design patterns and SOLID principles for maintainability |

### 2.3 Functional Requirements

#### A. Customer Functions

| ID | Requirement |
|----|-------------|
| FR-C1 | Register new account |
| FR-C2 | Log in with identity verification and failed-login handling |
| FR-C3 | View and update profile |
| FR-C4 | List salon services |
| FR-C5 | View available time slots for service + employee |
| FR-C6 | Create appointment (triggers Notification Service) |
| FR-C7 | View past appointments |
| FR-C8 | Cancel active appointment (updates calendar, notifies) |
| FR-C9 | Make payment for completed service (uses Payment Service) |

#### B. Manager Functions

| ID | Requirement |
|----|-------------|
| FR-M1 | Add, update, delete beauty services (name, duration, price) |
| FR-M2 | Add and manage employee records |
| FR-M3 | Set working hours, days off, availability |
| FR-M4 | Calculate and pay employee salaries (Payment Service) |

#### C. Employee Functions

| ID | Requirement |
|----|-------------|
| FR-E1 | Log in with unique credentials |
| FR-E2 | View assigned appointments / calendar |
| FR-E3 | Update appointment status (Completed, Cancelled, No-Show) |

#### D. Core System Services

| Service | Responsibility |
|---------|----------------|
| Notification Service | Status updates on appointment create/cancel |
| Payment Service | Customer payments and manager salary payments |

### 2.4 Non-Functional Requirements

| Category | Requirement |
|----------|-------------|
| Maintainability | Layered architecture, Clean Code, SOLID |
| Extensibility | Open-Closed via Strategy and Factory patterns |
| Data Integrity | Referential consistency, encapsulation, no double-booking |
| Security | Role-based access, login lockout after failed attempts |
| Performance | HashMap-based O(1) lookups where applicable |
| Robustness | Exception handling for invalid input |
| Usability | Styled console menus (ANSI colors, boxed layouts), clear prompts and error messages |

### 2.5 Use Case Descriptions

#### UC-1: Create Appointment

- **Primary Actor:** Customer  
- **Includes:** Notification Service  
- **Flow:** Select service & employee → view slots → confirm → save as Scheduled → update calendar → notify both parties  

#### UC-2: Complete Service and Make Payment

- **Primary Actors:** Employee, Customer  
- **Includes:** Payment Service  
- **Flow:** Employee marks Completed → pending payment created → customer pays → appointment becomes Paid  

#### UC-3: Cancel Appointment

- **Primary Actor:** Customer  
- **Includes:** Notification Service  
- **Flow:** Select appointment → confirm → status Cancelled → release slot → notify employee  

#### UC-4: Pay Salary

- **Primary Actor:** Manager  
- **Includes:** Payment Service  
- **Flow:** Select employee → confirm amount → process payment → mark salary paid for current month  

### 2.6 Use Case Diagram (Textual)

```
                    ┌─────────────────────────────────────┐
                    │   Beauty Salon Management System    │
                    └─────────────────────────────────────┘
         Customer          Employee           Manager
            │                  │                  │
    Register, Login      Login (shared)    Service Management
    Profile Mgmt         View Calendar     Employee Management
    List Services        Update Status     Working Hours
    View Slots                              Pay Salary
    Create Appt ──────► Notification ◄──── Cancel Appt
    View/Cancel Appt
    Make Payment ─────► Payment Service ◄── Pay Salary
```

---

## 3. Phase 2 – Design

### 3.1 Architectural Design (N-Tier)

```
┌─────────────────────────────────────────────────────────────┐
│                  PRESENTATION LAYER                         │
│   ConsoleApplication, MainMenu, Customer/Employee/Manager   │
│   Menus, ConsoleIO, ConsoleUI (styling & layout)            │
└───────────────────────────┬─────────────────────────────────┘
                            │ delegates
┌───────────────────────────▼─────────────────────────────────┐
│                  APPLICATION / SERVICE LAYER                    │
│   AuthService, AppointmentService, PaymentService,              │
│   ManagerService, NotificationService, UserProfileService       │
│   PaymentStrategy implementations                             │
└───────────────────────────┬─────────────────────────────────┘
                            │ uses
┌───────────────────────────▼─────────────────────────────────┐
│                      DOMAIN LAYER                             │
│   User, Customer, Employee, Manager, Appointment,             │
│   BeautyService, Payment, TimeSlot, EmployeeCalendar          │
│   Repository interfaces, UserFactory, AppointmentObserver       │
└───────────────────────────┬─────────────────────────────────┘
                            │ implemented by
┌───────────────────────────▼─────────────────────────────────┐
│                   INFRASTRUCTURE LAYER                        │
│   InMemory*Repository, IdGenerator, DateTimeUtil              │
└─────────────────────────────────────────────────────────────┘
```

**Dependency rule:** Upper layers depend on lower layers; Domain never depends on Presentation or Infrastructure implementations (Dependency Inversion via repository interfaces).

### 3.2 Package Organization

| Package | Contents |
|---------|----------|
| `presentation` | Console UI, role menus, `ConsoleIO`, `ConsoleUI` |
| `presentation.menu` | `MainMenu`, `CustomerMenu`, `EmployeeMenu`, `ManagerMenu` |
| `presentation.util` | Input helpers and visual formatting |
| `application` | `ApplicationContext`, `SampleDataBootstrap` |
| `application.service` | Use-case orchestration |
| `application.payment` | Strategy pattern for payment methods |
| `domain.model` | Core entities |
| `domain.valueobject` | TimeSlot, WorkSchedule, EmployeeCalendar |
| `domain.enums` | Status and role enumerations |
| `domain.repository` | Repository interfaces |
| `domain.factory` | UserFactory |
| `domain.observer` | AppointmentObserver |
| `domain.exception` | Domain-specific exceptions |
| `infrastructure.repository` | In-memory repository implementations |
| `infrastructure.util` | IdGenerator, DateTimeUtil |

### 3.3 Class Diagram (Summary)

| Class | Responsibility | Key Relationships |
|-------|----------------|-------------------|
| `User` (abstract) | Credentials, profile, lockout | Extended by Customer, Employee, Manager |
| `Customer` | Appointment list references | Links appointment IDs |
| `Employee` | Calendar, salary tracking | Has EmployeeCalendar |
| `Manager` | Administrative actor | — |
| `BeautyService` | Catalog item | Referenced by Appointment |
| `Appointment` | Booking lifecycle | Associated with Customer, Employee, Service |
| `Payment` | Financial record | Linked to appointment or employee |
| `AppointmentService` | Appointment CRUD orchestration | Uses repositories + NotificationService |
| `PaymentService` | Payment processing | Uses PaymentStrategy |
| `NotificationService` | Observer notifications | Implements AppointmentObserver |
| `ConsoleUI` | Console formatting (presentation only) | Used by menus; no domain dependency |

### 3.4 Sequence Diagrams (Narrative)

**Create Appointment:** Customer → Presentation → AppointmentService → validate slot → persist Appointment → book EmployeeCalendar → NotificationService.notify  

**Cancel Appointment:** Customer → AppointmentService → verify Scheduled → cancel → release calendar slot → NotificationService.notify  

**Complete & Pay:** Employee → mark Completed → PaymentService creates Pending payment → Customer selects strategy → PaymentService processes → Appointment.markPaid  

**Pay Salary:** Manager → PaymentService → strategy.process → Employee.markSalaryPaid  

### 3.5 Statechart Diagrams

#### Appointment Lifecycle

| From | Trigger | To |
|------|---------|-----|
| (initial) | Create | SCHEDULED |
| SCHEDULED | Employee completes | COMPLETED |
| SCHEDULED | Customer cancels | CANCELLED |
| SCHEDULED | No-show | NO_SHOW |
| COMPLETED | Payment success | PAID |

#### Payment Lifecycle

| From | Trigger | To |
|------|---------|-----|
| (initial) | Created | PENDING |
| PENDING | Success | COMPLETED |
| PENDING | Failure | FAILED |
| COMPLETED | Refund | REFUNDED |

### 3.6 Activity Diagrams (Summary)

1. **Login:** Single login screen for all actors → verify credentials → increment failures / lock OR route to role-specific dashboard (Customer / Employee / Manager)  
2. **Registration:** Available only for customers on the main menu; employees are created by the manager; the manager account is seeded at startup  
3. **Booking:** Select service & employee → fetch slots → conflict check → persist → notify  
4. **Payment:** Select method (Strategy) → process → mark Paid OR retry on failure  

### 3.7 Design Patterns Applied

| Pattern | Location | Purpose |
|---------|----------|---------|
| **Repository** | `domain.repository` + `infrastructure.repository` | Abstract data access |
| **Factory Method** | `UserFactory` | Create user subtypes without coupling services |
| **Strategy** | `PaymentStrategy` + implementations | Extensible payment methods |
| **Observer** | `AppointmentObserver` + `NotificationService` | Decouple notifications from appointment logic |

---

## 4. Phase 3 – Implementation

### 4.1 Implementation Overview

The system is implemented as a **Maven Java 17** project with **53 main source files** and **4 test classes**, organized into four layers. All persistence uses **in-memory HashMap-based repositories**. The user interface is a **styled console menu** system with role-specific dashboards.

| Item | Value |
|------|-------|
| Entry point | `com.cengirl.beautysalon.presentation.ConsoleApplication` |
| Composition root | `com.cengirl.beautysalon.application.ApplicationContext` |
| Demo data loader | `com.cengirl.beautysalon.application.SampleDataBootstrap` |
| Build tool | Maven (`pom.xml`) |
| Java version | 17 |

On startup, `ApplicationContext` constructs all repositories and services, then `SampleDataBootstrap` seeds a manager account, two employees, one demo customer, and three beauty services so the system can be demonstrated immediately without manual setup.

### 4.2 Layered Structure in Code

| Layer | Representative Classes |
|-------|------------------------|
| Presentation | `ConsoleApplication`, `MainMenu`, `CustomerMenu`, `EmployeeMenu`, `ManagerMenu`, `ConsoleUI`, `ConsoleIO` |
| Application | `AppointmentService`, `PaymentService`, `AuthService`, `ManagerService` |
| Domain | `Appointment`, `User`, `BeautyService`, `Payment`, `EmployeeCalendar` |
| Infrastructure | `InMemoryUserRepository`, `InMemoryAppointmentRepository`, `IdGenerator` |

**Example – Presentation delegates to Application (no business logic):**

```java
// CustomerMenu.createAppointment()
Appointment apt = context.getAppointmentService()
    .createAppointment(user.getId(), employeeId, serviceId, slots.get(index));
```

**Example – Domain enforces state rules:**

```java
// Appointment.markCompleted() only from SCHEDULED
private void transition(AppointmentStatus expected, AppointmentStatus next) {
    if (status != expected) {
        throw InvalidStateTransitionException.forAppointment(status, next);
    }
    status = next;
}
```

### 4.3 Applied Design Patterns in Implementation

#### Strategy Pattern (`application.payment`)

```java
public interface PaymentStrategy {
    String getName();
    boolean process(Payment payment);
}
```

Implementations: `CashPaymentStrategy`, `CreditCardPaymentStrategy`, `DigitalWalletPaymentStrategy`.  
`PaymentService` selects strategy by user choice without modifying core payment logic.

#### Factory Method (`domain.factory.UserFactory`)

```java
public static User create(UserRole role, String id, ...) {
    return switch (role) {
        case CUSTOMER -> new Customer(...);
        case EMPLOYEE -> new Employee(...);
        case MANAGER -> new Manager(...);
    };
}
```

#### Observer Pattern (`domain.observer` + `NotificationService`)

`NotificationService` implements `AppointmentObserver` and is invoked by `AppointmentService` on create/cancel events.

#### Repository Pattern

Interfaces in `domain.repository`; implementations in `infrastructure.repository`.

### 4.4 Key Classes and Components

| Class | Layer | Responsibility |
|-------|-------|----------------|
| `Appointment` | Domain | Encapsulates booking state machine |
| `EmployeeCalendar` | Domain | Slot booking and availability calculation |
| `AppointmentService` | Application | Orchestrates booking, cancel, status updates |
| `PaymentService` | Application | Appointment and salary payments |
| `AuthService` | Application | Registration, login, account lockout |
| `ManagerService` | Application | Catalog and staff administration |
| `InMemoryAppointmentRepository` | Infrastructure | HashMap storage for appointments |

### 4.5 Service Layer Operations

| Service | Key Operations |
|---------|----------------|
| `AuthService` | `registerCustomer`, `register` (internal), `login` (3-strike lockout, all roles) |
| `UserProfileService` | `getUser`, `updateProfile` |
| `AppointmentService` | `getAvailableSlots`, `createAppointment`, `cancelAppointment`, `updateStatusByEmployee` |
| `PaymentService` | `createPendingPaymentForAppointment`, `processAppointmentPayment`, `paySalary` |
| `ManagerService` | `addService`, `updateService`, `deleteService`, `addEmployee`, `setWorkingHours`, `addDayOff` |
| `NotificationService` | `onAppointmentCreated`, `onAppointmentCancelled` |

### 4.6 SOLID Principle Evaluation

| Principle | How It Is Applied |
|-----------|-------------------|
| **S – Single Responsibility** | Each service handles one area (auth, appointments, payments). Menus only handle I/O. |
| **O – Open/Closed** | New payment methods added by new `PaymentStrategy` classes without changing `PaymentService` core flow. |
| **L – Liskov Substitution** | `Customer`, `Employee`, `Manager` are usable wherever `User` is expected. |
| **I – Interface Segregation** | Small interfaces: `PaymentStrategy`, `AppointmentObserver`, repository interfaces per entity. |
| **D – Dependency Inversion** | Services depend on `UserRepository`, not `InMemoryUserRepository`. |

### 4.7 Code Structure and Class Interaction

**Typical request flow (Create Appointment):**

1. `CustomerMenu` reads input and calls `AppointmentService.createAppointment`  
2. `AppointmentService` loads `Employee`, `BeautyService`, validates slot via `EmployeeCalendar`  
3. Creates `Appointment`, books slot, saves via `AppointmentRepository`  
4. Calls `NotificationService.onAppointmentCreated`  
5. Returns result to menu for display  

**Authorization:** Services validate actor role and ownership (e.g., customer can only cancel own appointments; manager-only salary payment).

### 4.8 Exception Handling

Custom hierarchy under `DomainException`:

- `ValidationException` – invalid input or business rule  
- `AuthenticationException` – login failures  
- `EntityNotFoundException` – missing entities  
- `InvalidStateTransitionException` – illegal state changes  

`ConsoleApplication` catches `DomainException` and displays user-friendly messages via `ConsoleUI.printError()` without crashing.

### 4.9 Presentation Layer and Console User Experience

The presentation layer contains **no business logic**. All rules remain in domain and application services. Visual enhancements are isolated in `ConsoleUI` and do not affect service behaviour.

#### 4.9.1 Main Menu Structure

| Option | Action | Actors |
|--------|--------|--------|
| 1 – Login | Shared authentication for Customer, Employee, Manager | All |
| 2 – Register | Creates a new customer account only | Customer |
| 0 – Exit | Terminates the application | All |

After a successful login, the system reads the user’s `UserRole` and opens the matching dashboard. There is no separate login screen per role.

#### 4.9.2 Role Dashboards

**Customer dashboard:** Profile, list services, view slots, book appointment, view/cancel appointments, make payment.

**Employee dashboard:** View calendar, update appointment status (Completed / No-Show / Cancelled).

**Manager dashboard:** Service CRUD, add/list employees, set working hours, add day off, pay salary.

#### 4.9.3 ConsoleUI Features

`ConsoleUI` provides presentation-only formatting:

- ASCII startup banner with **CenGIRL** logo  
- Box-drawn menus with fixed-width right border alignment (`WIDTH = 62`)  
- ANSI colours for headers, options, success (✔), warning (⚠), and error (✖) messages  
- Section headers and tabular output for lists (services, appointments, employees)  
- Styled input prompts via `ConsoleIO` (`❯ Your choice:`)

Business operations still call the same service methods; only `System.out` output is formatted.

#### 4.9.4 Authentication and Account Provisioning

| Actor | How account is created | How to sign in |
|-------|------------------------|----------------|
| **Customer** | Main menu → Register, or seeded demo user | Main menu → Login |
| **Employee** | Manager → Add Employee | Main menu → Login (same screen as customer) |
| **Manager** | Seeded at startup (`SampleDataBootstrap`) | Main menu → Login |

`AuthService` verifies username/password, applies a **3-attempt lockout** policy, and returns the authenticated `User`. `MainMenu` routes by `user.getRole()` to the correct dashboard.

### 4.10 Demo Accounts (Seeded Data)

| Role | Username | Password | Notes |
|------|----------|----------|-------|
| Manager | `manager` | `admin1` | Full administrative access |
| Employee | `ayse` | `pass12` | Stylist – Ayşe Yılmaz |
| Employee | `elif` | `pass12` | Stylist – Elif Demir |
| Customer | `musteri1` | `pass12` | Demo customer – Zeynep Kaya |

**Pre-loaded services:** Haircut (45 min, 350 TRY), Manicure (30 min, 200 TRY), Hair Coloring (90 min, 900 TRY).

### 4.11 How to Run (Eclipse)

1. Import project: **File → Import → Maven → Existing Maven Projects**  
2. Select folder `OOProjeOdeviJava`  
3. Open `ConsoleApplication.java` → **Run As → Java Application**  
4. Use demo credentials above to test each role  

Alternative (command line): `mvn compile exec:java -Dexec.mainClass="com.cengirl.beautysalon.presentation.ConsoleApplication"`

---

## 5. Testing Approach and Results

### 5.1 Unit Testing

JUnit 5 tests target domain rules and service logic:

| Test Class | Focus |
|------------|-------|
| `AppointmentTest` | State transitions (Scheduled → Completed → Paid) |
| `AppointmentServiceTest` | Create/cancel, double-booking prevention |
| `AuthServiceTest` | Account lockout after 3 failed logins |
| `PaymentServiceTest` | Successful appointment payment |

**Run:** `mvn test`

### 5.2 Functional Testing (Manual Console)

| # | Scenario | Steps | Expected Result |
|---|----------|-------|-----------------|
| 1 | Customer login | Login: `musteri1` / `pass12` | Customer dashboard opens |
| 2 | Manager login | Login: `manager` / `admin1` | Manager dashboard opens |
| 3 | Employee login | Login: `ayse` / `pass12` | Employee dashboard opens |
| 4 | Customer registration | Register new customer → Login | New account works |
| 5 | Customer booking | List services → pick employee ID → book slot | Scheduled + notification printed |
| 6 | Cancel appointment | Cancel active appointment (confirm `y`) | Slot released, notification sent |
| 7 | Complete & pay | Employee marks Completed → customer pays (Cash) | Status PAID, payment COMPLETED |
| 8 | Manager salary | Manager → Pay Salary → select employee | Salary payment COMPLETED |
| 9 | Add employee | Manager → Add Employee → Login as new user | New employee dashboard works |
| 10 | Lockout | 3 wrong passwords on same account | Account locked |
| 11 | Double booking | Book same slot twice | Validation error, no duplicate booking |

### 5.3 Results

- Main sources compile successfully with `javac` (Java 17/21)  
- Console application starts and displays seeded demo data  
- Unit tests designed for Maven Surefire execution  

---

## 6. Reflection and Future Improvements

### 6.1 Challenges Faced

- Keeping **calendar consistency** when appointments are cancelled or marked no-show  
- Enforcing **state transitions** only through domain methods  
- Separating **presentation** from business logic while adding rich console styling via `ConsoleUI`  
- Aligning fixed-width menu borders when using ANSI colour codes (solved with `stripAnsi()` padding)  
- Clarifying that **one login screen** serves all roles while only customers self-register on the main menu  

### 6.2 Lessons Learned

- Designing repository interfaces early simplified testing and layer separation  
- Encapsulating appointment states in the domain layer prevented invalid mutations from services  
- Design patterns (especially Strategy and Observer) reduced coupling and matched Phase 2 design closely  

### 6.3 Future Enhancements

| Enhancement | Description |
|-------------|-------------|
| Database persistence | Replace in-memory repositories with JDBC/JPA implementations |
| GUI | Swing/JavaFX or web frontend |
| Email/SMS notifications | Real notification channels instead of console output |
| Reporting | Manager dashboards for revenue and occupancy |
| Appointment reminders | Scheduled notifications before appointment time |

---

## 7. Conclusion

The CenGIRL Beauty Salon Management System successfully progresses from **requirements analysis (Phase 1)** through **architectural design (Phase 2)** to a **working Java implementation (Phase 3)**. The codebase reflects the intended N-tier structure, applies required design patterns, enforces domain rules through encapsulation, and delivers a fully runnable console application with styled menus, seeded demo data, and role-based dashboards for customers, employees, and managers.

---

## 8. Appendix

### A. Deliverables Checklist

- [x] Final Consolidated Report (this document)  
- [x] Complete Java Source Code (`src/main/java`)  
- [x] Unit Tests (`src/test/java`)  
- [x] README with run instructions  

### B. Source File Count by Layer

| Layer / Area | Files |
|--------------|-------|
| Presentation (`presentation`, `menu`, `util`) | 7 |
| Application (`application`, `service`, `payment`) | 13 |
| Domain (`model`, `valueobject`, `enums`, `repository`, `factory`, `observer`, `exception`) | 27 |
| Infrastructure (`repository`, `util`) | 6 |
| Unit tests (`src/test/java`) | 4 |
| **Total main sources** | **53** |

### C. Complete Source Tree (Main)

```
src/main/java/com/cengirl/beautysalon/
├── presentation/
│   ├── ConsoleApplication.java
│   ├── menu/          (MainMenu, CustomerMenu, EmployeeMenu, ManagerMenu)
│   └── util/          (ConsoleIO, ConsoleUI)
├── application/
│   ├── ApplicationContext.java
│   ├── SampleDataBootstrap.java
│   ├── service/       (Auth, Appointment, Payment, Manager, Notification, UserProfile)
│   └── payment/       (PaymentStrategy + Cash, CreditCard, DigitalWallet)
├── domain/
│   ├── model/         (User, Customer, Employee, Manager, Appointment, BeautyService, Payment)
│   ├── valueobject/   (TimeSlot, WorkSchedule, EmployeeCalendar)
│   ├── enums/         (UserRole, AppointmentStatus, PaymentStatus, PaymentType)
│   ├── repository/    (interfaces)
│   ├── factory/       (UserFactory)
│   ├── observer/      (AppointmentObserver)
│   └── exception/
└── infrastructure/
    ├── repository/    (InMemory* implementations)
    └── util/          (IdGenerator, DateTimeUtil)
```

### D. Demo Credentials and Quick Test Flow

| Role | Username | Password |
|------|----------|----------|
| Manager | `manager` | `admin1` |
| Employee | `ayse` | `pass12` |
| Employee | `elif` | `pass12` |
| Customer | `musteri1` | `pass12` |

**Suggested end-to-end demo:** Manager login → Add Employee (optional) → Customer login → Book appointment → Employee login → Mark Completed → Customer login → Pay → Manager login → Pay Salary.

### E. Key Presentation Classes

| Class | Responsibility |
|-------|----------------|
| `ConsoleApplication` | Main loop, exception handling, startup/shutdown banners |
| `MainMenu` | Guest menu (login/register), role routing after authentication |
| `CustomerMenu` | Customer use-case menus |
| `EmployeeMenu` | Employee calendar and status updates |
| `ManagerMenu` | Administrative and payroll menus |
| `ConsoleUI` | Visual formatting only (banners, boxes, colours, tables) |
| `ConsoleIO` | User input with styled prompts |

---

*End of Report*
