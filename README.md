# 🏏 IPL Auction Software — Member 3: Database & Bidding Engine Developer Guide

> **Role Focus:** MySQL Schema Design, JPA Entities & Mappings, High-Throughput Live Bidding REST APIs, and Concurrency Control (Pessimistic & Optimistic Locking).

---

## 📅 Roadmap Overview (Weeks 1, 2 & 3 Completed)

```
┌──────────────────────────────────────────────┐
│  WEEK 1: Foundation, DB Schema & Project     │
│  • Git feature branch setup                  │
│  • MySQL DDL script & ER schema design       │
│  • HikariCP pool & JPA configuration         │
└──────────────────────┬───────────────────────┘
                       │
┌──────────────────────▼───────────────────────┐
│  WEEK 2: JPA Entities & Repositories         │
│  • Domain Entities (Team, Player, Bid, etc.) │
│  • Spring Data JPA Repositories              │
│  • Pessimistic Write Lock definitions        │
│  • Auto Data Seeders (10 IPL Teams & Players)│
└──────────────────────┬───────────────────────┘
                       │
┌──────────────────────▼───────────────────────┐
│  WEEK 3: Live Bidding Engine & REST APIs     │
│  • Atomic Bidding Engine Service             │
│  • IPL Slab & Purse Validation Rules         │
│  • Hammer Strike / Sold / Unsold Lifecycle   │
│  • REST Endpoints & Concurrency Test Suite   │
└──────────────────────────────────────────────┘
```

---

## 🛠️ Tech Stack
- **Language:** Java 17+
- **Framework:** Spring Boot 3.2.x (Spring Data JPA, Spring Web, Bean Validation)
- **Database:** MySQL 8.0+ (Production) / H2 In-Memory (Test/Dev)
- **Concurrency Control:** `LockModeType.PESSIMISTIC_WRITE` + `@Version` Optimistic Locking
- **API Documentation:** SpringDoc OpenAPI 3 (Swagger UI)
- **Build Tool:** Maven

---

## 🚀 Week 1: Database Schema & Git Setup

### 1. Git Feature Branch Setup
When contributing to the team repository, isolate all your database and bidding work inside your dedicated branch:

```bash
# Clone the repository (if not already done)
git clone <team-repo-url>
cd ipl-auction-software

# Create and switch to your feature branch
git checkout -b feature/database-bidding

# Verify your branch
git branch
```

