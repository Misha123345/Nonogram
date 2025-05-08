import React, { useState, useContext, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { AuthContext } from '../../contexts/AuthContext';
import styles from './Auth.module.css';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [formError, setFormError] = useState('');
  const { login, error, isAuthenticated, clearError } = useContext(AuthContext);
  const navigate = useNavigate();

  useEffect(() => {
    clearError();
    
    if (isAuthenticated()) {
      navigate('/');
    }
  }, [isAuthenticated, navigate, clearError]);

// eslint-disable-next-line react-hooks/exhaustive-deps
  useEffect(() => {
    if (formError) {
      setFormError('');
    }
  }, [username, password]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!username.trim()) {
      setFormError('Please enter your username');
      return;
    }
    
    if (!password) {
      setFormError('Please enter your password');
      return;
    }
    
    const success = await login(username, password);
    if (success) {
      navigate('/');
    }
  };

  return (
    <div className={styles.authContainer}>
      <div className={styles.authCard}>
        <h1 className={styles.authTitle}>Login</h1>
        
        {(error || formError) && (
          <div className={styles.errorMessage}>
            {error || formError}
          </div>
        )}
        
        <form onSubmit={handleSubmit} className={styles.authForm}>
          <div className={styles.formGroup}>
            <label htmlFor="username" className={styles.formLabel}>Username</label>
            <input
              type="text"
              id="username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className={`${styles.formControl} ${formError && !username ? styles.inputError : ''}`}
              placeholder="Enter your username"
            />
          </div>
          
          <div className={styles.formGroup}>
            <label htmlFor="password" className={styles.formLabel}>Password</label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className={`${styles.formControl} ${formError && !password ? styles.inputError : ''}`}
              placeholder="Enter your password"
            />
          </div>
          
          <button type="submit" className={styles.submitButton}>
            Login
          </button>
        </form>
        
        <div className={styles.authFooter}>
          Don't have an account? <Link to="/register" className={styles.authLink}>Register</Link>
        </div>
      </div>
    </div>
  );
};

export default Login; 