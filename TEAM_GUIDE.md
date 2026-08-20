# Team Collaboration & Roles Guide

This directory structure divides the **IPL Auction System** project among 5 team members based on their specific roles. Each member has their own assigned folder under the `/team` directory containing their dashboard instructions, directory targets, and responsibilities.

---

## 👥 Team Member Roles & Dashboards

1. **[Member 1: Real-Time Configuration & Security (You)](file:///c:/Users/hp/ipl-auction-system/team/member-1-config/README.md)**
   - **GitHub Handle:** `@srijansrivastava1234`
   - **Target Folder:** `backend/src/main/java/com/ipl/auction/config/`
   - **Responsibilities:** Security, Custom JWT filters, WebSocket interceptors, backend properties.

2. **[Member 2: Backend Controller & Service APIs](file:///c:/Users/hp/ipl-auction-system/team/member-2-backend-apis/README.md)**
   - **GitHub Handle:** `@amitkumarrajput1133-oss`
   - **Target Folders:** `backend/src/main/java/com/ipl/auction/controller/` and `backend/src/main/java/com/ipl/auction/service/`
   - **Responsibilities:** API Controllers, Business Logic validation rules (bids, budget limits, user profiles).

3. **[Member 3: Database Schema & JPA Repositories](file:///c:/Users/hp/ipl-auction-system/team/member-3-database/README.md)**
   - **GitHub Handle:** `@sharmaakhilesh8273-lgtm`
   - **Target Folders:** `backend/src/main/java/com/ipl/auction/model/` and `backend/src/main/java/com/ipl/auction/repository/`
   - **Responsibilities:** JPA entity schema design, query definitions, pessimistic write locks, DB local data initializer.

4. **[Member 4: Frontend UI Components & CSS](file:///c:/Users/hp/ipl-auction-system/team/member-4-frontend-ui/README.md)**
   - **GitHub Handle:** `@anshikapandey-bit`
   - **Target Folders:** `frontend/src/components/` and style files (`index.css`, `App.css`)
   - **Responsibilities:** Designing modular React components (`Login`, `Leaderboard`, `PlayerCard`, `BiddingConsole`), aesthetic styling.

5. **[Member 5: Frontend State & WebSockets Integration](file:///c:/Users/hp/ipl-auction-system/team/member-5-frontend-sockets/README.md)**
   - **GitHub Handle:** `@suryansh-svg`
   - **Target Files:** `frontend/src/App.jsx` (Shared Integration), custom React hooks/context.
   - **Responsibilities:** WebSocket connections, STOMP client integrations, real-time message handling, session cache storage.

---

## 🛠️ Git Collaboration Workflow
To prevent conflicts while working simultaneously:
- **Work on branches:** Each member should create and commit to their own branch (e.g. `feature/websocket-auth`, `feature/ui-adjustments`).
- **Submit Pull Requests:** When a task is complete, create a Pull Request on GitHub targeting `main`.
- **CODEOWNERS:** The repository is configured with a `.github/CODEOWNERS` file. Pushing modifications to specific paths will automatically request reviews from the assigned owner.