### 2. MySQL Schema Architecture (`schema.sql`)
The MySQL schema is located at [src/main/resources/schema.sql](file:///c:/minor%20project/src/main/resources/schema.sql).

#### Key Tables & Indices:
1. **`teams`**: Stores franchise budget (₹100 Cr default), remaining purse, squad capacity (max 25, min 18), and foreign player quotas (max 8).
2. **`players`**: Stores base price, current bid price, role, overseas flag, auction set category, and winning team link.
3. **`auctions`**: Stores the auction event state (`SCHEDULED`, `LIVE`, `PAUSED`, `CONCLUDED`) and pointer to the current live player under the hammer.
4. **`bids`**: Append-only live bidding log. Indexed on `(player_id, bid_amount DESC)` for instant $O(1)$ top bid queries.
5. **`team_squad`**: Final roster mapping populated upon hammer strike.
6. **`wallet_audit_logs`**: Financial audit trail for tracking every rupee deducted or adjusted.

### 3. Database Connection Configuration (`application.yml`)
Located at [src/main/resources/application.yml](file:///c:/minor%20project/src/main/resources/application.yml).

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ipl_auction_db?createDatabaseIfNotExist=true
    username: root
    password: root
    hikari:
      maximum-pool-size: 25
      connection-timeout: 30000
```

---

## 📦 Week 2: JPA Entities, Mappings & Repositories

### 1. JPA Entity Relationships
- [Team.java](file:///c:/minor%20project/src/main/java/com/ipl/auction/entity/Team.java): Contains `@Version` and helper business methods (`hasSufficientPurse`, `calculateRequiredPurseReserve`, `canAddOverseasPlayer`).
- [Player.java](file:///c:/minor%20project/src/main/java/com/ipl/auction/entity/Player.java): Maps player details, category, and `@ManyToOne` link to the current winning team.
- [Auction.java](file:///c:/minor%20project/src/main/java/com/ipl/auction/entity/Auction.java): Manages the active auction session and current player on stage.
- [Bid.java](file:///c:/minor%20project/src/main/java/com/ipl/auction/entity/Bid.java): Maps bid history with `BidStatus` (`ACCEPTED`, `OUTBID`, `WINNING_BID`).
- [TeamSquad.java](file:///c:/minor%20project/src/main/java/com/ipl/auction/entity/TeamSquad.java): Mapping table ensuring each player belongs to at most one team squad (`player_id UNIQUE`).
- [WalletAuditLog.java](file:///c:/minor%20project/src/main/java/com/ipl/auction/entity/WalletAuditLog.java): Immutable financial ledger.

### 2. Concurrency Locking in Repositories
Located in [PlayerRepository.java](file:///c:/minor%20project/src/main/java/com/ipl/auction/repository/PlayerRepository.java) and [TeamRepository.java](file:///c:/minor%20project/src/main/java/com/ipl/auction/repository/TeamRepository.java):

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT p FROM Player p WHERE p.id = :id")
Optional<Player> findByIdWithPessimisticLock(@Param("id") Long id);
```
> **Why Pessimistic Locking?**
> When 5 franchises click the "Bid" paddle at the exact same millisecond, Pessimistic Write Lock issues `SELECT ... FOR UPDATE` in MySQL. The first transaction locks the player row, validates the slab and purse, updates the price, and commits. The next transactions queue up sequentially, preventing double-bids at the same price and race conditions.

---

## ⚡ Week 3: Live Bidding Engine, Rules & REST APIs

### 1. Live Bidding Engine Rules ([BiddingEngineService.java](file:///c:/minor%20project/src/main/java/com/ipl/auction/service/BiddingEngineService.java))
The engine validates:
1. **Auction State:** Must be `LIVE`.
2. **Player State:** Must be `IN_AUCTION`.
3. **Self-Outbidding Prevention:** A team holding the highest bid cannot bid against itself.
4. **Dynamic IPL Increment Slabs:**
   - `< ₹1.00 Crore`: **+ ₹10 Lakhs**
   - `₹1.00 Cr - ₹5.00 Cr`: **+ ₹20 Lakhs**
   - `₹5.00 Cr - ₹10.00 Cr`: **+ ₹25 Lakhs**
   - `> ₹10.00 Crore`: **+ ₹50 Lakhs**
5. **Team Purse Check:** `remainingPurse >= bidAmount`.
6. **Minimum Squad Reserve Rule:** Team must retain at least `(18 - squadCount) * ₹20 Lakhs` so they can complete the mandatory minimum squad of 18 players.
7. **Foreign Player Quota:** Max 8 overseas players per team.

---

### 2. REST API Endpoints

#### Swagger UI Documentation:
Run the application and navigate to: `http://localhost:8080/swagger-ui.html`

#### 📡 Live Bidding APIs (`/api/v1/bids`)
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/bids/place` | Place a real-time incremental bid (Thread-safe) |
| `GET` | `/api/v1/bids/player/{id}/current` | Get live price, winning team, and next minimum bid |
| `GET` | `/api/v1/bids/player/{id}/history` | Audit trail of all bids placed for a player |

#### 🔨 Auctioneer Operations (`/api/v1/auction`)
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/auction/stage` | Bring player to the auction stage (`IN_AUCTION`) |
| `POST` | `/api/v1/auction/players/{id}/hammer/sold` | Strike hammer: SOLD! Deducts purse, assigns squad |
| `POST` | `/api/v1/auction/players/{id}/hammer/unsold` | Pass player: UNSOLD |

#### 💰 Team Purse & Quotas (`/api/v1/teams`)
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/v1/teams/{id}/purse-summary` | Real-time purse balance, remaining slots, and max spendable |
| `GET` | `/api/v1/teams/purse-summary` | Summary table for all 10 franchises |

---

## 🧪 Testing & Verification Guide

### 1. Run All Automated Tests
```bash
mvn clean test
```
This executes:
- **`IplAuctionApplicationTests`**: Verifies context and JPA configurations.
- **`BiddingEngineServiceTest`**: Validates opening bids, increment slabs, self-outbidding prevention, overseas quota restrictions, and purse reserve rules.
- **`BiddingEngineConcurrencyTest`**: Multi-threaded simulation using `ExecutorService` and `CountDownLatch` where multiple franchises bid simultaneously on the same player, asserting zero race conditions.

---

### 2. Manual End-to-End API Walkthrough (cURL / Postman)

#### Step A: Bring Rishabh Pant (ID: 1) to the Auction Stage
```bash
curl -X POST http://localhost:8080/api/v1/auction/stage \
  -H "Content-Type: application/json" \
  -d '{"auctionId": 1, "playerId": 1}'
```

#### Step B: Place Opening Bid from CSK (Team ID: 1, Base Price: ₹2.00 Cr)
```bash
curl -X POST http://localhost:8080/api/v1/bids/place \
  -H "Content-Type: application/json" \
  -d '{"auctionId": 1, "playerId": 1, "teamId": 1, "bidAmount": 20000000}'
```

#### Step C: Place Next Counter-Bid from MI (Team ID: 2, Auto-calculated next bid: ₹2.20 Cr)
```bash
curl -X POST http://localhost:8080/api/v1/bids/place \
  -H "Content-Type: application/json" \
  -d '{"auctionId": 1, "playerId": 1, "teamId": 2}'
```

#### Step D: Check Current Live Floor State
```bash
curl http://localhost:8080/api/v1/bids/player/1/current
```

#### Step E: Auctioneer Strikes Hammer (SOLD to MI!)
```bash
curl -X POST "http://localhost:8080/api/v1/auction/players/1/hammer/sold?auctionId=1"
```

#### Step F: Verify Mumbai Indians Purse Summary (Purse deducted by ₹2.20 Cr)
```bash
curl http://localhost:8080/api/v1/teams/2/purse-summary
```

---

## 🌿 Git Commit & Push Instructions

Once you have verified the implementation, commit and push your work to GitHub:

```bash
git add .
git commit -m "feat(database-bidding): Complete Week 1, 2, and 3 schema, entities, and live bidding engine with concurrency locking"
git push origin feature/database-bidding
```
