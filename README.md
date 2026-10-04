# CenGIRL – Beauty Salon Management System

**Course:** CME2210 Object Oriented Analysis and Design  
**University:** Dokuz Eylül University, Computer Engineering  
**Authors:** Şengül Akkaya (2023510121), Arzu Hamdioğlu (2023510054), Zeliha Erdoğan (2023510145)

## Overview

Console-based beauty salon management system implemented in Java with strict **N-tier layered architecture**, in-memory persistence, and design patterns (Strategy, Factory, Observer, Repository).

## Project Structure

```
src/main/java/com/cengirl/beautysalon/
├── presentation/          # Console UI (no business logic)
├── application/           # Services, payment strategies, composition root
│   ├── service/
│   └── payment/
├── domain/                # Entities, value objects, enums, repository interfaces
│   ├── model/
│   ├── valueobject/
│   ├── repository/
│   ├── factory/
│   ├── observer/
│   └── exception/
└── infrastructure/        # In-memory repositories, utilities
    ├── repository/
    └── util/
```

## Requirements

- Java 17+
- Maven 3.8+ (optional, recommended for tests)

## Run

### With Maven

```bash
mvn compile exec:java -Dexec.mainClass="com.cengirl.beautysalon.presentation.ConsoleApplication"
```

### Without Maven

```bash
javac -encoding UTF-8 -d target/classes (all files under src/main/java)
java -cp target/classes com.cengirl.beautysalon.presentation.ConsoleApplication
```

## Demo Accounts (seeded on startup)

| Role     | Username  | Password | Email               | Phone          |
|----------|-----------|----------|---------------------|----------------|
| Manager  | manager   | admin1   | manager@cengirl.com | 0532 000 00 01 |
| Employee | ayse      | pass12   | ayse@cengirl.com    | 0532 111 00 01 |
| Employee | elif      | pass12   | elif@cengirl.com    | 0532 111 00 02 |
| Customer | musteri1  | pass12   | zeynep@mail.com     | 0532 222 00 01 |

- **Login:** Main menu → 1 (all roles use the same screen)  
- **Register:** Main menu → 2 (customers only)  
- **Logout:** Role menu → 0 (returns to main menu; does not exit app)  
- **Booking dates:** Today + next 6 days (pick by index, no typing)

## Tests

```bash
mvn test
```

## Documentation

See `docs/FINAL_CONSOLIDATED_REPORT.md` for the full Phase 1–3 consolidated report.
