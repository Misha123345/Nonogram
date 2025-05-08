import React, { useState, useEffect, useRef, useContext } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import GameHeader from '../../Components/Game/GameHeader';
import GameBoard from '../../Components/Game/GameBoard';
import CompletionMessage from '../../Components/Game/CompletionMessage';
import Loading from '../../Components/Loading';
import { gameApi, scoreApi } from '../../services';
import { AuthContext } from '../../contexts/AuthContext';
import styles from './Game.module.css';

const Game = () => {
  const { difficulty } = useParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [game, setGame] = useState(null);
  const [boardState, setBoardState] = useState([]);
  const [timer, setTimer] = useState(0);
  const [isCompleted, setIsCompleted] = useState(false);
  const [gameOver, setGameOver] = useState(false);
  const [lives, setLives] = useState(3); 
  const [score, setScore] = useState(0);
  const [startTime, setStartTime] = useState(null);
  const [isProcessingMove, setIsProcessingMove] = useState(false);
  const [originalDifficulty, setOriginalDifficulty] = useState(difficulty);
  
  const { isAuthenticated } = useContext(AuthContext);
  
  const timerInterval = useRef(null);

  const getBoardSize = () => {
    switch(difficulty) {
      case 'easy': return 5;
      case 'medium': return 10;
      case 'hard': return 15;
      case 'random': return Math.floor(Math.random() * 10) + 5;
      default: return 5;
    }
  };

  const handleBeforeUnload = () => {
    if (!isCompleted && !gameOver && isAuthenticated() && timer > 0) {
      saveGameState(timer);
    }
  };

  const saveGameState = async (timeElapsed) => {
    try {
      await gameApi.saveGame(timeElapsed, originalDifficulty);
    } catch (error) {
      console.error('Error saving game:', error);
    }
  };

  const stopTimer = () => {
    if (timerInterval.current) {
      clearInterval(timerInterval.current);
      timerInterval.current = null;
    }
  };

  useEffect(() => {
    if (!isAuthenticated()) {
      setLoading(false);
      return;
    }
    
    window.addEventListener('beforeunload', handleBeforeUnload);
    
    const loadGame = async () => {
      setLoading(true);
      try {
        let gameData;
        
        if (difficulty === 'saved') {
          const response = await gameApi.loadSavedGame();
          gameData = response.data;
          
          if (gameData.originalDifficulty) {
            setOriginalDifficulty(gameData.originalDifficulty);
          }
          
          if (gameData.timeElapsed !== undefined && gameData.timeElapsed !== null) {
            setTimer(parseInt(gameData.timeElapsed, 10));
            const calculatedStartTime = Date.now() - parseInt(gameData.timeElapsed, 10) * 1000;
            setStartTime(calculatedStartTime);
          } else {
            setStartTime(Date.now());
          }
        } else {
          const size = getBoardSize();
          const livesCount = 3;
          
          const response = await gameApi.getNewGame(size, livesCount);
          gameData = response.data;
          
          setOriginalDifficulty(difficulty);
          setStartTime(Date.now());
        }
        
        setLives(gameData.lives);
        
        const emptyBoardState = Array(gameData.rows).fill().map(() => 
          Array(gameData.columns).fill(false)
        );
        
        let initialBoardState;
        if (difficulty === 'saved' && gameData.field) {
          initialBoardState = Array(gameData.rows).fill().map((_, r) => 
            Array(gameData.columns).fill().map((_, c) => 
              gameData.field[r][c].marked
            )
          );
        } else {
          initialBoardState = emptyBoardState;
        }
        
        const gameObj = {
          size: gameData.rows,
          solution: null,
          rowHints: gameData.legend.rows,
          colHints: gameData.legend.columns
        };
        
        setGame(gameObj);
        setBoardState(initialBoardState);
        setIsCompleted(gameData.state === 'SOLVED');
        setGameOver(gameData.state === 'FAILED');
        setScore(gameData.score || 0);
        
        if (gameData.state === 'SOLVED' || gameData.state === 'FAILED') {
          stopTimer();
        }
      } catch (error) {
        console.error('Error loading game:', error);
        alert('Failed to load game. Please try again.');
        navigate('/play');
      } finally {
        setLoading(false);
      }
    };
    
    loadGame();
    
    return () => {
      window.removeEventListener('beforeunload', handleBeforeUnload);
      stopTimer();
    };
  }, [difficulty, isAuthenticated]);

  useEffect(() => {
    if (loading || isCompleted || gameOver || !startTime) {
      return;
    }
    
    timerInterval.current = setInterval(() => {
      const elapsedSeconds = Math.floor((Date.now() - startTime) / 1000);
      setTimer(elapsedSeconds);
      
      if (elapsedSeconds > 0 && elapsedSeconds % 10 === 0 && isAuthenticated()) {
        saveGameState(elapsedSeconds);
      }
    }, 1000);
    
    return () => {
      stopTimer();
    };
  }, [loading, isCompleted, gameOver, startTime, isAuthenticated]);
  
  useEffect(() => {
    return () => {
      if (!isCompleted && !gameOver && isAuthenticated() && timer > 0) {
        saveGameState(timer);
      }
    };
  }, [timer, isCompleted, gameOver, isAuthenticated]);

  const handleCellClick = async (rowIndex, colIndex) => {
    if (isCompleted || gameOver || isProcessingMove) return;
    
    try {
      setIsProcessingMove(true);
      
      const response = await gameApi.makeMove(rowIndex, colIndex, originalDifficulty);
      const gameData = response.data;
      
      setLives(gameData.lives);
      
      const newBoardState = Array(gameData.rows).fill().map((_, r) => 
        Array(gameData.columns).fill().map((_, c) => 
          gameData.field[r][c].marked
        )
      );
      
      setBoardState(newBoardState);
      
      if (gameData.state === 'SOLVED') {
        setIsCompleted(true);
        stopTimer();
        setScore(gameData.score);
        
        try {
          await gameApi.deleteSavedGame();
        } catch (error) {
          console.error('Error deleting saved game:', error);
        }
      } else if (gameData.state === 'FAILED') {
        setGameOver(true);
        stopTimer();
        
        try {
          await gameApi.deleteSavedGame();
        } catch (error) {
          console.error('Error deleting saved game:', error);
        }
      } else {
        try {
          await saveGameState(timer);
        } catch (error) {
          console.error('Error saving game state after move:', error);
        }
      }
    } catch (error) {
      console.error('Error making move:', error);
    } finally {
      setIsProcessingMove(false);
    }
  };

  // Save score
  const saveScore = async (playerName) => {
    try {
      const difficultyForScore = difficulty === 'saved' ? originalDifficulty : difficulty;
      
      const scoreData = {
        game: 'puzzle-' + difficultyForScore,
        player: playerName,
        points: score,
        playedOn: new Date().toISOString()
      };
      
      await scoreApi.addScore(scoreData);
      return true;
    } catch (error) {
      console.error('Error saving score:', error);
      throw error;
    }
  };

  // Format time
  const formatTime = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs < 10 ? '0' + secs : secs}`;
  };

  // Return to difficulty selection
  const handleBackClick = () => {
    navigate('/play');
  };

  if (loading) {
    return <Loading text="Loading game..." />;
  }

  // If user is not authenticated, show login message
  if (!isAuthenticated()) {
    return (
      <div className={styles.gameContainer}>
        <h1 className={styles.title}>Nonogram Puzzle</h1>
        <div className={styles.authRequired}>
          <h2>Authentication Required</h2>
          <p>You need to be logged in to play the game.</p>
          <div className={styles.authButtons}>
            <Link to="/login" className={styles.loginButton}>Login</Link>
            <Link to="/register" className={styles.registerButton}>Register</Link>
          </div>
          <Link to="/play" className={styles.backButton}>Back to Menu</Link>
        </div>
      </div>
    );
  }

  return (
    <div className={styles.gameContainer}>
      <h1 className={styles.title}>
        Nonogram Puzzle - {difficulty === 'saved' ? `Saved Game (${originalDifficulty})` : difficulty}
      </h1>
      
      <GameHeader 
        difficulty={difficulty === 'saved' ? originalDifficulty : difficulty}
        timer={formatTime(timer)}
        lives={lives}
        onBackClick={handleBackClick}
      />
      
      <div className={styles.gameContent}>
        {game ? (
          <GameBoard 
            game={game}
            boardState={boardState}
            isCompleted={isCompleted || gameOver}
            onCellClick={handleCellClick}
          />
        ) : (
          <div className={styles.errorMessage}>
            Failed to load game data. Please try again later.
          </div>
        )}
        
        {isCompleted && (
          <CompletionMessage 
            time={formatTime(timer)}
            score={score}
            onPlayAgain={handleBackClick}
            onSaveScore={saveScore}
          />
        )}
        
        {gameOver && (
          <div className={styles.gameOverMessage}>
            <h2>Game Over!</h2>
            <p>You've run out of lives.</p>
            <button className={styles.playAgainButton} onClick={handleBackClick}>
              Try Again
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default Game; 