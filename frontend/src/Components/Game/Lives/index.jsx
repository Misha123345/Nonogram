import React from 'react';
import styles from './Lives.module.css';

const Lives = ({ lives }) => {
  const hearts = [];
  for (let i = 0; i < 3; i++) {
    if (i < lives) {
      hearts.push(
        <span 
          key={i} 
          className={styles.heart}
        >
          ❤️
        </span>
      );
    } else {
      hearts.push(
        <span 
          key={i} 
          className={`${styles.heart} ${styles.emptyHeart}`}
        >
          🖤
        </span>
      );
    }
  }

  return (
    <div className={styles.lives}>
      {hearts}
    </div>
  );
};

export default Lives; 