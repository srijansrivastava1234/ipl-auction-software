import { useEffect, useState } from 'react';
import axios from 'axios';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

import Login from './components/Login';
import FranchiseHeader from './components/FranchiseHeader';
import PlayerCard from './components/PlayerCard';
import BiddingConsole from './components/BiddingConsole';
import Leaderboard from './components/Leaderboard';

const BACKEND_URL = import.meta.env.VITE_API_URL || 'http://localhost:8082';

function App() {
  const [user, setUser] = useState(null);
  const [players, setPlayers] = useState([]);
  const [teams, setTeams] = useState([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState('');
  const [activeHighestBid, setActiveHighestBid] = useState(null);

  // Load user from session storage on startup
  useEffect(() => {
    const savedUser = sessionStorage.getItem('ipl_user');
    if (savedUser) {
      setUser(JSON.parse(savedUser));
    }
    setLoading(false);
  }, []);

  // Fetch initial teams and players once user is logged in
  useEffect(() => {
    if (!user) return;
    fetchInitialData();

    // Establish WebSocket Connection
    const stompClient = new Client({
      webSocketFactory: () => new SockJS(`${BACKEND_URL}/ws-auction`),
      reconnectDelay: 5000,
      onConnect: () => {
        // Subscribe to live bids
        stompClient.subscribe('/topic/bids', (msg) => {
          const newBid = JSON.parse(msg.body);
          setMessage(`🔥 Live Bid! ${newBid.team.name} raised bid to ₹${newBid.amount.toLocaleString('en-IN')}!`);
          
          // Refresh list and if this bid is for the current player, update active bid state
          fetchInitialData();
        });

        // Subscribe to final sales
        stompClient.subscribe('/topic/players', (msg) => {
          const soldPlayer = JSON.parse(msg.body);
          setMessage(`🎉 HAMMER DOWN! ${soldPlayer.name} SOLD to ${soldPlayer.team.name} for ₹${soldPlayer.basePrice.toLocaleString('en-IN')}!`);
          
          fetchInitialData();
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
  }, [user]);

  // Fetch active player's highest bid whenever the active player changes
  useEffect(() => {
    if (players.length > 0 && players[currentIndex]) {
      fetchHighestBid(players[currentIndex].id);
    } else {
      setActiveHighestBid(null);
    }
  }, [currentIndex, players]);

  const fetchInitialData = async () => {
    try {
      const [playersRes, teamsRes] = await Promise.all([
        axios.get(`${BACKEND_URL}/api/players`),
        axios.get(`${BACKEND_URL}/api/teams`)
      ]);
      setPlayers(playersRes.data);
      setTeams(teamsRes.data);
    } catch (error) {
      console.error('Error fetching initial data:', error);
      setMessage('⚠️ Could not connect to Spring Boot server.');
    }
  };

  const fetchHighestBid = async (playerId) => {
    try {
      const res = await axios.get(`${BACKEND_URL}/api/bids/player/${playerId}/highest`);
      if (res.status === 200 && res.data) {
        setActiveHighestBid(res.data);
      } else {
        setActiveHighestBid(null);
      }
    } catch (error) {
      console.error('Error fetching highest bid:', error);
      setActiveHighestBid(null);
    }
  };

  const handleLoginSuccess = (loginData) => {
    sessionStorage.setItem('ipl_user', JSON.stringify(loginData));
    setUser(loginData);
  };

  const handleLogout = () => {
    sessionStorage.removeItem('ipl_user');
    setUser(null);
    setPlayers([]);
    setTeams([]);
    setActiveHighestBid(null);
    setMessage('');
  };

  const handlePlaceBid = async (amount, isIncremental) => {
    const currentPlayer = players[currentIndex];
    if (!currentPlayer || !user) return;

    let targetBidAmount = 0;
    const currentPrice = activeHighestBid ? activeHighestBid.amount : currentPlayer.basePrice;

    if (isIncremental) {
      targetBidAmount = currentPrice + amount;
    } else {
      if (amount <= currentPrice) {
        setMessage(`⚠️ Bid must be higher than current bid (₹${currentPrice.toLocaleString('en-IN')})!`);
        return;
      }
      targetBidAmount = amount;
    }

    try {
      const config = {
        headers: { Authorization: `Bearer ${user.token}` }
      };

      await axios.post(`${BACKEND_URL}/api/bids`, {
        playerId: currentPlayer.id,
        teamId: user.teamId,
        amount: targetBidAmount
      }, config);

      setMessage('');
    } catch (err) {
      const errMsg = err.response?.data?.error || err.message;
      setMessage(`❌ Bidding Error: ${errMsg}`);
    }
  };

  const handleSellPlayer = async () => {
    const currentPlayer = players[currentIndex];
    if (!currentPlayer || !user) return;

    if (!activeHighestBid) {
      setMessage('⚠️ No bids have been placed on this player yet. Cannot finalize sale.');
      return;
    }

    try {
      const config = {
        headers: { Authorization: `Bearer ${user.token}` }
      };

      await axios.put(`${BACKEND_URL}/api/players/${currentPlayer.id}/sell`, {
        teamId: activeHighestBid.team.id,
        finalPrice: activeHighestBid.amount
      }, config);

      setMessage('');
    } catch (err) {
      const errMsg = err.response?.data?.error || err.message;
      setMessage(`❌ Failed to finalize sale: ${errMsg}`);
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
      <div className="loading-screen">
        <h2>Connecting to IPL Auction Arena...</h2>
      </div>
    );
  }

  // Render Login Card if not authenticated
  if (!user) {
    return <Login onLoginSuccess={handleLoginSuccess} backendUrl={BACKEND_URL} />;
  }

  const currentPlayer = players[currentIndex];
  const activeTeam = teams.find(t => t.id === user.teamId);

  // Augment current player displaying current highest bid price
  const displayPlayer = currentPlayer ? {
    ...currentPlayer,
    basePrice: activeHighestBid ? activeHighestBid.amount : currentPlayer.basePrice,
    team: currentPlayer.status === 'SOLD' ? currentPlayer.team : (activeHighestBid ? activeHighestBid.team : null)
  } : null;

  return (
    <div className="app-container">
      <FranchiseHeader
        user={user}
        activeTeam={activeTeam}
        totalPlayers={players.length}
        currentIndex={currentIndex}
        onLogout={handleLogout}
      />

      {/* Broadcast Message Ticker */}
      {message && (
        <div className="ticker-message">
          <span className="ticker-icon">📢</span>
          <span className="ticker-text">{message}</span>
        </div>
      )}

      {/* Main Grid Layout */}
      {displayPlayer ? (
        <div className="auction-grid">
          {/* Left Column: Player Display & Console */}
          <div className="main-stage">
            <PlayerCard player={displayPlayer} />
            
            <BiddingConsole
              user={user}
              player={displayPlayer}
              onPlaceBid={handlePlaceBid}
              onSellPlayer={handleSellPlayer}
              onPrevPlayer={handlePrevPlayer}
              onNextPlayer={handleNextPlayer}
              currentIndex={currentIndex}
              totalPlayers={players.length}
            />
          </div>

          {/* Right Column: Leaderboard */}
          <div className="side-stage">
            <Leaderboard teams={teams} />
          </div>
        </div>
      ) : (
        <div className="empty-pool-message">
          <p>No players available in the auction pool.</p>
        </div>
      )}
    </div>
  );
}

export default App;