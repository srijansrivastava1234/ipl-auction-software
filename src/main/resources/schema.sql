-- ==============================================================================
-- IPL AUCTION SOFTWARE - DATABASE SCHEMA (MySQL 8.0+)
-- Member 3: Database & Bidding Engine Developer
-- ==============================================================================

-- Drop tables in reverse dependency order if needed for clean re-run
DROP TABLE IF EXISTS wallet_audit_logs;
DROP TABLE IF EXISTS bids;
DROP TABLE IF EXISTS team_squad;
DROP TABLE IF EXISTS auctions;
DROP TABLE IF EXISTS players;
DROP TABLE IF EXISTS teams;

-- ==============================================================================
-- 1. TEAMS TABLE (Franchises participating in the Auction)
-- ==============================================================================
CREATE TABLE teams (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    team_name VARCHAR(100) NOT NULL UNIQUE,
    short_code VARCHAR(10) NOT NULL UNIQUE,
    logo_url VARCHAR(255),
    total_purse BIGINT NOT NULL DEFAULT 1000000000,          -- Default ₹100 Crore in INR
    remaining_purse BIGINT NOT NULL DEFAULT 1000000000,      -- Decremented on successful bids
    max_squad_size INT NOT NULL DEFAULT 25,                 -- IPL standard: 18 - 25 players
    min_squad_size INT NOT NULL DEFAULT 18,
    max_foreign_players INT NOT NULL DEFAULT 8,             -- IPL standard: max 8 overseas
    current_squad_count INT NOT NULL DEFAULT 0,
    current_foreign_count INT NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0,                      -- Optimistic Concurrency Control
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_team_purse CHECK (remaining_purse >= 0),
    CONSTRAINT chk_team_squad_capacity CHECK (current_squad_count <= max_squad_size),
    CONSTRAINT chk_team_foreign_capacity CHECK (current_foreign_count <= max_foreign_players)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================================
-- 2. PLAYERS TABLE (Cricketers available for the Auction)
-- ==============================================================================
CREATE TABLE players (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    role VARCHAR(30) NOT NULL,                              -- BATSMAN, BOWLER, ALL_ROUNDER, WICKET_KEEPER
    country VARCHAR(60) NOT NULL DEFAULT 'India',
    is_overseas BOOLEAN NOT NULL DEFAULT FALSE,
    age INT,
    base_price BIGINT NOT NULL,                             -- In INR (e.g. 20000000 = ₹2 Cr, 5000000 = ₹50 Lakh)
    current_bid_price BIGINT DEFAULT 0,                     -- Highest active bid
    current_winning_team_id BIGINT,                         -- Team currently holding the highest bid
    status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',        -- AVAILABLE, IN_AUCTION, SOLD, UNSOLD, WITHDRAWN
    auction_set_category VARCHAR(50),                      -- e.g., 'MARQUEE_SET_1', 'BATSMAN_SET_1'
    photo_url VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0,                      -- Optimistic Concurrency Control
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_players_winning_team FOREIGN KEY (current_winning_team_id) REFERENCES teams(id) ON DELETE SET NULL,
    CONSTRAINT chk_player_base_price CHECK (base_price > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Indexing for fast player lookups by status and set
CREATE INDEX idx_players_status ON players(status);
CREATE INDEX idx_players_category ON players(auction_set_category);

-- ==============================================================================
-- 3. AUCTIONS TABLE (Auction Event Session)
-- ==============================================================================
CREATE TABLE auctions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,                            -- e.g., 'IPL 2025 Mega Auction'
    year INT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED',        -- SCHEDULED, LIVE, PAUSED, CONCLUDED
    current_player_id BIGINT,                               -- Currently active player under the hammer
    start_time TIMESTAMP NULL,
    end_time TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_auctions_current_player FOREIGN KEY (current_player_id) REFERENCES players(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================================
-- 4. BIDS TABLE (Live Bidding Audit Log & Stream)
-- ==============================================================================
CREATE TABLE bids (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    auction_id BIGINT NOT NULL,
    player_id BIGINT NOT NULL,
    team_id BIGINT NOT NULL,
    bid_amount BIGINT NOT NULL,
    bid_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    bid_status VARCHAR(30) NOT NULL DEFAULT 'ACCEPTED',     -- ACCEPTED, OUTBID, WINNING_BID, REJECTED
    rejection_reason VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_bids_auction FOREIGN KEY (auction_id) REFERENCES auctions(id) ON DELETE CASCADE,
    CONSTRAINT fk_bids_player FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE,
    CONSTRAINT fk_bids_team FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE,
    CONSTRAINT chk_bid_amount CHECK (bid_amount > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Performance Critical Indexes for High-Concurrency Bidding Lookups
CREATE INDEX idx_bids_player_amount ON bids(player_id, bid_amount DESC);
CREATE INDEX idx_bids_auction_player ON bids(auction_id, player_id);
CREATE INDEX idx_bids_team ON bids(team_id);
CREATE INDEX idx_bids_timestamp ON bids(bid_timestamp DESC);

-- ==============================================================================
-- 5. TEAM_SQUAD TABLE (Sold Player Roster Mapping)
-- ==============================================================================
CREATE TABLE team_squad (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    team_id BIGINT NOT NULL,
    player_id BIGINT NOT NULL UNIQUE,                       -- Each player can only be in ONE squad
    sold_price BIGINT NOT NULL,
    auction_id BIGINT NOT NULL,
    acquired_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_squad_team FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE,
    CONSTRAINT fk_squad_player FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE,
    CONSTRAINT fk_squad_auction FOREIGN KEY (auction_id) REFERENCES auctions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_squad_team ON team_squad(team_id);

-- ==============================================================================
-- 6. WALLET_AUDIT_LOGS TABLE (Purse & Financial Audit Trail)
-- ==============================================================================
CREATE TABLE wallet_audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    team_id BIGINT NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,                  -- PURSE_CREDIT, PURSE_FINAL_DEDUCTION, PURSE_ADJUSTMENT
    amount BIGINT NOT NULL,
    balance_before BIGINT NOT NULL,
    balance_after BIGINT NOT NULL,
    reference_player_id BIGINT NULL,
    description VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_wallet_team FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE,
    CONSTRAINT fk_wallet_player FOREIGN KEY (reference_player_id) REFERENCES players(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_wallet_team_date ON wallet_audit_logs(team_id, created_at DESC);
