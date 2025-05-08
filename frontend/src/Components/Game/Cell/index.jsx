import React from 'react';
import styles from './Cell.module.css';

const Cell = ({ filled, completed, onClick }) => {
  return (
    <div 
      className={`${styles.cell} ${filled ? styles.filled : ''} ${completed ? styles.completed : ''}`}
      onClick={onClick}
    >
    </div>
  );
};

export default Cell; 