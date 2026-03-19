import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { TrendingUp, Briefcase, User, LogOut } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export default function Header() {
  const { username, logout } = useAuth();
  const location = useLocation();

  const navItems = [
    { path: '/portfolio', label: 'Portfolio', icon: Briefcase },
    { path: '/profile', label: 'Profile', icon: User },
  ];

  return (
    <header style={{
      position: 'sticky', top: 0, zIndex: 50,
      backgroundColor: '#1e293b',
      borderBottom: '1px solid #334155',
      padding: '0 24px',
    }}>
      <div style={{
        maxWidth: 1200, margin: '0 auto',
        display: 'flex', alignItems: 'center',
        justifyContent: 'space-between', height: 64,
      }}>
        {/* Logo */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 24 }}>
          <Link to="/portfolio" style={{
            display: 'flex', alignItems: 'center', gap: 8,
            textDecoration: 'none', color: 'white',
          }}>
            <div style={{
              width: 36, height: 36, borderRadius: 8,
              backgroundColor: '#2563eb',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
            }}>
              <TrendingUp size={18} color="white" />
            </div>
            <span style={{ fontSize: 18, fontWeight: 600 }}>Stockify</span>
          </Link>

          {/* Nav */}
          <nav style={{ display: 'flex', gap: 4 }}>
            {navItems.map(item => {
              const Icon = item.icon;
              const isActive = location.pathname === item.path;
              return (
                <Link key={item.path} to={item.path} style={{
                  display: 'flex', alignItems: 'center', gap: 6,
                  padding: '6px 14px', borderRadius: 6,
                  textDecoration: 'none',
                  fontSize: 14, fontWeight: 500,
                  backgroundColor: isActive ? '#334155' : 'transparent',
                  color: isActive ? 'white' : '#94a3b8',
                  transition: 'all 0.2s',
                }}>
                  <Icon size={15} />
                  {item.label}
                </Link>
              );
            })}
          </nav>
        </div>

        {/* Right side */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
          <span style={{ fontSize: 14, color: '#94a3b8' }}>
            Welcome, <span style={{ color: 'white', fontWeight: 500 }}>{username}</span>
          </span>
          <button onClick={logout} style={{
            display: 'flex', alignItems: 'center', gap: 6,
            padding: '7px 14px', borderRadius: 6,
            border: '1px solid #334155',
            backgroundColor: 'transparent',
            color: '#94a3b8', fontSize: 14, cursor: 'pointer',
            transition: 'all 0.2s',
          }}
            onMouseOver={e => e.currentTarget.style.borderColor = '#2563eb'}
            onMouseOut={e => e.currentTarget.style.borderColor = '#334155'}
          >
            <LogOut size={15} />
            Sign out
          </button>
        </div>
      </div>
    </header>
  );
}
