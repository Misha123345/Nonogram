import React from 'react';
import RatingStars from '../RatingStars';
import styles from './RatingForm.module.css';

const RatingForm = ({ 
  rating, 
  hoverRating, 
  feedback,
  isEditing = false,
  onRatingChange, 
  onMouseEnter, 
  onMouseLeave, 
  onFeedbackChange, 
  onSubmit,
  onCancel
}) => {
  return (
    <div className={styles.ratingForm}>
      <h2>{isEditing ? 'Edit Your Rating' : 'Rate This Game'}</h2>
      <form onSubmit={onSubmit}>
        <div className={styles.ratingStarsInput}>
          <RatingStars
            rating={rating}
            hoverRating={hoverRating}
            interactive={true}
            onRatingChange={onRatingChange}
            onMouseEnter={onMouseEnter}
            onMouseLeave={onMouseLeave}
            size="large"
          />
          <div className={styles.ratingInstruction}>
            Click on a star to rate the game from 1 to 5
          </div>
        </div>
        
        <div className={styles.formActions}>
          <button 
            type="submit" 
            className={styles.submitButton}
            disabled={rating === 0}
          >
            {isEditing ? 'Update Rating' : 'Submit Rating'}
          </button>
          
          {onCancel && (
            <button 
              type="button" 
              className={styles.cancelButton}
              onClick={onCancel}
            >
              Cancel
            </button>
          )}
        </div>
      </form>
    </div>
  );
};

export default RatingForm; 