import React from 'react';
import styles from './RatingStars.module.css';

const RatingStars = ({ 
  rating, 
  hoverRating, 
  interactive = false, 
  onRatingChange, 
  onMouseEnter,
  onMouseLeave,
  size = 'medium'
}) => {
  const renderStars = () => {
    const stars = [];
    
    for (let i = 1; i <= 5; i++) {
      const starValue = interactive ? (hoverRating || rating || 0) : rating;
      const filled = i <= starValue;
      
      stars.push(
        <span
          key={i}
          className={`
            ${styles.star} 
            ${filled ? styles.filled : ''} 
            ${styles[size]}
          `}
          onClick={interactive ? () => onRatingChange(i) : undefined}
          onMouseEnter={interactive ? () => onMouseEnter(i) : undefined}
          onMouseLeave={interactive ? onMouseLeave : undefined}
        >
          ★
        </span>
      );
    }
    
    return stars;
  };

  return (
    <div className={`${styles.starsContainer} ${interactive ? styles.interactive : ''}`}>
      {renderStars()}
    </div>
  );
};

export default RatingStars; 