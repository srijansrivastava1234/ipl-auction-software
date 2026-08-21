/**
 * Squad & Purse Analytics Controller
 * Member 4 - Frontend & API Integration Lead
 */

const SquadController = {
    // Default initial team purse data
    teamPurseData: {
        "Royal Challengers Bengaluru": { total: 120.0, spent: 77.5, players: 16, overseas: 5 },
        "Chennai Super Kings": { total: 120.0, spent: 65.0, players: 18, overseas: 6 },
        "Mumbai Indians": { total: 120.0, spent: 82.0, players: 15, overseas: 4 },
        "Kolkata Knight Riders": { total: 120.0, spent: 55.0, players: 17, overseas: 5 },
        "Sunrisers Hyderabad": { total: 120.0, spent: 70.0, players: 14, overseas: 6 }
    },

    selectedTeam: "Royal Challengers Bengaluru",

    init() {
        if (typeof document === 'undefined') return;
        
        const teamSelect = document.getElementById('squad-team-select');
        if (teamSelect) {
            teamSelect.addEventListener('change', (e) => {
                this.selectedTeam = e.target.value;
                this.renderMetrics();
            });
        }

        this.renderMetrics();
    },

    /**
     * Render Financial Purse & Squad count cards
     */
    renderMetrics() {
        const teamData = this.teamPurseData[this.selectedTeam] || { total: 120.0, spent: 0, players: 0, overseas: 0 };
        const remaining = (teamData.total - teamData.spent).toFixed(2);

        const purseEl = document.getElementById('squad-remaining-purse');
        const spentEl = document.getElementById('squad-spent-purse');
        const squadCountEl = document.getElementById('squad-player-count');
        const overseasCountEl = document.getElementById('squad-overseas-count');
        const teamHeaderEl = document.getElementById('squad-team-header');

        if (purseEl) purseEl.textContent = `₹ ${remaining} Cr`;
        if (spentEl) spentEl.textContent = `Total Spent: ₹ ${teamData.spent.toFixed(2)} Cr / ${teamData.total.toFixed(2)} Cr`;
        if (squadCountEl) squadCountEl.textContent = `${teamData.players} / 25`;
        if (overseasCountEl) overseasCountEl.textContent = `${teamData.overseas} / 8`;
        if (teamHeaderEl) teamHeaderEl.textContent = `${this.selectedTeam} - Financial & Squad Overview`;
    },

    /**
     * Record purchase from Live Auction controller
     */
    recordPurchase(teamName, player, priceInCr) {
        if (!this.teamPurseData[teamName]) {
            this.teamPurseData[teamName] = { total: 120.0, spent: 0, players: 0, overseas: 0 };
        }

        this.teamPurseData[teamName].spent += priceInCr;
        this.teamPurseData[teamName].players += 1;
        if (player.isOverseas || (player.country && player.country !== 'India')) {
            this.teamPurseData[teamName].overseas += 1;
        }

        if (this.selectedTeam === teamName) {
            this.renderMetrics();
        }
    }
};

if (typeof window !== 'undefined') {
    window.SquadController = SquadController;
    document.addEventListener('DOMContentLoaded', () => SquadController.init());
}

if (typeof module !== 'undefined') {
    module.exports = SquadController;
}
