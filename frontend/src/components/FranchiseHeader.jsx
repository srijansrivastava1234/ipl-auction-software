function FranchiseHeader({ user, activeTeam, totalPlayers, currentIndex, onLogout }) {
  return (
    <header className="app-header">
      <div className="header-brand">
        <span className="brand-icon">🏟️</span>
        <div>
          <h1 className="brand-title">IPL Live Auction Arena</h1>
          <p className="brand-subtitle">
            Live Stream • Player {totalPlayers > 0 ? currentIndex + 1 : 0} of {totalPlayers}
          </p>
        </div>
      </div>

      <div className="user-profile-widget">
        <div className="user-details">
          <span className="user-role-badge">
            {user.role === 'ADMIN' ? '🎙️ Auctioneer' : '💼 Franchise Owner'}
          </span>
          <span className="username-display">{user.username}</span>
          {user.teamName && <span className="team-display">({user.teamName})</span>}
        </div>

        {user.role === 'TEAM_OWNER' && activeTeam && (
          <div className="purse-badge">
            <span className="purse-label">Purse Left</span>
            <span className="purse-amount">₹{activeTeam.budget?.toLocaleString('en-IN')}</span>
          </div>
        )}

        <button onClick={onLogout} className="logout-button">
          Leave Arena
        </button>
      </div>
    </header>
  );
}

export default FranchiseHeader;
