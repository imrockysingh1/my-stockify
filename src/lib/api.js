const API_BASE_URL = 'http://localhost:8080';

class ApiError extends Error {
  constructor(status, message) {
    super(message);
    this.status = status;
    this.name = 'ApiError';
  }
}

async function fetchWithAuth(url, options = {}) {
  let token = null;
  try { token = localStorage.getItem('authToken'); } catch {}

  const headers = { 'Content-Type': 'application/json' };
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const response = await fetch(`${API_BASE_URL}${url}`, { ...options, headers });

  if (response.status === 401) {
    try { localStorage.removeItem('authToken'); localStorage.removeItem('username'); } catch {}
    window.location.href = '/login';
    throw new ApiError(401, 'Session expired. Please login again.');
  }
  return response;
}

function mapProfile(raw) {
  return {
    username: raw.username || '',
    name: raw.name || '',
    email: raw.email || '',
    phone: raw.phone || '',
    dob: Array.isArray(raw.dob)
      ? `${raw.dob[0]}-${String(raw.dob[1]).padStart(2, '0')}-${String(raw.dob[2]).padStart(2, '0')}`
      : (raw.dob || ''),
    aadhaar: raw.aadhar || '',
    pan: raw.pan || '',
    income: raw.income || 0,
    fatherName: raw.fatherName || '',
    occupation: raw.occupation || '',
    maritalStatus: raw.maritalStatus || '',
    emailVerified: raw.emailVerified || false,
  };
}

export async function login(data) {
  const response = await fetch(`${API_BASE_URL}/api/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  if (!response.ok) throw new ApiError(response.status, await response.text() || 'Login failed');
  const result = await response.json();
  const token = result.data?.token || result.token;
  try {
    localStorage.setItem('authToken', token);
    localStorage.setItem('username', data.username);
  } catch {}
  return { token, username: data.username };
}

export async function register(data) {
  const backendData = {
    username: data.username,
    name: data.name,
    email: data.email,
    phone: data.phone,
    dob: data.dob,
    aadhar: data.aadhaar,
    pan: data.pan,
    income: data.income,
    password: data.password,
    fatherName: data.fatherName,
    occupation: data.occupation,
    maritalStatus: data.maritalStatus,
  };
  const response = await fetch(`${API_BASE_URL}/api/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(backendData),
  });
  if (!response.ok) throw new ApiError(response.status, await response.text() || 'Registration failed');
}

export async function getUserProfile(username) {
  const response = await fetchWithAuth(`/api/user-profile/${username}`);
  if (!response.ok) throw new ApiError(response.status, await response.text() || 'Failed to fetch profile');
  const result = await response.json();
  return mapProfile(result.data);
}

export async function updateUserProfile(username, data) {
  const response = await fetchWithAuth(`/api/user-profile/${username}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  });
  if (!response.ok) throw new ApiError(response.status, await response.text() || 'Update failed');
  const result = await response.json();
  return mapProfile(result.data);
}

export async function getWalletBalance(username) {
  const response = await fetchWithAuth(`/api/user-profile/${username}/wallet`);
  if (!response.ok) throw new ApiError(response.status, await response.text() || 'Failed to fetch wallet');
  const result = await response.json();
  return result.data;
}

export async function sendOtp(username) {
  const response = await fetchWithAuth(`/api/user-profile/${username}/send-otp`, { method: 'POST' });
  if (!response.ok) throw new ApiError(response.status, await response.text() || 'Failed to send OTP');
  return (await response.json()).data;
}

export async function verifyEmail(username, otp) {
  const response = await fetchWithAuth(`/api/user-profile/${username}/verify-email`, {
    method: 'POST',
    body: JSON.stringify({ otp }),
  });
  const result = await response.json();
  if (!response.ok || !result.success) throw new ApiError(response.status, result.data || 'Verification failed');
  return result.data;
}

export async function getPortfolio(username) {
  const response = await fetchWithAuth(`/api/portfolio/${username}`);
  if (!response.ok) throw new ApiError(response.status, await response.text() || 'Failed to fetch portfolio');
  const result = await response.json();
  const rawHoldings = result.data || [];
  const holdings = rawHoldings.map(h => ({
    id: h.id, symbol: h.stockName || '', name: h.stockName || '',
    quantity: h.quantity || 0, avgPrice: h.averagePrice || 0,
    currentPrice: h.averagePrice || 0,
    value: h.investment || ((h.averagePrice || 0) * (h.quantity || 0)),
    returns: 0, returnsPercentage: 0,
  }));
  const totalInvested = holdings.reduce((sum, h) => sum + h.value, 0);
  return { username, holdings, totalValue: totalInvested, totalInvested, returns: 0, returnsPercentage: 0 };
}

export function logout() {
  try { localStorage.removeItem('authToken'); localStorage.removeItem('username'); } catch {}
  window.location.href = '/login';
}

export function isAuthenticated() {
  try {
    const token = localStorage.getItem('authToken');
    return !!token && token !== 'undefined' && token !== 'null';
  } catch { return false; }
}

export function getStoredUsername() {
  try { return localStorage.getItem('username'); } catch { return null; }
}
