import React from 'react';
import RatingStars from '../RatingStars';
import styles from './UserRatingsList.module.css';

const UserRatingsList = ({ userRatings }) => {
  if (!userRatings || userRatings.length === 0) {
    return <div className={styles.noRatings}>No ratings submitted yet</div>;
  }

  return (
    <div className={styles.userRatingsList}>
      <h3>Latest Ratings</h3>
      <div className={styles.ratingsList}>
        {userRatings.map((rating, index) => (
          <div key={index} className={styles.ratingItem}>
            <div className={styles.userInfo}>
              <span className={styles.userName}>{rating.player}</span>
              <span className={styles.ratingDate}>
                {new Date(rating.ratedOn).toLocaleDateString()}
              </span>
            </div>
            <div className={styles.ratingValue}>
              <RatingStars rating={rating.rating} size="small" />
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default UserRatingsList; 