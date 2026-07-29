function Leaderboard({ teams }) {
  const INITIAL_PURSE = 1000000000; // 100 Cr

  const getUtilizedPercent = (budget) => {
    const remaining = Number(budget) || 0;
    const utilized = INITIAL_PURSE - remaining;
    const pct = (utilized / INITIAL_PURSE) * 100;
    return Math.max(0, Math.min(100, pct)); // clamp between 0 and 100
  };

  return (
    <div className="leaderboard-card-glass">
      <h3>📊 Franchise Purse Leaderboard</h3>
      
      <div className="table-responsive">
        <table className="leaderboard-table">
          <thead>
            <tr>
              <th>Franchise</th>
              <th>Purse Left</th>
              <th>Utilization</th>
            </tr>
          </thead>
          <tbody>
            {teams.map((team) => {
              const utilPct = getUtilizedPercent(team.budget);
              return (
                <tr key={team.id}>
                  <td className="team-name-col">{team.name}</td>
                  <td className="team-purse-col">
                    ₹{(team.budget / 10000000).toFixed(2)} Cr
                  </td>
                  <td className="team-progress-col">
                    <div className="progress-bar-container">
                      <div
                        className="progress-bar-fill"
                        style={{ width: `${utilPct}%` }}
                      ></div>
                      <span className="progress-text">{utilPct.toFixed(0)}% Used</span>
                    </div>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default Leaderboard;
