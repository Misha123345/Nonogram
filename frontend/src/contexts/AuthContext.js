import React, { createContext, useState, useEffect, useCallback } from 'react';
import { userApi } from '../services';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [currentUser, setCurrentUser] = useState(null);
  const [token, setToken] = useState(() => localStorage.getItem('jwtToken') || null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const parseJwt = useCallback((tokenToParse) => {
    if (!tokenToParse) return null;
    try {
      const base64Url = tokenToParse.split('.')[1];
      if (!base64Url) return null;
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
          return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      }).join(''));
      return JSON.parse(jsonPayload);
    } catch (e) {
      console.error("Error parsing JWT:", e);
      return null;
    }
  }, []);

  useEffect(() => {
    setLoading(true);
    const storedToken = localStorage.getItem('jwtToken');
    if (storedToken) {
      const decodedToken = parseJwt(storedToken);
      if (decodedToken && decodedToken.exp * 1000 > Date.now()) {
        if (decodedToken.sub) {
          setToken(storedToken);
          setCurrentUser({ username: decodedToken.sub });
        } else {
          console.error("JWT token exists but does not contain 'sub' (username) claim.");
          localStorage.removeItem('jwtToken');
          setToken(null);
          setCurrentUser(null);
        }
      } else {
        console.log("Stored token expired or invalid.");
        localStorage.removeItem('jwtToken');
        setToken(null);
        setCurrentUser(null);
      }
    } else {
      setToken(null);
      setCurrentUser(null);
    }
    setLoading(false);
  }, [parseJwt]);

  const login = useCallback(async (username, password) => {
    setError(null);
    setLoading(true);
    try {
      const response = await userApi.login(username, password);
      if (response.data && response.data.jwt) {
        const newToken = response.data.jwt;
        const decodedToken = parseJwt(newToken);
        if (decodedToken && decodedToken.sub) {
          localStorage.setItem('jwtToken', newToken);
          setToken(newToken);
          setCurrentUser({ username: decodedToken.sub });
          return true;
        } else {
          setError('Invalid token received from server.');
          return false;
        }
      } else {
        setError('Login successful but no token received.');
        return false;
      }
    } catch (err) {
      console.error("Login error:", err);
      if (err.response && err.response.status === 401) {
        setError('Invalid username or password.');
      } else if (err.response && err.response.status === 404) {
        setError('User not found.');
      } else if (err.response && err.response.data) {
        setError(err.response.data);
      } else {
        setError('Login failed. Please try again later.');
      }
      localStorage.removeItem('jwtToken');
      setToken(null);
      setCurrentUser(null);
      return false;
    } finally {
      setLoading(false);
    }
  }, [parseJwt]);

  const register = useCallback(async (username, password) => {
    setError(null);
    setLoading(true);
    try {
      await userApi.register(username, password);
      return true;
    } catch (err) {
      console.error("Registration error:", err);
      if (err.response && err.response.status === 409) {
        setError('User with this username already exists.');
      } else if (err.response && err.response.status === 400) {
        if (err.response.data?.includes('Username already exists')) {
          setError('User with this username already exists.');
        } else {
          setError('Invalid data. Make sure username and password meet requirements.');
        }
      } else if (err.response?.data) {
        setError(err.response.data);
      } else {
        setError('Registration failed. Please try again later.');
      }
      return false;
    } finally {
      setLoading(false);
    }
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem('jwtToken');
    setToken(null);
    setCurrentUser(null);
    setError(null);
  }, []);

  const isAuthenticated = useCallback(() => {
    return !!token && !!currentUser;
  }, [token, currentUser]);

  const clearError = useCallback(() => {
    setError(null);
  }, []);

  const updateUserData = async (userData) => {
    try {
      setError(null);
      setLoading(true);
      
      const updateData = {
        ...userData,
        originalUsername: currentUser.username
      };
      
      const response = await userApi.updateUser(updateData);
      
      if (response.data) {
        const usernameChanged = userData.username !== currentUser.username;
        
        if (usernameChanged && userData.password) {
          await logout();
          await login(userData.username, userData.password);
          return true;
        } else {
          const updatedUser = response.data;
          
          if (usernameChanged) {
            setTimeout(() => {
              logout();
            }, 3000);
            return { usernameChanged: true };
          } else {
            setCurrentUser({
              ...currentUser,
              ...updatedUser
            });
          }
          
          return true;
        }
      } else {
        setError('Error updating user data');
        return false;
      }
    } catch (err) {
      console.error("Update user error:", err);
      if (err.response && err.response.status === 404) {
        setError('User not found.');
      } else if (err.response && err.response.status === 400) {
        setError('Invalid data provided.');
      } else if (err.response && err.response.data) {
        setError(err.response.data);
      } else {
        setError('Error updating user data. Please try again later.');
      }
      return false;
    } finally {
      setLoading(false);
    }
  };

  const value = {
    currentUser,
    token,
    loading,
    error,
    login,
    register,
    logout,
    isAuthenticated,
    updateUserData,
    clearError
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}; 