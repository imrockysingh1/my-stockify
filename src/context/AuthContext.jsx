import React, { createContext, useContext, useEffect, useState } from 'react';
import { isAuthenticated, getStoredUsername, logout as apiLogout } from '../lib/api';

const AuthContext = createContext(undefined);

export function AuthProvider({ children }) {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [username, setUsername] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const authenticated = isAuthenticated();
    const storedUsername = getStoredUsername();
    setIsLoggedIn(authenticated);
    setUsername(storedUsername);
    setIsLoading(false);
  }, []);

  const logout = () => {
    apiLogout();
    setIsLoggedIn(false);
    setUsername(null);
  };

  const setAuth = (newUsername) => {
    setIsLoggedIn(true);
    setUsername(newUsername);
  };

  return (
    <AuthContext.Provider value={{ isLoggedIn, username, logout, setAuth, isLoading }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within AuthProvider');
  return context;
}
