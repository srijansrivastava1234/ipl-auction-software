/**
 * Live Auction Room Controller - Real-Time Bidding Simulator
 * Member 4 - Frontend & API Integration Lead
 */

const AuctionController = {
    // Current Active Player state
    activePlayer: {
        id: 101,
        name: "Virat Kohli",
        role: "Batter",
        country: "India",
        basePriceVal: 2.0, // in Crores
        basePriceStr: "₹ 2.00 Cr",
        set: "Set 1 - Marquee Batsman"
    },

    currentBidVal: 14.5, // in Crores
    currentBidTeam: "Royal Challengers Bengaluru",
    bidStatus: "ACTIVE", // ACTIVE, SOLD, UNSOLD
    bidHistory: [
        { team: "Chennai Super Kings", amount: "₹ 14.00 Cr", time: "14:02:10", isLatest: false },
        { team: "Royal Challengers Bengaluru", amount: "₹ 14.50 Cr", time: "14:03:45", isLatest: true }
    ],

    teams: [
        "Royal Challengers Bengaluru",
        "Chennai Super Kings",
        "Mumbai Indians",
        "Kolkata Knight Riders",
        "Sunrisers Hyderabad",
        "Gujarat Titans",
        "Delhi Capitals",
        "Rajasthan Royals",
        "Punjab Kings",
        "Lucknow Super Giants"
    ],

    init() {
        if (typeof document === 'undefined') return;
        this.renderPodium();
        this.renderBidHistory();
    },

    /**
     * Render Podium Player details
     */
    renderPodium() {
        const nameEl = document.getElementById('auction-player-name');
        const roleEl = document.getElementById('auction-player-role');
        const basePriceEl = document.getElementById('auction-player-base');
        const currentBidEl = document.getElementById('auction-current-bid');
        const biddingTeamEl = document.getElementById('auction-bidding-team');
        const setBadgeEl = document.getElementById('auction-player-set');
        const statusBadgeEl = document.getElementById('auction-status-badge');

        if (nameEl) nameEl.textContent = this.activePlayer.name;
        if (roleEl) roleEl.textContent = `${this.activePlayer.country} • ${this.activePlayer.role}`;
        if (basePriceEl) basePriceEl.textContent = this.activePlayer.basePriceStr;
        if (currentBidEl) currentBidEl.textContent = `₹ ${this.currentBidVal.toFixed(2)} Cr`;
        if (biddingTeamEl) biddingTeamEl.textContent = `Held by: ${this.currentBidTeam}`;
        if (setBadgeEl) setBadgeEl.textContent = this.activePlayer.set;

        if (statusBadgeEl) {
            statusBadgeEl.textContent = this.bidStatus === 'ACTIVE' ? 'LIVE BIDDING' : this.bidStatus;
            statusBadgeEl.className = `status-badge ${this.bidStatus === 'SOLD' ? 'success' : this.bidStatus === 'UNSOLD' ? '' : 'gold'}`;
        }
    },

    /**
     * Render scrolling live bid history feed
     */
    renderBidHistory() {
        const historyContainer = document.getElementById('auction-bid-history');
        if (!historyContainer) return;

        if (this.bidHistory.length === 0) {
            historyContainer.innerHTML = `<div style="font-size: 12px; color: var(--text-muted);">No bids placed yet for this player.</div>`;
            return;
        }

        historyContainer.innerHTML = this.bidHistory.map((item, idx) => `
            <div class="bid-item ${idx === 0 ? 'latest' : ''}">
                <span class="bid-team-badge">🏏 ${item.team}</span>
                <span class="bid-amount">${item.amount}</span>
                <span class="bid-time">${item.time}</span>
            </div>
        `).join('');
    },

    /**
     * Raise bid handler
     * @param {number} increment - Amount in Crores (0.2 = 20L, 0.5 = 50L, 1.0 = 1Cr)
     */
    async raiseBid(increment = 0.5) {
        if (this.bidStatus !== 'ACTIVE') {
            if (window.Toast) window.Toast.show(`Bidding is closed for ${this.activePlayer.name} (${this.bidStatus})`, 'warning');
            return;
        }

        // Get stored active team or default user team
        const user = window.AuthService ? window.AuthService.getUser() : null;
        const activeTeam = (user && user.team) ? user.team : "Mumbai Indians";

        this.currentBidVal += increment;
        this.currentBidTeam = activeTeam;

        const timestamp = new Date().toLocaleTimeString();
        const formattedAmount = `₹ ${this.currentBidVal.toFixed(2)} Cr`;

        const newBid = {
            team: activeTeam,
            amount: formattedAmount,
            time: timestamp,
            isLatest: true
        };

        this.bidHistory.unshift(newBid);

        // Attempt API endpoint trigger via interceptor
        try {
            const apiClient = typeof window !== 'undefined' ? window.api : null;
            if (apiClient) {
                await apiClient.post(CONFIG.ENDPOINTS.AUCTION.BID, {
                    playerId: this.activePlayer.id,
                    team: activeTeam,
                    bidAmount: this.currentBidVal
                });
            }
        } catch (err) {
            // Mock mode fallback
        }

        this.renderPodium();
        this.renderBidHistory();

        if (window.Toast) window.Toast.show(`New highest bid: ${formattedAmount} by ${activeTeam}!`, 'success');
    },

    /**
     * Mark player as SOLD
     */
    markSold() {
        if (this.bidStatus === 'SOLD') return;

        this.bidStatus = 'SOLD';
        this.renderPodium();

        if (window.Toast) window.Toast.show(`🎉 HAMMER DOWN! ${this.activePlayer.name} SOLD to ${this.currentBidTeam} for ₹ ${this.currentBidVal.toFixed(2)} Cr!`, 'success', 5000);

        // Notify Squad Controller to update purse
        if (window.SquadController && window.SquadController.recordPurchase) {
            window.SquadController.recordPurchase(this.currentBidTeam, this.activePlayer, this.currentBidVal);
        }
    },

    /**
     * Mark player as UNSOLD
     */
    markUnsold() {
        this.bidStatus = 'UNSOLD';
        this.renderPodium();
        if (window.Toast) window.Toast.show(`${this.activePlayer.name} passed and marked UNSOLD.`, 'info');
    },

    /**
     * Load next player onto podium
     */
    nextPlayer() {
        const pool = (window.AdminController && window.AdminController.players) ? window.AdminController.players : [];
        if (pool.length > 0) {
            const randomPlayer = pool[Math.floor(Math.random() * pool.length)];
            const baseVal = parseFloat(randomPlayer.basePrice.replace(/[^0-9.]/g, '')) || 1.0;
            
            this.activePlayer = {
                id: randomPlayer.id,
                name: randomPlayer.name,
                role: randomPlayer.role,
                country: randomPlayer.country,
                basePriceVal: baseVal,
                basePriceStr: randomPlayer.basePrice,
                set: randomPlayer.set || "Set 1 - Indian"
            };
            this.currentBidVal = baseVal;
            this.currentBidTeam = "Base Price";
            this.bidStatus = "ACTIVE";
            this.bidHistory = [{ team: "Starting Base", amount: randomPlayer.basePrice, time: new Date().toLocaleTimeString() }];
            
            this.renderPodium();
            this.renderBidHistory();
            if (window.Toast) window.Toast.show(`Now on Podium: ${this.activePlayer.name} (${this.activePlayer.basePriceStr})`, 'info');
        }
    }
};

if (typeof window !== 'undefined') {
    window.AuctionController = AuctionController;
    document.addEventListener('DOMContentLoaded', () => AuctionController.init());
}

if (typeof module !== 'undefined') {
    module.exports = AuctionController;
}
