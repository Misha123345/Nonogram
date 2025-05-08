import React from 'react';
import styles from './ScoresTabs.module.css';

const ScoresTabs = ({ activeTab, onTabChange }) => {
  const difficulties = ['easy', 'medium', 'hard', 'random'];
  
  return (
    <div className={styles.tabs}>
      {difficulties.map(difficulty => (
        <button 
          key={difficulty}
          className={`${styles.tabButton} ${activeTab === difficulty ? styles.activeTab : ''}`}
          onClick={() => onTabChange(difficulty)}
        >
          {difficulty.charAt(0).toUpperCase() + difficulty.slice(1)}
        </button>
      ))}
    </div>
  );
};

export default ScoresTabs; 