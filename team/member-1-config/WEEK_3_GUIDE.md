# 🏏 Week 3 Guide: Exception Handling Verification & Branch Merging

**Role:** Member 1 - Project Lead & Core Backend REST API Developer

Welcome to **Week 3** of the IPL Auction System project. Now that all Week 2 PRs have been integrated into the remote `main` branch on GitHub, this week focuses on **verifying and validating the exception handling mechanism and business rule validations** directly on the integrated root-level layout.

---

## 📂 Progress Audit: How Much Work is Done?

The remote repository has been force-updated to the integrated team structure where the Spring Boot application resides directly at the root level. All exception classes, service validation rules, and the `@ControllerAdvice` handler are already implemented and merged:

| Mapped Exception | Mapped HTTP Status | Trigger Context |
| :--- | :--- | :--- |
| **`ResourceNotFoundException`** | `404 NOT_FOUND` | Thrown when a player lookup by ID or team lookup fails. |
| **`BadRequestException`** | `400 BAD_REQUEST` | Thrown on validation conflicts (e.g. team name duplication or overseas caps). |
| **`InsufficientPurseException`** | `400 BAD_REQUEST` | Thrown when a bid exceeds the team's available purse. |
| **`SquadLimitExceededException`** | `400 BAD_REQUEST` | Thrown when a team has reached its squad size limit (25). |

---

## 🛠️ Step 1: Git Branch Setup

To ensure your workspace is synchronized with your teammates, you need to pull the integrated remote `main` branch and create a clean Week 3 branch from it:

```bash
# 1. Checkout main and reset to remote main
git checkout main
git reset --hard origin/main

# 2. Recreate your local feature branch
git branch -D feature/week3-exceptions
git checkout -b feature/week3-exceptions
```

---

## 💻 Step 2: Integrated Exception Handler Architecture

All custom exceptions are caught dynamically inside `src/main/java/com/ipl/auction/exception/GlobalExceptionHandler.java`:

- `ResourceNotFoundException` -> Returns HTTP 404 with error payload
- `BadRequestException` -> Returns HTTP 400 with error payload
- `InsufficientPurseException` -> Returns HTTP 400 with error payload
- `SquadLimitExceededException` -> Returns HTTP 400 with error payload
- `MethodArgumentNotValidException` -> Returns HTTP 400 with field-level binding errors

---

## 🧪 Step 3: Verification Steps (Manual Testing)

Run a Maven clean compilation from the root directory to verify the backend builds correctly:
```bash
.\mvnw.cmd clean compile
```

To test the exception handler:
1. Boot the application: `.\mvnw.cmd spring-boot:run` from the root directory.
2. Send the following `curl` requests to confirm the handler catches exceptions and returns the correct payload and HTTP status codes:

*   **Test Team Name Duplication (Should yield HTTP 400 Bad Request):**
    ```bash
    curl -X POST http://localhost:8082/api/teams \
      -H "Content-Type: application/json" \
      -d '{"name": "Chennai Super Kings", "shortName": "CSK", "totalPurse": 100000000}'
    ```
    *Response:* `"success": false, "message": "Team with name 'Chennai Super Kings' already exists"` with HTTP 400 status.

*   **Test Non-Existent Team Lookup (Should yield HTTP 404 Not Found):**
    ```bash
    curl -X GET http://localhost:8082/api/teams/999
    ```
    *Response:* `"success": false, "message": "Team not found with ID: 999"` with HTTP 404 status.

*   **Test Non-Existent Player Update (Should yield HTTP 404 Not Found):**
    ```bash
    curl -X PUT http://localhost:8082/api/players/999 \
      -H "Content-Type: application/json" \
      -d '{"name": "Non-existent", "category": "Batsman", "nationality": "Indian", "basePrice": 20000000}'
    ```
    *Response:* `"success": false, "message": "Player not found with ID: 999"` with HTTP 404 status.

---

## 🚀 Step 4: Commit & Push to GitHub

Once verified, commit and push your new feature branch:

```bash
git add .
git commit -m "feat: align branch with remote main and add Week 3 configuration guide"
git push -u origin feature/week3-exceptions --force
```

> [!IMPORTANT]
> Once you push this branch to GitHub, **reply to this chat message** to let me know so we can review the integration!

