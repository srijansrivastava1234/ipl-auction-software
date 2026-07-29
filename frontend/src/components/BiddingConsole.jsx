import { useState } from 'react';

function BiddingConsole({
  user,
  player,
  onPlaceBid,
  onSellPlayer,
  onPrevPlayer,
  onNextPlayer,
  currentIndex,
  totalPlayers
}) {
  const [customBidStr, setCustomBidStr] = useState('');

  if (!player) return null;

  const isSold = player.status === 'SOLD';
  const isAdmin = user.role === 'ADMIN';
  const isOwner = user.role === 'TEAM_OWNER';

  const handleCustomSubmit = (e) => {
    e.preventDefault();
    const val = Number(customBidStr);
    if (!val || isNaN(val)) return;
    onPlaceBid(val, false); // false indicates absolute amount
    setCustomBidStr('');
  };

  return (
    <div className="console-container">
      {/* Auctioneer Stage Controls */}
      {isAdmin && (
        <div className="console-panel auctioneer-panel">
          <h3>🎙️ Auctioneer Dashboard</h3>
          
          <div className="stage-navigation">
            <button
              onClick={onPrevPlayer}
              disabled={currentIndex === 0}
              className="nav-button prev-button"
            >
              ◀ Previous Player
            </button>
            <span className="stage-indicator">
              {currentIndex + 1} / {totalPlayers}
            </span>
            <button
              onClick={onNextPlayer}
              disabled={currentIndex === totalPlayers - 1}
              className="nav-button next-button"
            >
              Next Player ▶
            </button>
          </div>

          {!isSold ? (
            <button
              onClick={onSellPlayer}
              className="action-button sell-button"
            >
              🔨 Hammer Down (Mark SOLD)
            </button>
          ) : (
            <div className="auctioneer-msg success">
              ✅ Player successfully sold to {player.team?.name}
            </div>
          )}
        </div>
      )}

      {/* Franchise Owner Console */}
      {isOwner && (
        <div className="console-panel owner-panel">
          <h3>🏏 Franchise Bidding Desk</h3>
          
          {!isSold ? (
            <div className="bidding-actions">
              <p className="bidding-hint">
                As owner of <strong>{user.teamName}</strong>, raise the current price:
              </p>
              
              <div className="increment-row">
                <button
                  onClick={() => onPlaceBid(2000000, true)} // true indicates incremental
                  className="bid-increment-btn l20"
                >
                  Raise +₹20L
                </button>
                <button
                  onClick={() => onPlaceBid(5000000, true)}
                  className="bid-increment-btn l50"
                >
                  Raise +₹50L
                </button>
                <button
                  onClick={() => onPlaceBid(10000000, true)}
                  className="bid-increment-btn c1"
                >
                  Raise +₹1Cr
                </button>
              </div>

              <form onSubmit={handleCustomSubmit} className="custom-bid-form">
                <input
                  type="number"
                  placeholder="Enter custom total bid (e.g. 25000000)"
                  value={customBidStr}
                  onChange={(e) => setCustomBidStr(e.target.value)}
                  className="custom-bid-input"
                />
                <button type="submit" className="custom-bid-submit">
                  Submit Bid
                </button>
              </form>
            </div>
          ) : (
            <div className="bidding-msg finished">
              🔒 Bidding is closed. Player acquired by {player.team?.name}.
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export default BiddingConsole;
