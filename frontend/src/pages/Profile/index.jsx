import React, { useState, useContext, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../../contexts/AuthContext';
import styles from './Auth.module.css';

const Profile = () => {
  const [username, setUsername] = useState('');
  const [newUsername, setNewUsername] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [formError, setFormError] = useState('');
  const [formSuccess, setFormSuccess] = useState('');
  const [showPasswordForm, setShowPasswordForm] = useState(false);
  const [showUsernameForm, setShowUsernameForm] = useState(false);
  const { currentUser, updateUserData, isAuthenticated, error } = useContext(AuthContext);
  const navigate = useNavigate();

  useEffect(() => {
    if (!isAuthenticated()) {
      navigate('/login');
    } else {
      setUsername(currentUser.username || '');
    }
  }, [isAuthenticated, navigate, currentUser]);

  useEffect(() => {
    if (formSuccess) {
      const timer = setTimeout(() => {
        setFormSuccess('');
      }, 5000);
      
      return () => clearTimeout(timer);
    }
  }, [formSuccess]);

  // eslint-disable-next-line react-hooks/exhaustive-deps
  useEffect(() => {
    if (formError) {
      setFormError('');
    }
  }, [newUsername, password, confirmPassword]);

  const handlePasswordChange = async (e) => {
    e.preventDefault();
    setFormError('');
    setFormSuccess('');
    
    if (!password || !confirmPassword) {
      setFormError('Both password fields must be filled');
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
    
    const userData = {
      username: currentUser.username,
      password: password
    };
    
    const success = await updateUserData(userData);
    if (success) {
      setFormSuccess('Password successfully updated');
      setPassword('');
      setConfirmPassword('');
      setShowPasswordForm(false);
    }
  };

  const handleUsernameChange = async (e) => {
    e.preventDefault();
    setFormError('');
    setFormSuccess('');
    
    if (!newUsername.trim()) {
      setFormError('Username cannot be empty');
      return;
    }
    
    if (newUsername.length < 3) {
      setFormError('Username must be at least 3 characters long');
      return;
    }
    
    if (newUsername === currentUser.username) {
      setFormError('New username is the same as current username');
      return;
    }
    
    if (!window.confirm('Changing your username will require you to log in again. Continue?')) {
      return;
    }
    
    const userData = {
      username: newUsername
    };
    
    const result = await updateUserData(userData);
    
    if (result === true || (result && result.usernameChanged)) {
      setFormSuccess('Username successfully updated. Please log in again with your new username.');
      setNewUsername('');
      setShowUsernameForm(false);
    }
  };

  const togglePasswordForm = () => {
    setShowPasswordForm(!showPasswordForm);
    setShowUsernameForm(false);
    setFormError('');
    setPassword('');
    setConfirmPassword('');
  };

  const toggleUsernameForm = () => {
    setShowUsernameForm(!showUsernameForm);
    setShowPasswordForm(false);
    setFormError('');
    setNewUsername('');
  };

  if (!isAuthenticated()) {
    return null;
  }

  return (
    <div className={styles.authContainer}>
      <div className={styles.authCard}>
        <h1 className={styles.authTitle}>Profile</h1>
        
        <div className={styles.profileInfo}>
          <h2>Username: {username}</h2>
        </div>
        
        {formError && (
          <div className={styles.errorMessage}>
            {formError}
          </div>
        )}
        
        {error && (
          <div className={styles.errorMessage}>
            {error}
          </div>
        )}
        
        {formSuccess && (
          <div className={styles.successMessage}>
            {formSuccess}
          </div>
        )}
        
        <div className={styles.actionButtons}>
          <button 
            className={`${styles.actionButton} ${showPasswordForm ? styles.activeButton : ''}`} 
            onClick={togglePasswordForm}
          >
            {showPasswordForm ? 'Hide' : 'Change Password'}
          </button>
          
          <button 
            className={`${styles.actionButton} ${showUsernameForm ? styles.activeButton : ''}`} 
            onClick={toggleUsernameForm}
          >
            {showUsernameForm ? 'Hide' : 'Change Username'}
          </button>
        </div>
        
        {showPasswordForm && (
          <form onSubmit={handlePasswordChange} className={styles.authForm}>
            <div className={styles.formGroup}>
              <label htmlFor="password" className={styles.formLabel}>New Password</label>
              <input
                type="password"
                id="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className={`${styles.formControl} ${formError && formError.includes('Password') ? styles.inputError : ''}`}
                placeholder="Enter new password"
              />
            </div>
            
            <div className={styles.formGroup}>
              <label htmlFor="confirmPassword" className={styles.formLabel}>Confirm Password</label>
              <input
                type="password"
                id="confirmPassword"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                className={`${styles.formControl} ${formError && formError.includes('match') ? styles.inputError : ''}`}
                placeholder="Confirm new password"
              />
            </div>
            
            <button type="submit" className={styles.submitButton}>
              Save Password
            </button>
          </form>
        )}
        
        {showUsernameForm && (
          <form onSubmit={handleUsernameChange} className={styles.authForm}>
            <div className={styles.formGroup}>
              <label htmlFor="newUsername" className={styles.formLabel}>New Username</label>
              <input
                type="text"
                id="newUsername"
                value={newUsername}
                onChange={(e) => setNewUsername(e.target.value)}
                className={`${styles.formControl} ${formError && formError.includes('username') ? styles.inputError : ''}`}
                placeholder="Enter new username"
              />
            </div>
            
            <button type="submit" className={styles.submitButton}>
              Save Username
            </button>
          </form>
        )}
      </div>
    </div>
  );
};

export default Profile; 