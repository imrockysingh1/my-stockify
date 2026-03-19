import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { TrendingUp } from 'lucide-react';
import { register } from '../lib/api';

const inputStyle = {
  width: '100%', padding: '10px 14px',
  backgroundColor: '#0f172a', color: '#e2e8f0',
  border: '1px solid #334155', borderRadius: 8, fontSize: 14,
};
const labelStyle = {
  display: 'block', fontSize: 13, fontWeight: 500,
  color: '#94a3b8', marginBottom: 6,
};

function Field({ label, children }) {
  return (
    <div>
      <label style={labelStyle}>{label}</label>
      {children}
    </div>
  );
}

export default function Register() {
  const [formData, setFormData] = useState({
    username: '', name: '', email: '', phone: '',
    dob: '', aadhaar: '', pan: '', income: '',
    fatherName: '', occupation: '', maritalStatus: '',
    password: '', confirmPassword: '',
  });
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (formData.password !== formData.confirmPassword) {
      setError('Passwords do not match'); return;
    }
    if (formData.aadhaar.length !== 12) {
      setError('Aadhaar number must be 12 digits'); return;
    }
    if (formData.pan.length !== 10) {
      setError('PAN must be 10 characters'); return;
    }

    setIsLoading(true);
    try {
      await register({
        username: formData.username,
        name: formData.name,
        email: formData.email,
        phone: formData.phone,
        dob: formData.dob,
        aadhaar: formData.aadhaar,
        pan: formData.pan.toUpperCase(),
        income: parseFloat(formData.income),
        password: formData.password,
        fatherName: formData.fatherName,
        occupation: formData.occupation,
        maritalStatus: formData.maritalStatus,
      });
      navigate('/login');
    } catch (err) {
      setError(err.message || 'Registration failed. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const grid2 = { display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 };

  return (
    <div style={{
      minHeight: '100vh', display: 'flex', alignItems: 'center',
      justifyContent: 'center', backgroundColor: '#0f172a', padding: '24px 16px',
    }}>
      <div style={{ width: '100%', maxWidth: 560 }}>

        {/* Logo */}
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 10, marginBottom: 28 }}>
          <div style={{ width: 44, height: 44, borderRadius: 10, backgroundColor: '#2563eb', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <TrendingUp size={22} color="white" />
          </div>
          <span style={{ fontSize: 26, fontWeight: 700, color: 'white' }}>Stockify</span>
        </div>

        {/* Card */}
        <div style={{ backgroundColor: '#1e293b', borderRadius: 12, border: '1px solid #334155', padding: 32, boxShadow: '0 20px 40px rgba(0,0,0,0.3)' }}>
          <h2 style={{ fontSize: 22, fontWeight: 600, color: 'white', textAlign: 'center', marginBottom: 4 }}>Create your account</h2>
          <p style={{ fontSize: 14, color: '#64748b', textAlign: 'center', marginBottom: 24 }}>Fill in your details to get started. You'll receive ₹10,000 virtual balance!</p>

          <form onSubmit={handleSubmit}>

            {/* Section: Account Info */}
            <p style={{ fontSize: 11, fontWeight: 700, color: '#2563eb', textTransform: 'uppercase', letterSpacing: '0.08em', marginBottom: 12 }}>Account Info</p>
            <div style={{ ...grid2, marginBottom: 16 }}>
              <Field label="Username">
                <input style={inputStyle} name="username" value={formData.username} onChange={handleChange} placeholder="Choose a username" required disabled={isLoading} />
              </Field>
              <Field label="Full Name">
                <input style={inputStyle} name="name" value={formData.name} onChange={handleChange} placeholder="Your full name" required disabled={isLoading} />
              </Field>
            </div>

            {/* Section: Contact */}
            <p style={{ fontSize: 11, fontWeight: 700, color: '#2563eb', textTransform: 'uppercase', letterSpacing: '0.08em', marginBottom: 12 }}>Contact Details</p>
            <div style={{ marginBottom: 16 }}>
              <Field label="Email Address">
                <input style={inputStyle} type="email" name="email" value={formData.email} onChange={handleChange} placeholder="Enter your email" required disabled={isLoading} />
              </Field>
            </div>
            <div style={{ ...grid2, marginBottom: 16 }}>
              <Field label="Phone Number">
                <input style={inputStyle} type="tel" name="phone" value={formData.phone} onChange={handleChange} placeholder="10-digit number" required disabled={isLoading} />
              </Field>
              <Field label="Date of Birth">
                <input style={inputStyle} type="date" name="dob" value={formData.dob} onChange={handleChange} required disabled={isLoading} />
              </Field>
            </div>

            {/* Section: Personal Info */}
            <p style={{ fontSize: 11, fontWeight: 700, color: '#2563eb', textTransform: 'uppercase', letterSpacing: '0.08em', marginBottom: 12 }}>Personal Info</p>
            <div style={{ ...grid2, marginBottom: 16 }}>
              <Field label="Father's Name">
                <input style={inputStyle} name="fatherName" value={formData.fatherName} onChange={handleChange} placeholder="Father's full name" disabled={isLoading} />
              </Field>
              <Field label="Occupation">
                <input style={inputStyle} name="occupation" value={formData.occupation} onChange={handleChange} placeholder="e.g. Software Engineer" disabled={isLoading} />
              </Field>
            </div>
            <div style={{ ...grid2, marginBottom: 16 }}>
              <Field label="Marital Status">
                <select
                  style={{ ...inputStyle, cursor: 'pointer' }}
                  name="maritalStatus"
                  value={formData.maritalStatus}
                  onChange={handleChange}
                  disabled={isLoading}
                >
                  <option value="">Select status</option>
                  <option value="Single">Single</option>
                  <option value="Married">Married</option>
                  <option value="Divorced">Divorced</option>
                  <option value="Widowed">Widowed</option>
                </select>
              </Field>
              <Field label="Annual Income (₹)">
                <input style={inputStyle} type="number" name="income" value={formData.income} onChange={handleChange} placeholder="Enter annual income" min={0} required disabled={isLoading} />
              </Field>
            </div>

            {/* Section: KYC */}
            <p style={{ fontSize: 11, fontWeight: 700, color: '#2563eb', textTransform: 'uppercase', letterSpacing: '0.08em', marginBottom: 12 }}>KYC Details</p>
            <div style={{ ...grid2, marginBottom: 16 }}>
              <Field label="Aadhaar Number">
                <input style={inputStyle} name="aadhaar" value={formData.aadhaar} onChange={handleChange} placeholder="12-digit Aadhaar" maxLength={12} pattern="[0-9]{12}" required disabled={isLoading} />
              </Field>
              <Field label="PAN">
                <input style={{ ...inputStyle, textTransform: 'uppercase' }} name="pan" value={formData.pan} onChange={handleChange} placeholder="ABCDE1234F" maxLength={10} required disabled={isLoading} />
              </Field>
            </div>

            {/* Section: Password */}
            <p style={{ fontSize: 11, fontWeight: 700, color: '#2563eb', textTransform: 'uppercase', letterSpacing: '0.08em', marginBottom: 12 }}>Security</p>
            <div style={{ ...grid2, marginBottom: 20 }}>
              <Field label="Password">
                <input style={inputStyle} type="password" name="password" value={formData.password} onChange={handleChange} placeholder="Create password" minLength={8} required disabled={isLoading} />
              </Field>
              <Field label="Confirm Password">
                <input style={inputStyle} type="password" name="confirmPassword" value={formData.confirmPassword} onChange={handleChange} placeholder="Confirm password" minLength={8} required disabled={isLoading} />
              </Field>
            </div>

            {error && (
              <div style={{ backgroundColor: '#450a0a', border: '1px solid #dc2626', borderRadius: 6, padding: '10px 14px', color: '#fca5a5', fontSize: 13, marginBottom: 16 }}>
                {error}
              </div>
            )}

            {/* Wallet notice */}
            <div style={{ backgroundColor: '#052e16', border: '1px solid #16a34a', borderRadius: 8, padding: '10px 14px', marginBottom: 16, fontSize: 13, color: '#4ade80', display: 'flex', alignItems: 'center', gap: 8 }}>
              🎉 You'll receive <strong>₹10,000 virtual balance</strong> to practice trading!
            </div>

            <button
              type="submit"
              disabled={isLoading}
              style={{
                width: '100%', padding: '12px 0',
                backgroundColor: isLoading ? '#1d4ed8' : '#2563eb',
                color: 'white', border: 'none', borderRadius: 8,
                fontSize: 15, fontWeight: 600,
                cursor: isLoading ? 'not-allowed' : 'pointer',
              }}
            >
              {isLoading ? 'Creating account...' : 'Create Account'}
            </button>
          </form>

          <p style={{ textAlign: 'center', fontSize: 14, color: '#64748b', marginTop: 20 }}>
            Already have an account?{' '}
            <Link to="/login" style={{ color: '#60a5fa', fontWeight: 500, textDecoration: 'none' }}>Sign in</Link>
          </p>
        </div>
      </div>
    </div>
  );
}
