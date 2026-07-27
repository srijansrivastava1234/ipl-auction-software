import { useEffect, useState } from 'react';
import axios from 'axios';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

function App() {
  const [players, setPlayers] = useState([]);
  const [teams, setTeams] = useState([]);
  const [selectedTeamId, setSelectedTeamId] = useState('');
  const [currentIndex, setCurrentIndex] = useState(0);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState('');
  const [customBids, setCustomBids] = useState({});

  useEffect(() => {
    fetchData();

    // Initialize STOMP client over SockJS
    const stompClient = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8082/ws-auction'),
      reconnectDelay: 5000,
      onConnect: () => {
        stompClient.subscribe('/topic/bids', (msg) => {
          const newBid = JSON.parse(msg.body);
          setMessage(`🔥 Live Bid! ${newBid.team.name} raised bid to ₹${newBid.amount.toLocaleString('en-IN')}!`);
          fetchData();
        });

        stompClient.subscribe('/topic/players', (msg) => {
          const soldPlayer = JSON.parse(msg.body);
          setMessage(`🎉 HAMMER DOWN! ${soldPlayer.name} SOLD to ${soldPlayer.team.name}!`);
          fetchData();
        });
      },
      onStompError: (frame) => {
        console.warn('STOMP error:', frame);
      }
    });

    stompClient.activate();

    return () => {
      stompClient.deactivate();
    };
  }, []);

  const fetchData = async () => {
    try {
      const [playersRes, teamsRes] = await Promise.all([
        axios.get('http://localhost:8082/api/players'),
        axios.get('http://localhost:8082/api/teams')
      ]);
      setPlayers(playersRes.data);
      setTeams(teamsRes.data);
      if (teamsRes.data.length > 0 && !selectedTeamId) {
        setSelectedTeamId(teamsRes.data[0].id.toString());
      }
      setLoading(false);
    } catch (error) {
      console.error('Error fetching data:', error);
      setMessage('⚠️ Could not connect to Spring Boot server.');
      setLoading(false);
    }
  };

  const activeTeam = teams.find(t => t.id.toString() === selectedTeamId);
  const currentPlayer = players[currentIndex];

  const handleBidInputChange = (playerId, value) => {
    setCustomBids(prev => ({ ...prev, [playerId]: value }));
  };

  const handlePlaceBid = async (addAmount = null) => {
    if (!currentPlayer) return;
    if (!selectedTeamId) {
      setMessage('⚠️ Please select your franchise first!');
      return;
    }

    const currentPrice = currentPlayer.basePrice || 0;
    let targetBidAmount = 0;

    if (addAmount) {
      targetBidAmount = currentPrice + addAmount;
    } else {
      const enteredValue = Number(customBids[currentPlayer.id]);
      if (!enteredValue || enteredValue <= currentPrice) {
        setMessage(`⚠️ Bid must be higher than current bid (₹${currentPrice.toLocaleString('en-IN')})!`);
        return;
      }
      targetBidAmount = enteredValue;
    }

    if (activeTeam && activeTeam.budget < targetBidAmount) {
      setMessage(`❌ ${activeTeam.name} does not have enough purse left!`);
      return;
    }

    try {
      await axios.post('http://localhost:8082/api/bids', {
        playerId: currentPlayer.id,
        teamId: selectedTeamId,
        amount: targetBidAmount
      });
      setCustomBids(prev => ({ ...prev, [currentPlayer.id]: '' }));
    } catch (err) {
      setMessage(`❌ ${err.response?.data || err.message}`);
    }
  };

  const handleSellPlayer = async () => {
    if (!currentPlayer || !selectedTeamId) return;

    try {
      await axios.put(`http://localhost:8082/api/players/${currentPlayer.id}/sell`, {
        teamId: selectedTeamId,
        finalPrice: currentPlayer.basePrice
      });
    } catch (err) {
      setMessage(`❌ Failed to finalize sale: ${err.response?.data || err.message}`);
    }
  };

  const handleNextPlayer = () => {
    if (currentIndex < players.length - 1) {
      setCurrentIndex(prev => prev + 1);
      setMessage('');
    }
  };

  const handlePrevPlayer = () => {
    if (currentIndex > 0) {
      setCurrentIndex(prev => prev - 1);
      setMessage('');
    }
  };

  if (loading) {
    return (
      <div style={{ padding: '2rem', fontFamily: 'Segoe UI, sans-serif', backgroundColor: '#121212', color: '#fff', minHeight: '100vh' }}>
        <h2>Connecting to IPL Auction Arena...</h2>
      </div>
    );
  }

  return (
    <div style={{ padding: '2rem', fontFamily: 'Segoe UI, sans-serif', backgroundColor: '#121212', color: '#fff', minHeight: '100vh' }}>

      {/* Header */}
      <header style={{
        borderBottom: '2px solid #333',
        paddingBottom: '1rem',
        marginBottom: '1.5rem',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        flexWrap: 'wrap',
        gap: '1rem'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.8rem' }}>
          <span style={{ fontSize: '2.5rem' }}>🏟️</span>
          <div>
            <h1 style={{ margin: 0, fontSize: '1.8rem', lineHeight: '1.2' }}>Multi-Franchise IPL Auction Arena</h1>
            <p style={{ margin: '0.2rem 0 0 0', color: '#888', fontSize: '0.95rem' }}>
              Live Broadcast • Player {players.length > 0 ? currentIndex + 1 : 0} of {players.length}
            </p>
          </div>
        </div>

        {/* Team Identity Dropdown */}
        <div style={{ display: 'flex', gap: '1rem', alignItems: 'center', background: '#1e1e1e', padding: '0.8rem 1.2rem', borderRadius: '8px', border: '1px solid #333' }}>
          <div>
            <label style={{ marginRight: '0.5rem', fontWeight: 'bold', color: '#ff9800' }}>Your Franchise: </label>
            <select
              value={selectedTeamId}
              onChange={(e) => setSelectedTeamId(e.target.value)}
              style={{ padding: '0.4rem 0.8rem', borderRadius: '4px', fontSize: '1rem', background: '#2c2c2c', color: '#fff', border: '1px solid #444' }}
            >
              {teams.map(team => (
                <option key={team.id} value={team.id}>{team.name}</option>
              ))}
            </select>
          </div>

          {activeTeam && (
            <div style={{ background: '#2e7d32', padding: '0.4rem 1rem', borderRadius: '6px', fontWeight: 'bold' }}>
              Your Purse: ₹{activeTeam.budget?.toLocaleString('en-IN')}
            </div>
          )}
        </div>
      </header>

      {/* Broadcast Message Ticker */}
      {message && (
        <div style={{ marginBottom: '1.5rem', padding: '0.8rem', borderRadius: '6px', background: '#1e1e1e', borderLeft: '4px solid #ff9800', fontSize: '1.1rem', fontWeight: 'bold' }}>
          {message}
        </div>
      )}

      {/* Main Stage */}
      {currentPlayer ? (
        <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '2rem', maxWidth: '1100px', margin: '0 auto' }}>

          <div style={{ background: '#1e1e1e', border: '2px solid #333', borderRadius: '12px', padding: '2rem', position: 'relative' }}>
            <span style={{ position: 'absolute', top: '1rem', right: '1rem', padding: '0.3rem 0.8rem', borderRadius: '20px', background: currentPlayer.status === 'SOLD' ? '#388e3c' : '#ff9800', fontWeight: 'bold' }}>
              {currentPlayer.status}
            </span>

            <h2 style={{ fontSize: '2.5rem', margin: '0 0 0.5rem 0', color: '#646cff' }}>{currentPlayer.name}</h2>
            <p style={{ fontSize: '1.2rem', color: '#aaa', margin: '0 0 1.5rem 0' }}>Role: <strong>{currentPlayer.role}</strong></p>

            <div style={{ background: '#2a2a2a', padding: '1.5rem', borderRadius: '8px', marginBottom: '1.5rem', border: '1px solid #444' }}>
              <span style={{ color: '#888', display: 'block', fontSize: '0.9rem' }}>CURRENT HIGHEST BID</span>
              <span style={{ fontSize: '3rem', fontWeight: 'bold', color: '#4caf50' }}>
                ₹{currentPlayer.basePrice?.toLocaleString('en-IN')}
              </span>
            </div>

            {currentPlayer.status === 'UNSOLD' ? (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>

                {/* Paddle Actions */}
                <div style={{ display: 'flex', gap: '0.5rem' }}>
                  <button onClick={() => handlePlaceBid(2000000)} style={{ flex: 1, padding: '0.8rem', background: '#2196f3', color: '#fff', border: 'none', borderRadius: '6px', fontWeight: 'bold', cursor: 'pointer', fontSize: '1rem' }}>
                    Raise +₹20L
                  </button>
                  <button onClick={() => handlePlaceBid(5000000)} style={{ flex: 1, padding: '0.8rem', background: '#ff9800', color: '#000', border: 'none', borderRadius: '6px', fontWeight: 'bold', cursor: 'pointer', fontSize: '1rem' }}>
                    Raise +₹50L
                  </button>
                  <button onClick={() => handlePlaceBid(10000000)} style={{ flex: 1, padding: '0.8rem', background: '#9c27b0', color: '#fff', border: 'none', borderRadius: '6px', fontWeight: 'bold', cursor: 'pointer', fontSize: '1rem' }}>
                    Raise +₹1Cr
                  </button>
                </div>

                <div style={{ display: 'flex', gap: '0.5rem' }}>
                  <input
                    type="number"
                    placeholder="Enter custom total bid..."
                    value={customBids[currentPlayer.id] || ''}
                    onChange={(e) => handleBidInputChange(currentPlayer.id, e.target.value)}
                    style={{ flex: 1, padding: '0.8rem', borderRadius: '6px', border: '1px solid #444', background: '#2c2c2c', color: '#fff', fontSize: '1rem' }}
                  />
                  <button onClick={() => handlePlaceBid()} style={{ padding: '0.8rem 1.5rem', background: '#e91e63', color: '#fff', border: 'none', borderRadius: '6px', fontWeight: 'bold', cursor: 'pointer' }}>
                    Submit Bid
                  </button>
                </div>

                <button
                  onClick={handleSellPlayer}
                  style={{ marginTop: '1rem', padding: '1rem', background: '#4caf50', color: '#fff', border: 'none', borderRadius: '6px', fontWeight: 'bold', fontSize: '1.2rem', cursor: 'pointer' }}
                >
                  🔨 Hammer Down (SOLD)
                </button>
              </div>
            ) : (
              <div style={{ padding: '1rem', background: '#2e7d32', borderRadius: '6px', fontSize: '1.2rem', fontWeight: 'bold', textAlign: 'center' }}>
                🎉 Player acquired by {currentPlayer.team?.name || 'Franchise'}
              </div>
            )}
          </div>

          {/* Navigation & Live Team Leaderboard */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <div style={{ background: '#1e1e1e', padding: '1rem', borderRadius: '8px', border: '1px solid #333' }}>
              <h3 style={{ margin: '0 0 1rem 0' }}>Auctioneer Stage Controls</h3>
              <div style={{ display: 'flex', gap: '0.5rem' }}>
                <button
                  onClick={handlePrevPlayer}
                  disabled={currentIndex === 0}
                  style={{ flex: 1, padding: '0.6rem', background: '#333', color: '#fff', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
                >
                  ◀ Prev
                </button>
                <button
                  onClick={handleNextPlayer}
                  disabled={currentIndex === players.length - 1}
                  style={{ flex: 1, padding: '0.6rem', background: '#333', color: '#fff', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
                >
                  Next ▶
                </button>
              </div>
            </div>

            <div style={{ background: '#1e1e1e', padding: '1rem', borderRadius: '8px', border: '1px solid #333', flex: 1 }}>
              <h3 style={{ margin: '0 0 1rem 0' }}>Live Team Purses</h3>
              <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
                {teams.map(t => (
                  <li key={t.id} style={{ display: 'flex', justifyContent: 'space-between', padding: '0.5rem 0', borderBottom: '1px solid #2a2a2a', fontSize: '0.9rem' }}>
                    <span>{t.name}</span>
                    <strong style={{ color: '#4caf50' }}>₹{(t.budget / 10000000).toFixed(2)} Cr</strong>
                  </li>
                ))}
              </ul>
            </div>
          </div>

        </div>
      ) : (
        <p>No players available in auction pool.</p>
      )}
    </div>
  );
}

export default App;