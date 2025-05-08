import React from 'react';
import Lives from '../Lives';
import styles from './GameHeader.module.css';

const GameHeader = ({ timer, lives, onBackClick }) => {
  return (
    <div className={styles.header}>
      <button onClick={onBackClick} className={styles.backButton}>
        ⬅ Back
      </button>
      
      <div className={styles.gameInfo}>
        <div className={styles.timer}>⏱️ {timer}</div>
        <Lives lives={lives} />
      </div>
    </div>
  );
};

export default GameHeader; 