import React from 'react';
import styles from './CommentForm.module.css';

const CommentForm = ({ name, comment, onNameChange, onCommentChange, onSubmit, isLoggedIn = false }) => {
  return (
    <div className={styles.commentForm}>
      <h2>Add Your Comment</h2>
      <form onSubmit={onSubmit}>
        {!isLoggedIn ? (
          <div className={styles.formGroup}>
            <label htmlFor="name">Your Name</label>
            <input
              type="text"
              id="name"
              value={name}
              onChange={onNameChange}
              placeholder="Enter your name"
              required
            />
          </div>
        ) : (
          <div className={styles.formGroup}>
            <label htmlFor="name">Posting as</label>
            <div className={styles.usernameBadge}>{name}</div>
          </div>
        )}
        
        <div className={styles.formGroup}>
          <label htmlFor="comment">Your Comment</label>
          <textarea
            id="comment"
            value={comment}
            onChange={onCommentChange}
            placeholder="Share your thoughts about the game"
            rows="4"
            required
          ></textarea>
        </div>
        
        <button type="submit" className={styles.submitButton}>
          Post Comment
        </button>
      </form>
    </div>
  );
};

export default CommentForm; 