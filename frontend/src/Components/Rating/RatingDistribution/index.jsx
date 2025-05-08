import React from 'react';
import styles from './RatingDistribution.module.css';

const RatingDistribution = ({ distribution, totalRatings }) => {
  const calculatePercentage = (count) => {
    return totalRatings > 0 ? Math.round((count / totalRatings) * 100) : 0;
  };
  
  return (
    <div className={styles.ratingDistribution}>
      {distribution.map((count, index) => (
        <div key={index} className={styles.ratingBar}>
          <div className={styles.ratingLabel}>{5 - index} ★</div>
          <div className={styles.ratingBarContainer}>
            <div 
              className={styles.ratingBarFill} 
              style={{ width: `${calculatePercentage(distribution[4 - index])}%` }}
            ></div>
          </div>
          <div className={styles.ratingPercent}>
            {calculatePercentage(distribution[4 - index])}%
          </div>
        </div>
      ))}
    </div>
  );
};

export default RatingDistribution; 