# 🏏 Member 1 Progress & Milestone Verification Log

**Author:** Srijan Srivastava (@srijansrivastava1234)  
**Role:** Member 1 - Project Lead & Core Backend REST API Developer  
**Date:** August 30, 2026  
**Status:** 55% Total Project Milestone Completed (100% of Member 1 Assigned Core Backend Scope)

---

## 📌 Scope & Architecture Summary

### 1. Base Spring Boot Architecture & Configurations
- Initialized Spring Boot 3.x with Java 17 and Maven Wrapper.
- Configured layered modular structure (`config`, `controller`, `dto`, `entity`, `exception`, `repository`, `service`).
- Added baseline health check endpoint (`/api/v1/health`).

### 2. Team Management REST APIs (`/api/v1/teams`)
- **CRUD Operations**: Complete registration, query by ID/all, updates, and soft/hard deletes.
- **Purse Tracking**: Automatic balance computation, total budget vs. remaining budget.
- **Squad Size Constraints**: Max 25 players, min 18 players reserve rule (`(18 - squadCount) * ₹20L`).
- **Overseas Player Quota**: Cap at 8 overseas players per franchise.

### 3. Player Management REST APIs (`/api/v1/players`)
- **CRUD Operations**: Player auction staging pool registration, queries, and updates.
- **Categorization & Filtering**: Support for `BATSMAN`, `BOWLER`, `ALL_ROUNDER`, `WICKET_KEEPER`.
- **Status State Machine**: `AVAILABLE`, `IN_AUCTION`, `SOLD`, `UNSOLD`.

### 4. Global Exception Handler (`@RestControllerAdvice`)
- Centralized JSON error format (`ApiResponse.error(...)`).
- Handlers for `ResourceNotFoundException`, `InsufficientPurseException`, `SquadLimitExceededException`, `InvalidBidException`, and validation errors (`MethodArgumentNotValidException`).

---

## 🔒 Scope Boundaries Verified
- ✅ No Security/JWT filters (Reserved for Member 2)
- ✅ No manual SQL schema locking scripts (Reserved for Member 3)
- ✅ No Frontend UI code (Reserved for Member 4)
- ✅ No QA/Swagger test configuration (Reserved for Member 5)
