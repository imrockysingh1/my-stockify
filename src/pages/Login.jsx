import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { TrendingUp } from 'lucide-react';
import { login } from '../lib/api';
import { useAuth } from '../context/AuthContext';

export default function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();
  const { setAuth } = useAuth();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);
    try {
      await login({ username, password });
      setAuth(username);
      navigate('/portfolio');
    } catch (err) {
      setError(err.message || 'Login failed. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div style={{
      minHeight: '100vh', display: 'flex',
      alignItems: 'center', justifyContent: 'center',
      backgroundColor: '#0f172a', padding: 16,
    }}>
      <div style={{ width: '100%', maxWidth: 420 }}>
        {/* Logo */}
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 10, marginBottom: 32 }}>
          <div style={{
            width: 44, height: 44, borderRadius: 10,
            backgroundColor: '#2563eb',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
          }}>
            <TrendingUp size={22} color="white" />
          </div>
          <span style={{ fontSize: 26, fontWeight: 700, color: 'white' }}>Stockify</span>
        </div>

        {/* Card */}
        <div style={{
          backgroundColor: '#1e293b', borderRadius: 12,
          border: '1px solid #334155', padding: 32,
          boxShadow: '0 20px 40px rgba(0,0,0,0.3)',
        }}>
          <h2 style={{ fontSize: 22, fontWeight: 600, color: 'white', textAlign: 'center', marginBottom: 6 }}>
            Welcome back
          </h2>
          <p style={{ fontSize: 14, color: '#64748b', textAlign: 'center', marginBottom: 28 }}>
            Sign in to your account to continue
          </p>

          <form onSubmit={handleSubmit}>
            <div style={{ marginBottom: 16 }}>
              <label style={{ display: 'block', fontSize: 13, fontWeight: 500, color: '#94a3b8', marginBottom: 6 }}>
                Username
              </label>
              <input
                type="text"
                value={username}
                onChange={e => setUsername(e.target.value)}
                placeholder="Enter your username"
                required
                disabled={isLoading}
              />
            </div>

            <div style={{ marginBottom: 16 }}>
              <label style={{ display: 'block', fontSize: 13, fontWeight: 500, color: '#94a3b8', marginBottom: 6 }}>
                Password
              </label>
              <input
                type="password"
                value={password}
                onChange={e => setPassword(e.target.value)}
                placeholder="Enter your password"
                required
                disabled={isLoading}
              />
            </div>

            {error && (
              <div style={{
                backgroundColor: '#450a0a', border: '1px solid #dc2626',
                borderRadius: 6, padding: '10px 14px',
                color: '#fca5a5', fontSize: 13, marginBottom: 16,
              }}>
                {error}
              </div>
            )}

            <button
              type="submit"
              disabled={isLoading}
              style={{
                width: '100%', padding: '11px 0',
                backgroundColor: isLoading ? '#1d4ed8' : '#2563eb',
                color: 'white', border: 'none', borderRadius: 8,
                fontSize: 15, fontWeight: 600, cursor: isLoading ? 'not-allowed' : 'pointer',
                transition: 'background 0.2s', marginTop: 4,
              }}
            >
              {isLoading ? 'Signing in...' : 'Sign in'}
            </button>
          </form>

          <p style={{ textAlign: 'center', fontSize: 14, color: '#64748b', marginTop: 24 }}>
            Don't have an account?{' '}
            <Link to="/register" style={{ color: '#60a5fa', fontWeight: 500, textDecoration: 'none' }}>
              Create account
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
