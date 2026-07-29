import { useState } from 'react';
import axios from 'axios';

function Login({ onLoginSuccess, backendUrl }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await axios.post(`${backendUrl}/api/auth/login`, {
        username,
        password
      });
      onLoginSuccess(response.data);
    } catch (err) {
      setError(err.response?.data?.error || 'Invalid credentials or server unreachable.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <div className="login-logo">🏟️</div>
        <h2>IPL Auction Arena</h2>
        <p className="login-subtitle">Sign in as an Auctioneer or Franchise Owner</p>
        
        {error && <div className="login-error">{error}</div>}

        <form onSubmit={handleSubmit} className="login-form">
          <div className="form-group">
            <label htmlFor="username">Username</label>
            <input
              type="text"
              id="username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="e.g. csk_owner or admin"
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              required
            />
          </div>

          <button type="submit" className="login-button" disabled={loading}>
            {loading ? 'Authenticating...' : 'Enter Arena'}
          </button>
        </form>

        <div className="login-hints">
          <h4>Seeded Accounts for Testing:</h4>
          <ul>
            <li><strong>Admin (Auctioneer):</strong> admin / admin123</li>
            <li><strong>Franchises:</strong> csk_owner / csk123, mi_owner / mi123, rcb_owner / rcb123, etc.</li>
          </ul>
        </div>
      </div>
    </div>
  );
}

export default Login;
