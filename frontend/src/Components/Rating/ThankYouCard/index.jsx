import React from 'react';
import RatingStars from '../RatingStars';
import styles from './ThankYouCard.module.css';

const ThankYouCard = ({ userRating, feedback }) => {
  return (
    <div className={styles.thankYouCard}>
      <h2>Thank You for Your Rating!</h2>
      <div className={styles.userRatingDisplay}>
        <p>Your rating: </p>
        <RatingStars rating={userRating} size="small" />
      </div>
      {feedback && (
        <div className={styles.userFeedback}>
          <p>Your feedback:</p>
          <div className={styles.feedbackText}>{feedback}</div>
        </div>
      )}
    </div>
  );
};

export default ThankYouCard;
