import React, { useEffect, useState } from 'react';
import {
  User, Mail, Phone, Calendar, CreditCard, IndianRupee,
  Shield, Edit, CheckCircle, AlertCircle, Wallet, X, Save, Send
} from 'lucide-react';
import Header from '../components/Header';
import { useAuth } from '../context/AuthContext';
import { getUserProfile, updateUserProfile, getWalletBalance, sendOtp, verifyEmail } from '../lib/api';

function formatDate(d) {
  if (!d) return 'N/A';
  return new Date(d).toLocaleDateString('en-IN', { year: 'numeric', month: 'long', day: 'numeric' });
}
function formatCurrency(v) {
  return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(v);
}
function getInitials(name) {
  if (!name) return '?';
  return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
}

function ProfileField({ icon: Icon, label, value, mono = false, badge }) {
  return (
    <div style={{ display: 'flex', alignItems: 'flex-start', gap: 14, padding: 16, borderRadius: 10, backgroundColor: '#0f172a', border: '1px solid #1e3a5f' }}>
      <div style={{ width: 40, height: 40, borderRadius: 8, backgroundColor: '#1e293b', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
        <Icon size={18} color="#64748b" />
      </div>
      <div style={{ flex: 1 }}>
        <p style={{ fontSize: 12, color: '#64748b', marginBottom: 4 }}>{label}</p>
        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <p style={{ fontSize: 15, fontWeight: 500, color: 'white', fontFamily: mono ? 'monospace' : 'inherit', letterSpacing: mono ? '0.1em' : 'normal' }}>
            {value || '—'}
          </p>
          {badge}
        </div>
      </div>
    </div>
  );
}

export default function Profile() {
  const { username } = useAuth();
  const [profile, setProfile] = useState(null);
  const [wallet, setWallet] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  // Edit modal state
  const [showEdit, setShowEdit] = useState(false);
  const [editData, setEditData] = useState({});
  const [editLoading, setEditLoading] = useState(false);
  const [editError, setEditError] = useState('');
  const [editSuccess, setEditSuccess] = useState('');

  // OTP state
  const [showOtp, setShowOtp] = useState(false);
  const [otp, setOtp] = useState('');
  const [otpLoading, setOtpLoading] = useState(false);
  const [otpMsg, setOtpMsg] = useState('');
  const [otpSent, setOtpSent] = useState(false);

  useEffect(() => {
    if (!username) return;
    Promise.all([
      getUserProfile(username),
      getWalletBalance(username).catch(() => null),
    ]).then(([profileData, walletData]) => {
      setProfile(profileData);
      setWallet(walletData);
    }).catch(err => setError(err.message))
      .finally(() => setIsLoading(false));
  }, [username]);

  const openEdit = () => {
    setEditData({
      phone: profile.phone,
      email: profile.email,
      fatherName: profile.fatherName,
      occupation: profile.occupation,
      maritalStatus: profile.maritalStatus,
      income: profile.income,
    });
    setEditError('');
    setEditSuccess('');
    setShowEdit(true);
  };

  const handleEditSave = async () => {
    setEditLoading(true);
    setEditError('');
    setEditSuccess('');
    try {
      const updated = await updateUserProfile(username, editData);
      setProfile(updated);
      setEditSuccess('Profile updated successfully!');
      setTimeout(() => setShowEdit(false), 1200);
    } catch (err) {
      setEditError(err.message || 'Update failed');
    } finally {
      setEditLoading(false);
    }
  };

  const handleSendOtp = async () => {
    setOtpLoading(true);
    setOtpMsg('');
    try {
      await sendOtp(username);
      setOtpSent(true);
      setOtpMsg('OTP sent to your email!');
    } catch (err) {
      setOtpMsg(err.message || 'Failed to send OTP');
    } finally {
      setOtpLoading(false);
    }
  };

  const handleVerifyOtp = async () => {
    if (!otp || otp.length !== 6) { setOtpMsg('Enter 6-digit OTP'); return; }
    setOtpLoading(true);
    setOtpMsg('');
    try {
      await verifyEmail(username, otp);
      setProfile(prev => ({ ...prev, emailVerified: true }));
      setShowOtp(false);
      setOtp('');
    } catch (err) {
      setOtpMsg(err.message || 'Invalid OTP');
    } finally {
      setOtpLoading(false);
    }
  };

  const overlay = {
    position: 'fixed', inset: 0, backgroundColor: 'rgba(0,0,0,0.7)',
    display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 100, padding: 16,
  };
  const modal = {
    backgroundColor: '#1e293b', borderRadius: 14, border: '1px solid #334155',
    padding: 28, width: '100%', maxWidth: 480, maxHeight: '90vh', overflowY: 'auto',
  };
  const inputStyle = {
    width: '100%', padding: '9px 12px', backgroundColor: '#0f172a',
    color: '#e2e8f0', border: '1px solid #334155', borderRadius: 8, fontSize: 14,
  };
  const labelStyle = { display: 'block', fontSize: 12, color: '#64748b', marginBottom: 5 };

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#0f172a' }}>
      <Header />
      <main style={{ maxWidth: 860, margin: '0 auto', padding: '32px 24px' }}>

        {/* Page header */}
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 28 }}>
          <div>
            <h1 style={{ fontSize: 26, fontWeight: 600, color: 'white', marginBottom: 4 }}>Your Profile</h1>
            <p style={{ color: '#64748b', fontSize: 14 }}>Manage your account information</p>
          </div>
          {profile && (
            <button onClick={openEdit} style={{
              display: 'flex', alignItems: 'center', gap: 8,
              padding: '9px 18px', backgroundColor: '#2563eb',
              color: 'white', border: 'none', borderRadius: 8,
              fontSize: 14, fontWeight: 600, cursor: 'pointer',
            }}>
              <Edit size={15} /> Edit Profile
            </button>
          )}
        </div>

        {isLoading ? (
          <div style={{ textAlign: 'center', color: '#64748b', padding: 60 }}>Loading profile...</div>
        ) : error ? (
          <div style={{ textAlign: 'center', color: '#dc2626', padding: 60 }}>{error}</div>
        ) : profile ? (
          <>
            {/* Wallet + Avatar row */}
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16, marginBottom: 20 }}>

              {/* Avatar card */}
              <div style={{ backgroundColor: '#1e293b', borderRadius: 12, border: '1px solid #334155', padding: 24, display: 'flex', alignItems: 'center', gap: 18 }}>
                <div style={{ width: 68, height: 68, borderRadius: '50%', backgroundColor: '#2563eb', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 24, fontWeight: 700, color: 'white', flexShrink: 0 }}>
                  {getInitials(profile.name)}
                </div>
                <div>
                  <h2 style={{ fontSize: 20, fontWeight: 600, color: 'white', marginBottom: 2 }}>{profile.name}</h2>
                  <p style={{ fontSize: 13, color: '#64748b', marginBottom: 10 }}>@{profile.username}</p>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                    {profile.emailVerified ? (
                      <span style={{ display: 'inline-flex', alignItems: 'center', gap: 5, backgroundColor: '#052e16', border: '1px solid #16a34a', borderRadius: 20, padding: '3px 10px', fontSize: 12, fontWeight: 600, color: '#16a34a' }}>
                        <CheckCircle size={12} /> Verified
                      </span>
                    ) : (
                      <button onClick={() => { setShowOtp(true); setOtpSent(false); setOtpMsg(''); setOtp(''); }} style={{ display: 'inline-flex', alignItems: 'center', gap: 5, backgroundColor: '#451a03', border: '1px solid #d97706', borderRadius: 20, padding: '3px 10px', fontSize: 12, fontWeight: 600, color: '#fbbf24', cursor: 'pointer' }}>
                        <AlertCircle size={12} /> Verify Email
                      </button>
                    )}
                  </div>
                </div>
              </div>

              {/* Wallet card */}
              <div style={{ backgroundColor: '#1e293b', borderRadius: 12, border: '1px solid #334155', padding: 24 }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 12 }}>
                  <div style={{ width: 40, height: 40, borderRadius: 8, backgroundColor: '#052e16', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <Wallet size={20} color="#16a34a" />
                  </div>
                  <p style={{ fontSize: 14, color: '#64748b', fontWeight: 500 }}>Virtual Wallet</p>
                </div>
                <p style={{ fontSize: 32, fontWeight: 700, color: '#4ade80', marginBottom: 6 }}>
                  {wallet ? formatCurrency(wallet.amount) : '—'}
                </p>
                <p style={{ fontSize: 12, color: '#64748b' }}>Available balance to practice trading</p>
              </div>
            </div>

            {/* Profile fields grid */}
            <div style={{ backgroundColor: '#1e293b', borderRadius: 12, border: '1px solid #334155', padding: 24 }}>
              <h3 style={{ fontSize: 16, fontWeight: 600, color: 'white', marginBottom: 4 }}>Personal Information</h3>
              <p style={{ fontSize: 13, color: '#64748b', marginBottom: 20 }}>Your account and KYC details</p>

              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: 12 }}>
                <ProfileField icon={Mail} label="Email Address" value={profile.email}
                  badge={profile.emailVerified
                    ? <span style={{ display: 'inline-flex', alignItems: 'center', gap: 3, fontSize: 11, color: '#4ade80' }}><CheckCircle size={11} />Verified</span>
                    : <span style={{ display: 'inline-flex', alignItems: 'center', gap: 3, fontSize: 11, color: '#fbbf24' }}><AlertCircle size={11} />Unverified</span>}
                />
                <ProfileField icon={Phone} label="Phone Number" value={profile.phone} />
                <ProfileField icon={Calendar} label="Date of Birth" value={formatDate(profile.dob)} />
                <ProfileField icon={IndianRupee} label="Annual Income" value={formatCurrency(profile.income)} />
                <ProfileField icon={User} label="Father's Name" value={profile.fatherName} />
                <ProfileField icon={User} label="Occupation" value={profile.occupation} />
                <ProfileField icon={Shield} label="Marital Status" value={profile.maritalStatus} />
                <ProfileField icon={CreditCard} label="Aadhaar Number" value={profile.aadhaar} mono />
                <ProfileField icon={CreditCard} label="PAN" value={profile.pan} mono />
              </div>
            </div>
          </>
        ) : null}
      </main>

      {/* ── EDIT MODAL ─────────────────────────────── */}
      {showEdit && (
        <div style={overlay} onClick={e => e.target === e.currentTarget && setShowEdit(false)}>
          <div style={modal}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 20 }}>
              <h3 style={{ fontSize: 18, fontWeight: 600, color: 'white' }}>Edit Profile</h3>
              <button onClick={() => setShowEdit(false)} style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#64748b' }}><X size={20} /></button>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
              <div>
                <label style={labelStyle}>Phone Number</label>
                <input style={inputStyle} value={editData.phone || ''} onChange={e => setEditData(p => ({ ...p, phone: e.target.value }))} placeholder="10-digit phone" maxLength={10} />
              </div>
              <div>
                <label style={labelStyle}>Email Address</label>
                <input style={inputStyle} type="email" value={editData.email || ''} onChange={e => setEditData(p => ({ ...p, email: e.target.value }))} placeholder="Email address" />
                {editData.email !== profile.email && (
                  <p style={{ fontSize: 11, color: '#fbbf24', marginTop: 4 }}>⚠ Changing email will require re-verification</p>
                )}
              </div>
              <div>
                <label style={labelStyle}>Father's Name</label>
                <input style={inputStyle} value={editData.fatherName || ''} onChange={e => setEditData(p => ({ ...p, fatherName: e.target.value }))} placeholder="Father's full name" />
              </div>
              <div>
                <label style={labelStyle}>Occupation</label>
                <input style={inputStyle} value={editData.occupation || ''} onChange={e => setEditData(p => ({ ...p, occupation: e.target.value }))} placeholder="e.g. Software Engineer" />
              </div>
              <div>
                <label style={labelStyle}>Marital Status</label>
                <select style={{ ...inputStyle, cursor: 'pointer' }} value={editData.maritalStatus || ''} onChange={e => setEditData(p => ({ ...p, maritalStatus: e.target.value }))}>
                  <option value="">Select status</option>
                  <option value="Single">Single</option>
                  <option value="Married">Married</option>
                  <option value="Divorced">Divorced</option>
                  <option value="Widowed">Widowed</option>
                </select>
              </div>
              <div>
                <label style={labelStyle}>Annual Income (₹)</label>
                <input style={inputStyle} type="number" value={editData.income || ''} onChange={e => setEditData(p => ({ ...p, income: parseFloat(e.target.value) }))} placeholder="Annual income" min={0} />
              </div>
            </div>

            {editError && <div style={{ marginTop: 14, padding: '10px 14px', backgroundColor: '#450a0a', border: '1px solid #dc2626', borderRadius: 6, color: '#fca5a5', fontSize: 13 }}>{editError}</div>}
            {editSuccess && <div style={{ marginTop: 14, padding: '10px 14px', backgroundColor: '#052e16', border: '1px solid #16a34a', borderRadius: 6, color: '#4ade80', fontSize: 13 }}>{editSuccess}</div>}

            <button
              onClick={handleEditSave}
              disabled={editLoading}
              style={{ marginTop: 20, width: '100%', padding: '11px 0', backgroundColor: '#2563eb', color: 'white', border: 'none', borderRadius: 8, fontSize: 15, fontWeight: 600, cursor: editLoading ? 'not-allowed' : 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 8 }}
            >
              <Save size={16} /> {editLoading ? 'Saving...' : 'Save Changes'}
            </button>
          </div>
        </div>
      )}

      {/* ── OTP MODAL ──────────────────────────────── */}
      {showOtp && (
        <div style={overlay} onClick={e => e.target === e.currentTarget && setShowOtp(false)}>
          <div style={{ ...modal, maxWidth: 380 }}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 20 }}>
              <h3 style={{ fontSize: 18, fontWeight: 600, color: 'white' }}>Verify Email</h3>
              <button onClick={() => setShowOtp(false)} style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#64748b' }}><X size={20} /></button>
            </div>

            <p style={{ fontSize: 14, color: '#94a3b8', marginBottom: 20 }}>
              We'll send a 6-digit OTP to <strong style={{ color: 'white' }}>{profile?.email}</strong>
            </p>

            {!otpSent ? (
              <button
                onClick={handleSendOtp}
                disabled={otpLoading}
                style={{ width: '100%', padding: '11px 0', backgroundColor: '#2563eb', color: 'white', border: 'none', borderRadius: 8, fontSize: 15, fontWeight: 600, cursor: otpLoading ? 'not-allowed' : 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 8 }}
              >
                <Send size={16} /> {otpLoading ? 'Sending...' : 'Send OTP'}
              </button>
            ) : (
              <>
                <div style={{ marginBottom: 14 }}>
                  <label style={labelStyle}>Enter OTP</label>
                  <input
                    style={{ ...inputStyle, fontSize: 20, letterSpacing: '0.3em', textAlign: 'center' }}
                    value={otp}
                    onChange={e => setOtp(e.target.value.replace(/\D/g, '').slice(0, 6))}
                    placeholder="• • • • • •"
                    maxLength={6}
                  />
                </div>
                <button
                  onClick={handleVerifyOtp}
                  disabled={otpLoading}
                  style={{ width: '100%', padding: '11px 0', backgroundColor: '#16a34a', color: 'white', border: 'none', borderRadius: 8, fontSize: 15, fontWeight: 600, cursor: otpLoading ? 'not-allowed' : 'pointer' }}
                >
                  {otpLoading ? 'Verifying...' : 'Verify OTP'}
                </button>
                <button
                  onClick={handleSendOtp}
                  disabled={otpLoading}
                  style={{ width: '100%', marginTop: 10, padding: '8px 0', backgroundColor: 'transparent', color: '#60a5fa', border: '1px solid #334155', borderRadius: 8, fontSize: 13, cursor: 'pointer' }}
                >
                  Resend OTP
                </button>
              </>
            )}

            {otpMsg && (
              <p style={{ marginTop: 12, fontSize: 13, textAlign: 'center', color: otpMsg.includes('sent') ? '#4ade80' : '#fca5a5' }}>
                {otpMsg}
              </p>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
