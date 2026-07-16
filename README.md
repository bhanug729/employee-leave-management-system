# Employee Leave Management System

A microservices-based HR leave-management system built with **Spring Boot 4**, **Spring Cloud**, and **MySQL**. Employees and Leave Applications are owned by independent services that discover each other through **Eureka** and communicate synchronously through a **load-balanced RestClient**, all fronted by a single **API Gateway**.

## Architecture

```
                          ┌─────────────────────┐
                          │     Eureka Server   │
                          │  (Service Registry) │
                          │        :8761        │
                          └───────────▲─────────┘
                                      │ (register/discover)
                                      │
                 ┌────────────────────┼─────────────────────┐
                 │                    │                     │
        ┌────────▲─────────┐    ┌──────▲──────┐    ┌────────▲──────┐
        │ employee-service │◄───│ API Gateway │───►│ leave-service │
        │      :8081       │    │    :8080    │    │     :8082     │
        └────▼────────▲────┘    └─────────────┘    └────▼─────▼────┘
             │        │                                 │     │
             │        │    RestClient(@LoadBalanced)    │     │
             │        └─────────────────────────────────┘     │
             │             ┌─────────────────────┐            │
             └────────────►│   MySQL (employee,  │◄───────────┘
                           │ leave_applications) │
                           └─────────────────────┘
```

## Key Features
* Centralized exception handling with `@RestControllerAdvice` for consistent error responses
* Service discovery and load balancing via Eureka + RestClient
* Leave balance deducted only on approval, restored on cancellation

## Leave approval workflow
1. `POST /leaves/apply` - employee applies for leave. `leave-service` calls `employee-service` (via `RestClient`) to confirm the employee exists and has enough balance, then stores the request as `PENDING`. **Balance is not touched yet.**
2. `PUT /leaves/{leave_id}/approve` - manager approves. Only now does `leave-service` call `employee-service` to actually deduct the days, then flips status to `APPROVED`.
3. `PUT /leaves/{leave_id}/reject`  - manager rejects a `PENDING` request. No balance was ever deducted, so nothing to restore.
4. `PUT /leaves/{leave_id}/cancel`  - if the leave was `APPROVED`, the days are restored via another cross-service call before marking it `CANCELLED`.

## Tech Stack

| Concern | Technology                                                   |
|---|--------------------------------------------------------------|
| Language / Runtime | Java 21                                                      |
| Framework | Spring Boot 4.0.7                                            |
| Microservices | Spring Cloud 2025.1.2 (Oakwood)                              |
| Service Discovery | Netflix Eureka                                               |
| API Gateway | Spring Cloud Gateway                                         |
| Inter-service calls | `RestClient` + `@LoadBalanced` (Spring Cloud LoadBalancer) |
| Persistence | Spring Data JPA / Hibernate + MySQL                          |
| Exception Handling | @RestControllerAdvice + GlobalExceptionHandler               |
| Logging | LogBack/Log4j2 (via SLF4J)                                   |

## Project Structure

```
employee-leave-management-system/
├── eureka-server/     # Service registry (:8761)
├── api-gateway/       # Single entry point, routes by path (:8080)
├── employee-service/  # Owns Employee data + leave balance (:8081)
├── leave-service/     # Owns Leave Application data, calls employee-service via RestClient (:8082)
└── README.md
```

## Prerequisites
- **Java 21+, Maven 3.8+, MySQL 8+**

## How to Run

### Run each module locally with Maven

**1. Start MySQL**

MySQL runs on `localhost:3306`. Single database `leave_employee_db` is used with two tables `employee` and `leave_applications`. Database and tables auto-created on first run.

**2. Start each service in a separate terminal, in this order:**
```bash
cd eureka-server      && mvn spring-boot:run   # wait until it's up before continuing
cd api-gateway        && mvn spring-boot:run
cd employee-service   && mvn spring-boot:run
cd leave-service      && mvn spring-boot:run
```

**3. Confirm registration** at `http://localhost:8761` - you should see `API-GATEWAY`, `EMPLOYEE-SERVICE`, and `LEAVE-SERVICE` registered as `UP`.

---

## API Reference

All requests go through the gateway at http://localhost:8080.

### Employee Service
| Method | Endpoint                                         | Description                                               |
|---|--------------------------------------------------|-----------------------------------------------------------|
| GET | `/employee`                                      | List all employees                                        |
| GET | `/employee/{id}`                                 | Get employee by ID                                        |
| GET | `/employee/code/{employee_code}`                 | Get employee by business code                             |
| POST | `/employee/add`                                  | Create employee (defaults to 24 days annual leave)        |
| PUT | `/employee/{id}`                                 | Update employee                                           |
| DELETE | `/employee/{id}`                                 | Delete employee                                           |
| PUT | `/employee/{employee_code}/deduct-leave?days=N`  | Internal (called by leave-service), Deduct leave balance  |
| PUT | `/employee/{employee_code}/restore-leave?days=N` | Internal (called by leave-service), Restore leave balance |

### Leave Service
| Method | Endpoint                                  | Description |
|---|-------------------------------------------|----|
| GET | `/leaves/all`                             |  List all leave requests |
| GET | `/leaves/{leave_id}`                      | Get a leave request |
| GET | `/leaves/employee/{employee_code}`        |  Leave history for one employee |
| POST | `/leaves/apply`                           | Apply for leave |
| PUT | `/leaves/{leave_id}/approve?approvedBy=X` |  Approve (deducts balance) |
| PUT | `/leaves/{leave_id}/reject?rejectedBy=X`  |  Reject |
| PUT | `/leaves/{leave_id}/cancel`               |  Cancel (restores balance if it was approved) |

## Possible Future Enhancements
*(not implemented, kept out of scope on purpose)*

* **Security:** Add Spring Security + JWT with API Gateway authentication filter
* **Testing:** Unit & Integration tests for employee-service and leave-service
* **Notifications:** Email service for leave approval / rejection events
