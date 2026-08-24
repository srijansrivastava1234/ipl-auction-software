-- ==============================================================================
-- IPL AUCTION SOFTWARE - SEED DATA (data.sql)
-- Initial 10 Franchises, Marquee Players, and Active 2025 Mega Auction
-- ==============================================================================

-- 1. SEED TEAMS (10 Official IPL Franchises with ₹100 Crore Purse)
INSERT INTO teams (id, team_name, short_code, logo_url, total_purse, remaining_purse, max_squad_size, min_squad_size, max_foreign_players, current_squad_count, current_foreign_count, version)
VALUES 
(1, 'Chennai Super Kings', 'CSK', 'https://assets.ipl.com/csk.png', 1000000000, 1000000000, 25, 18, 8, 0, 0, 0),
(2, 'Mumbai Indians', 'MI', 'https://assets.ipl.com/mi.png', 1000000000, 1000000000, 25, 18, 8, 0, 0, 0),
(3, 'Royal Challengers Bengaluru', 'RCB', 'https://assets.ipl.com/rcb.png', 1000000000, 1000000000, 25, 18, 8, 0, 0, 0),
(4, 'Kolkata Knight Riders', 'KKR', 'https://assets.ipl.com/kkr.png', 1000000000, 1000000000, 25, 18, 8, 0, 0, 0),
(5, 'Rajasthan Royals', 'RR', 'https://assets.ipl.com/rr.png', 1000000000, 1000000000, 25, 18, 8, 0, 0, 0),
(6, 'Sunrisers Hyderabad', 'SRH', 'https://assets.ipl.com/srh.png', 1000000000, 1000000000, 25, 18, 8, 0, 0, 0),
(7, 'Delhi Capitals', 'DC', 'https://assets.ipl.com/dc.png', 1000000000, 1000000000, 25, 18, 8, 0, 0, 0),
(8, 'Lucknow Super Giants', 'LSG', 'https://assets.ipl.com/lsg.png', 1000000000, 1000000000, 25, 18, 8, 0, 0, 0),
(9, 'Gujarat Titans', 'GT', 'https://assets.ipl.com/gt.png', 1000000000, 1000000000, 25, 18, 8, 0, 0, 0),
(10, 'Punjab Kings', 'PBKS', 'https://assets.ipl.com/pbks.png', 1000000000, 1000000000, 25, 18, 8, 0, 0, 0);

-- 2. SEED PLAYERS (Marquee Set 1, Batsmen, Bowlers, All-rounders, Wicketkeepers)
INSERT INTO players (id, full_name, role, country, is_overseas, age, base_price, current_bid_price, current_winning_team_id, status, auction_set_category, photo_url, version)
VALUES
(1, 'Rishabh Pant', 'WICKET_KEEPER', 'India', FALSE, 27, 20000000, 0, NULL, 'AVAILABLE', 'MARQUEE_SET_1', 'https://assets.ipl.com/pant.png', 0),
(2, 'Shreyas Iyer', 'BATSMAN', 'India', FALSE, 29, 20000000, 0, NULL, 'AVAILABLE', 'MARQUEE_SET_1', 'https://assets.ipl.com/iyer.png', 0),
(3, 'Mitchell Starc', 'BOWLER', 'Australia', TRUE, 34, 20000000, 0, NULL, 'AVAILABLE', 'MARQUEE_SET_1', 'https://assets.ipl.com/starc.png', 0),
(4, 'Jos Buttler', 'WICKET_KEEPER', 'England', TRUE, 34, 20000000, 0, NULL, 'AVAILABLE', 'MARQUEE_SET_1', 'https://assets.ipl.com/buttler.png', 0),
(5, 'KL Rahul', 'BATSMAN', 'India', FALSE, 32, 20000000, 0, NULL, 'AVAILABLE', 'MARQUEE_SET_1', 'https://assets.ipl.com/rahul.png', 0),
(6, 'Arshdeep Singh', 'BOWLER', 'India', FALSE, 25, 20000000, 0, NULL, 'AVAILABLE', 'MARQUEE_SET_1', 'https://assets.ipl.com/arshdeep.png', 0),
(7, 'David Miller', 'BATSMAN', 'South Africa', TRUE, 35, 15000000, 0, NULL, 'AVAILABLE', 'BATSMAN_SET_1', 'https://assets.ipl.com/miller.png', 0),
(8, 'Yuzvendra Chahal', 'BOWLER', 'India', FALSE, 34, 20000000, 0, NULL, 'AVAILABLE', 'SPINNER_SET_1', 'https://assets.ipl.com/chahal.png', 0),
(9, 'Liam Livingstone', 'ALL_ROUNDER', 'England', TRUE, 31, 20000000, 0, NULL, 'AVAILABLE', 'ALLROUNDER_SET_1', 'https://assets.ipl.com/livingstone.png', 0),
(10, 'Mohammed Shami', 'BOWLER', 'India', FALSE, 34, 20000000, 0, NULL, 'AVAILABLE', 'PACER_SET_1', 'https://assets.ipl.com/shami.png', 0),
(11, 'Glenn Maxwell', 'ALL_ROUNDER', 'Australia', TRUE, 36, 20000000, 0, NULL, 'AVAILABLE', 'ALLROUNDER_SET_1', 'https://assets.ipl.com/maxwell.png', 0),
(12, 'Washington Sundar', 'ALL_ROUNDER', 'India', FALSE, 25, 10000000, 0, NULL, 'AVAILABLE', 'ALLROUNDER_SET_1', 'https://assets.ipl.com/sundar.png', 0),
(13, 'Rachin Ravindra', 'ALL_ROUNDER', 'New Zealand', TRUE, 25, 15000000, 0, NULL, 'AVAILABLE', 'ALLROUNDER_SET_1', 'https://assets.ipl.com/rachin.png', 0),
(14, 'Marcus Stoinis', 'ALL_ROUNDER', 'Australia', TRUE, 35, 20000000, 0, NULL, 'AVAILABLE', 'ALLROUNDER_SET_1', 'https://assets.ipl.com/stoinis.png', 0),
(15, 'Quinton de Kock', 'WICKET_KEEPER', 'South Africa', TRUE, 32, 20000000, 0, NULL, 'AVAILABLE', 'WICKETKEEPER_SET_1', 'https://assets.ipl.com/qdk.png', 0);

-- 3. SEED ACTIVE AUCTION EVENT
INSERT INTO auctions (id, title, year, status, current_player_id, start_time, end_time)
VALUES
(1, 'IPL 2025 Mega Auction', 2025, 'LIVE', NULL, CURRENT_TIMESTAMP, NULL);
