import React, { useState, useContext, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { AuthContext } from '../../contexts/AuthContext';
import styles from './Auth.module.css';

const Register = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [formError, setFormError] = useState('');
  const { register, error, isAuthenticated, clearError } = useContext(AuthContext);
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
  }, [username, password, confirmPassword]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!username.trim()) {
      setFormError('Please enter a username');
      return;
    }
    
    if (!password) {
      setFormError('Please enter a password');
      return;
    }
    
    if (!confirmPassword) {
      setFormError('Please confirm your password');
      return;
    }
    
    if (password !== confirmPassword) {
      setFormError('Passwords do not match');
      return;
    }
    
    if (password.length < 6) {
      setFormError('Password must be at least 6 characters long');
      return;
    }
    
    const success = await register(username, password);
    if (success) {
      navigate('/login');
    }
  };

  return (
    <div className={styles.authContainer}>
      <div className={styles.authCard}>
        <h1 className={styles.authTitle}>Register</h1>
        
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
              className={`${styles.formControl} ${formError && formError.includes('username') ? styles.inputError : ''}`}
              placeholder="Choose a username"
              required
            />
          </div>
          
          <div className={styles.formGroup}>
            <label htmlFor="password" className={styles.formLabel}>Password</label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className={`${styles.formControl} ${formError && (formError.includes('password') || formError.includes('Password')) ? styles.inputError : ''}`}
              placeholder="Create a password"
              required
            />
            <small className={styles.formText}>Password must be at least 6 characters long</small>
          </div>
          
          <div className={styles.formGroup}>
            <label htmlFor="confirmPassword" className={styles.formLabel}>Confirm Password</label>
            <input
              type="password"
              id="confirmPassword"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              className={`${styles.formControl} ${formError && formError.includes('match') ? styles.inputError : ''}`}
              placeholder="Confirm your password"
              required
            />
          </div>
          
          <button type="submit" className={styles.submitButton}>
            Register
          </button>
        </form>
        
        <div className={styles.authFooter}>
          Already have an account? <Link to="/login" className={styles.authLink}>Login</Link>
        </div>
      </div>
    </div>
  );
};

export default Register; 