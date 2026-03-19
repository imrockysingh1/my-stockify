import React, { useEffect, useState } from 'react';
import { Wallet, TrendingUp, TrendingDown, PiggyBank, Briefcase } from 'lucide-react';
import Header from '../components/Header';
import { useAuth } from '../context/AuthContext';
import { getPortfolio } from '../lib/api';

function formatCurrency(value) {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency', currency: 'INR', maximumFractionDigits: 2,
  }).format(value);
}

function StatCard({ title, value, subtitle, icon: Icon, trend }) {
  return (
    <div style={{
      backgroundColor: '#1e293b', borderRadius: 12,
      border: '1px solid #334155', padding: 24,
    }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <div>
          <p style={{ fontSize: 13, color: '#64748b', marginBottom: 6 }}>{title}</p>
          <p style={{ fontSize: 24, fontWeight: 600, color: 'white', marginBottom: 4 }}>{value}</p>
          {subtitle && (
            <p style={{
              fontSize: 13, fontWeight: 500,
              color: trend === 'up' ? '#16a34a' : trend === 'down' ? '#dc2626' : '#64748b',
            }}>
              {subtitle}
            </p>
          )}
        </div>
        <div style={{
          width: 40, height: 40, borderRadius: 8,
          backgroundColor: '#334155',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
        }}>
          <Icon size={20} color="#94a3b8" />
        </div>
      </div>
    </div>
  );
}

export default function Portfolio() {
  const { username } = useAuth();
  const [portfolio, setPortfolio] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!username) return;
    getPortfolio(username)
      .then(data => setPortfolio(data))
      .catch(err => setError(err.message || 'Failed to load portfolio'))
      .finally(() => setIsLoading(false));
  }, [username]);

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#0f172a' }}>
      <Header />
      <main style={{ maxWidth: 1200, margin: '0 auto', padding: '32px 24px' }}>
        <div style={{ marginBottom: 32 }}>
          <h1 style={{ fontSize: 28, fontWeight: 600, color: 'white', marginBottom: 6 }}>
            Portfolio Overview
          </h1>
          <p style={{ color: '#64748b' }}>Track your investments and monitor performance</p>
        </div>

        {isLoading ? (
          <div style={{ textAlign: 'center', color: '#64748b', padding: 60 }}>Loading portfolio...</div>
        ) : error ? (
          <div style={{
            backgroundColor: '#1e293b', borderRadius: 12,
            border: '1px solid #334155', padding: 48, textAlign: 'center',
          }}>
            <p style={{ color: '#dc2626' }}>{error}</p>
          </div>
        ) : portfolio ? (
          <>
            {/* Stat Cards */}
            <div style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))',
              gap: 16, marginBottom: 24,
            }}>
              <StatCard title="Total Value" value={formatCurrency(portfolio.totalValue)} icon={Wallet} />
              <StatCard title="Total Invested" value={formatCurrency(portfolio.totalInvested)} icon={PiggyBank} />
              <StatCard
                title="Total Returns"
                value={formatCurrency(portfolio.returns)}
                subtitle={`${portfolio.returns >= 0 ? '+' : ''}${portfolio.returnsPercentage.toFixed(2)}%`}
                icon={portfolio.returns >= 0 ? TrendingUp : TrendingDown}
                trend={portfolio.returns >= 0 ? 'up' : 'down'}
              />
              <StatCard title="Holdings" value={portfolio.holdings.length.toString()} icon={Briefcase} />
            </div>

            {/* Holdings Table */}
            <div style={{
              backgroundColor: '#1e293b', borderRadius: 12,
              border: '1px solid #334155', overflow: 'hidden',
            }}>
              <div style={{ padding: '20px 24px', borderBottom: '1px solid #334155' }}>
                <h2 style={{ fontSize: 18, fontWeight: 600, color: 'white' }}>Your Holdings</h2>
                <p style={{ fontSize: 13, color: '#64748b', marginTop: 4 }}>Detailed breakdown of your investments</p>
              </div>

              {portfolio.holdings.length > 0 ? (
                <div style={{ overflowX: 'auto' }}>
                  <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                    <thead>
                      <tr style={{ borderBottom: '1px solid #334155' }}>
                        {['Stock', 'Qty', 'Avg Price', 'Current Price', 'Value', 'Returns'].map(h => (
                          <th key={h} style={{
                            padding: '12px 16px', textAlign: h === 'Stock' ? 'left' : 'right',
                            fontSize: 12, fontWeight: 600, color: '#64748b',
                            textTransform: 'uppercase', letterSpacing: '0.05em',
                          }}>{h}</th>
                        ))}
                      </tr>
                    </thead>
                    <tbody>
                      {portfolio.holdings.map((holding, i) => {
                        const isPositive = holding.returns >= 0;
                        return (
                          <tr key={holding.id || i} style={{
                            borderBottom: '1px solid #1e3a5f',
                            transition: 'background 0.15s',
                          }}
                            onMouseOver={e => e.currentTarget.style.backgroundColor = '#1a2744'}
                            onMouseOut={e => e.currentTarget.style.backgroundColor = 'transparent'}
                          >
                            <td style={{ padding: '14px 16px' }}>
                              <div style={{ fontWeight: 600, color: 'white' }}>{holding.symbol}</div>
                              <div style={{ fontSize: 12, color: '#64748b' }}>{holding.name}</div>
                            </td>
                            <td style={{ padding: '14px 16px', textAlign: 'right', color: '#e2e8f0' }}>{holding.quantity}</td>
                            <td style={{ padding: '14px 16px', textAlign: 'right', color: '#e2e8f0' }}>{formatCurrency(holding.avgPrice)}</td>
                            <td style={{ padding: '14px 16px', textAlign: 'right', color: '#e2e8f0' }}>{formatCurrency(holding.currentPrice)}</td>
                            <td style={{ padding: '14px 16px', textAlign: 'right', fontWeight: 600, color: 'white' }}>{formatCurrency(holding.value)}</td>
                            <td style={{ padding: '14px 16px', textAlign: 'right' }}>
                              <span style={{ color: isPositive ? '#16a34a' : '#dc2626', fontWeight: 500 }}>
                                {formatCurrency(holding.returns)}
                              </span>
                            </td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </table>
                </div>
              ) : (
                <div style={{ padding: 60, textAlign: 'center' }}>
                  <Briefcase size={40} color="#334155" style={{ margin: '0 auto 16px' }} />
                  <p style={{ fontSize: 16, fontWeight: 500, color: '#94a3b8' }}>No holdings yet</p>
                  <p style={{ fontSize: 14, color: '#64748b', marginTop: 6 }}>Start investing to see your portfolio here</p>
                </div>
              )}
            </div>
          </>
        ) : null}
      </main>
    </div>
  );
}
