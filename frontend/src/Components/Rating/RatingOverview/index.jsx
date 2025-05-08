import React from 'react';
import RatingStars from '../RatingStars';
import RatingDistribution from '../RatingDistribution';
import styles from './RatingOverview.module.css';

const RatingOverview = ({ averageRating, totalRatings, distribution }) => {
  return (
    <div className={styles.ratingOverview}>
      <div className={styles.averageRating}>
        <div className={styles.ratingNumber}>{averageRating.toFixed(1)}</div>
        <div className={styles.ratingStars}>
          <RatingStars rating={Math.round(averageRating)} />
        </div>
        <div className={styles.ratingCount}>
          Based on {totalRatings} {totalRatings === 1 ? 'rating' : 'ratings'}
        </div>
      </div>
      
      <RatingDistribution 
        distribution={distribution} 
        totalRatings={totalRatings} 
      />
    </div>
  );
};

export default RatingOverview; 