import React, { useState, useContext, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../../../contexts/AuthContext';
import styles from './CompletionMessage.module.css';

const CompletionMessage = ({ time, score, onPlayAgain, onSaveScore }) => {
  const [playerName, setPlayerName] = useState('');
  const [saveStatus, setSaveStatus] = useState(''); // for displaying save status
  const [isSaved, setIsSaved] = useState(false);
  const { currentUser, isAuthenticated } = useContext(AuthContext);
  const navigate = useNavigate();
  const hasAutoSaved = useRef(false);
  
  useEffect(() => {
    if (isAuthenticated() && currentUser && !hasAutoSaved.current) {
      const autoSaveScore = async () => {
        try {
          hasAutoSaved.current = true; // Set flag before save attempt
          await onSaveScore(currentUser.username);
          setSaveStatus('Score saved automatically!');
          setIsSaved(true);
        } catch (error) {
          console.error('Error auto-saving score:', error);
          setSaveStatus('Error saving score. :(');
          hasAutoSaved.current = false; // Reset flag in case of error
        }
      };
      
      autoSaveScore();
    }
  }, [isAuthenticated, currentUser, onSaveScore]);
  
  const handleLogin = () => {
    navigate('/login');
  };
  
  const handleSaveScore = async () => {
    if (!playerName.trim()) {
      setSaveStatus('Please enter your name');
      return;
    }
    
    // Don't save again if already saved
    if (isSaved) {
      setSaveStatus('Score already saved!');
      return;
    }
    
    try {
      await onSaveScore(playerName);
      setSaveStatus('Score saved successfully!');
      setIsSaved(true);
    } catch (error) {
      console.error('Error saving score:', error);
      setSaveStatus('Error saving score. Please try again.');
    }
  };
  
  return (
    <div className={styles.completionMessage}>
      <h2>Puzzle Solved!</h2>
      <p>Time: {time}</p>
      <p>Score: {score}</p>
      
      {!isAuthenticated() ? (
        !isSaved ? (
          <>
            <div className={styles.scoreForm}>
              <h3>Save Your Score</h3>
              <div className={styles.inputGroup}>
                <input
                  type="text"
                  value={playerName}
                  onChange={(e) => setPlayerName(e.target.value)}
                  placeholder="Enter your name"
                  className={styles.nameInput}
                />
                <button 
                  className={styles.saveButton} 
                  onClick={handleSaveScore}
                >
                  Save
                </button>
              </div>
              {saveStatus && <p className={styles.saveStatus}>{saveStatus}</p>}
            </div>
            <p className={styles.loginPrompt}>
              <button onClick={handleLogin} className={styles.loginButton}>
                Login
              </button>
              to automatically save your scores
            </p>
          </>
        ) : (
          <p className={styles.saveStatus}>{saveStatus}</p>
        )
      ) : (
        <p className={styles.saveStatus}>{saveStatus}</p>
      )}
      
      <button className={styles.newGameButton} onClick={onPlayAgain}>
        Play Again
      </button>
    </div>
  );
};

export default CompletionMessage; 