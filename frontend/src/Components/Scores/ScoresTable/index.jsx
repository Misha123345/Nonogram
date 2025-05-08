import React from 'react';
import styles from './ScoresTable.module.css';

const ScoresTable = ({ scores }) => {
  return (
    <div className={styles.scoresTableContainer}>
      <table className={styles.scoresTable}>
        <thead>
          <tr className={styles.scoresTableHeader}>
            <th>Rank</th>
            <th>Player</th>
            <th>Score</th>
            <th>Time</th>
            <th>Date</th>
          </tr>
        </thead>
        <tbody>
          {scores.map((score, index) => (
            <tr key={score.id} className={index < 3 ? styles.topScore : ''}>
              <td>
                {index === 0 && <span className={styles.goldMedal}>🥇</span>}
                {index === 1 && <span className={styles.silverMedal}>🥈</span>}
                {index === 2 && <span className={styles.bronzeMedal}>🥉</span>}
                {index > 2 && `#${index + 1}`}
              </td>
              <td>{score.player}</td>
              <td>{score.score}</td>
              <td>{score.time}</td>
              <td>{score.date}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ScoresTable; 