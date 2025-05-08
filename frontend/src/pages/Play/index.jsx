import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import styles from './Play.module.css';
import { gameApi } from '../../services';
import Loading from '../../Components/Loading';

const Play = () => {
  const navigate = useNavigate();
  const [hasSavedGame, setHasSavedGame] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    // Check for saved game when component loads
    checkForSavedGame();
  }, []);

  const checkForSavedGame = async () => {
    setIsLoading(true);
    try {
      const response = await gameApi.hasSavedGame();
      setHasSavedGame(response.data === true);
    } catch (error) {
      console.error('Error checking for saved game:', error);
      setHasSavedGame(false);
    } finally {
      setIsLoading(false);
    }
  };

  const handleDifficultySelect = (difficulty) => {
    navigate(`/game/${difficulty}`);
  };

  const continueSavedGame = () => {
    navigate('/game/saved');
  };

  const deleteSavedGame = async (e) => {
    e.stopPropagation();
    
    try {
      await gameApi.deleteSavedGame();
      setHasSavedGame(false);
    } catch (error) {
      console.error('Error deleting saved game:', error);
    }
  };

  return (
    <div className={styles.playContainer}>
      <h1 className={styles.title}>Select Difficulty</h1>
      
      {isLoading ? (
        <Loading text="Checking for saved games..." />
      ) : (
        <>
          <div className={styles.difficultyCards}>
            <div 
              className={styles.difficultyCard} 
              onClick={() => handleDifficultySelect('easy')}
            >
              <h2>Easy</h2>
              <p>5x5 puzzles</p>
              <p>Perfect for beginners</p>
            </div>
            
            <div 
              className={styles.difficultyCard} 
              onClick={() => handleDifficultySelect('medium')}
            >
              <h2>Medium</h2>
              <p>10x10 puzzles</p>
              <p>For experienced players</p>
            </div>
            
            <div 
              className={styles.difficultyCard} 
              onClick={() => handleDifficultySelect('hard')}
            >
              <h2>Hard</h2>
              <p>15x15 puzzles</p>
              <p>For expert players</p>
            </div>
            
            <div 
              className={styles.difficultyCard} 
              onClick={() => handleDifficultySelect('random')}
            >
              <h2>Random</h2>
              <p>Surprise yourself</p>
              <p>Any size, any difficulty</p>
            </div>
          </div>

          {hasSavedGame && (
            <div 
              className={`${styles.savedGameCard} ${styles.difficultyCard}`}
              onClick={continueSavedGame}
            >
              <h2>Continue Saved Game</h2>
              <p>Return to your unfinished game</p>
              <button 
                className={styles.deleteButton}
                onClick={deleteSavedGame}
              >
                Delete
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default Play; 