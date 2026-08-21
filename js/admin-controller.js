/**
 * Admin Dashboard Controller - Player & Team Onboarding Management
 * Member 4 - Frontend & API Integration Lead
 */

const AdminController = {
    // Shared in-memory player registry
    players: [
        { id: 101, name: "Virat Kohli", role: "Batter", basePrice: "₹ 2.00 Cr", country: "India", isOverseas: false, status: "Active Podium", set: "Set 1 - Marquee" },
        { id: 102, name: "Jasprit Bumrah", role: "Bowler", basePrice: "₹ 2.00 Cr", country: "India", isOverseas: false, status: "Available", set: "Set 1 - Marquee" },
        { id: 103, name: "Travis Head", role: "Batter", basePrice: "₹ 2.00 Cr", country: "Australia", isOverseas: true, status: "Available", set: "Set 1 - Marquee" },
        { id: 104, name: "Heinrich Klaasen", role: "Wicket-Keeper", basePrice: "₹ 1.50 Cr", country: "South Africa", isOverseas: true, status: "Available", set: "Set 2 - Overseas WK" },
        { id: 105, name: "Rishabh Pant", role: "Wicket-Keeper", basePrice: "₹ 2.00 Cr", country: "India", isOverseas: false, status: "Available", set: "Set 2 - Indian WK" },
        { id: 106, name: "Hardik Pandya", role: "All-Rounder", basePrice: "₹ 2.00 Cr", country: "India", isOverseas: false, status: "Available", set: "Set 3 - All-Rounder" }
    ],

    init() {
        if (typeof document === 'undefined') return;
        this.renderPlayerTable();

        const addPlayerForm = document.getElementById('add-player-form');
        if (addPlayerForm) {
            addPlayerForm.addEventListener('submit', (e) => this.handleAddPlayer(e));
        }

        const purseConfigForm = document.getElementById('purse-config-form');
        if (purseConfigForm) {
            purseConfigForm.addEventListener('submit', (e) => this.handlePurseConfig(e));
        }
    },

    /**
     * Render registered player management table
     */
    renderPlayerTable() {
        const tableBody = document.getElementById('admin-player-tbody');
        if (!tableBody) return;

        if (this.players.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="7" style="text-align: center; color: var(--text-muted);">No players in auction pool. Add a player above.</td></tr>`;
            return;
        }

        tableBody.innerHTML = this.players.map((p, idx) => `
            <tr>
                <td><strong>#${p.id}</strong></td>
                <td>
                    <div style="font-weight: 600;">${this.escapeHtml(p.name)}</div>
                    <div style="font-size: 11px; color: var(--text-muted);">${p.set}</div>
                </td>
                <td><span class="card-tag" style="background: rgba(58, 134, 255, 0.15); color: var(--ipl-electric-blue);">${p.role}</span></td>
                <td>${p.country} ${p.isOverseas ? '✈️' : '🇮🇳'}</td>
                <td style="font-weight: 700; color: var(--ipl-gold);">${p.basePrice}</td>
                <td>
                    <span class="status-badge ${p.status === 'Active Podium' ? 'gold' : p.status === 'Sold' ? 'success' : ''}">
                        ${p.status}
                    </span>
                </td>
                <td>
                    <button class="btn btn-outline btn-sm" onclick="AdminController.deletePlayer(${p.id})">🗑️ Remove</button>
                </td>
            </tr>
        `).join('');
    },

    /**
     * Add player form submit handler
     */
    async handleAddPlayer(e) {
        if (e) e.preventDefault();

        const nameEl = document.getElementById('player-name');
        const roleEl = document.getElementById('player-role');
        const priceEl = document.getElementById('player-base-price');
        const countryEl = document.getElementById('player-country');
        const overseasEl = document.getElementById('player-overseas');

        const name = nameEl ? nameEl.value.trim() : '';
        const role = roleEl ? roleEl.value : 'Batter';
        const basePrice = priceEl ? priceEl.value : '₹ 1.00 Cr';
        const country = countryEl ? countryEl.value.trim() : 'India';
        const isOverseas = overseasEl ? overseasEl.checked : false;

        if (!name) {
            if (window.Toast) window.Toast.show('Please enter player name', 'danger');
            return;
        }

        const newPlayer = {
            id: 100 + this.players.length + 1,
            name: name,
            role: role,
            basePrice: basePrice,
            country: country || (isOverseas ? 'Foreign' : 'India'),
            isOverseas: isOverseas,
            status: 'Available',
            set: isOverseas ? 'Set 2 - Overseas' : 'Set 1 - Indian'
        };

        try {
            const apiClient = typeof window !== 'undefined' ? window.api : null;
            if (apiClient) {
                await apiClient.post(CONFIG.ENDPOINTS.ADMIN.PLAYERS, newPlayer);
            }
        } catch (err) {
            // In-memory fallback
        }

        this.players.push(newPlayer);
        this.renderPlayerTable();

        if (nameEl) nameEl.value = '';
        if (countryEl) countryEl.value = '';
        if (overseasEl) overseasEl.checked = false;

        if (window.Toast) window.Toast.show(`Player "${name}" added to auction pool!`, 'success');
        
        // Notify auction controller if available
        if (window.AuctionController && window.AuctionController.updatePlayerPool) {
            window.AuctionController.updatePlayerPool(this.players);
        }
    },

    /**
     * Delete player from pool
     */
    deletePlayer(id) {
        const idx = this.players.findIndex(p => p.id === id);
        if (idx !== -1) {
            const removed = this.players.splice(idx, 1)[0];
            this.renderPlayerTable();
            if (window.Toast) window.Toast.show(`Removed "${removed.name}" from pool`, 'warning');
            
            if (window.AuctionController && window.AuctionController.updatePlayerPool) {
                window.AuctionController.updatePlayerPool(this.players);
            }
        }
    },

    /**
     * Purse configuration handler
     */
    handlePurseConfig(e) {
        if (e) e.preventDefault();
        const purseLimitEl = document.getElementById('purse-limit');
        const purseVal = purseLimitEl ? purseLimitEl.value : '120';
        
        if (window.Toast) window.Toast.show(`Franchise Purse cap set to ₹ ${purseVal} Cr`, 'success');
    },

    escapeHtml(str) {
        return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
    }
};

if (typeof window !== 'undefined') {
    window.AdminController = AdminController;
    document.addEventListener('DOMContentLoaded', () => AdminController.init());
}

if (typeof module !== 'undefined') {
    module.exports = AdminController;
}
