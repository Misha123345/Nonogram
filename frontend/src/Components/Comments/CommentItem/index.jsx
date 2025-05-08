import React from 'react';
import styles from './CommentItem.module.css';

const CommentItem = ({ comment }) => {
  // Format date to a more readable format
  const formatDate = (dateString) => {
    if (!dateString) return '';
    
    const date = new Date(dateString);
    if (isNaN(date.getTime())) return dateString; // Return original if invalid
    
    return date.toLocaleString('en-US', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      hour12: true
    });
  };

  return (
    <div className={styles.commentCard}>
      <div className={styles.commentHeader}>
        <h3>{comment.player}</h3>
        <span className={styles.commentDate}>{formatDate(comment.commentedOn)}</span>
      </div>
      <p className={styles.commentText}>{comment.comment}</p>
    </div>
  );
};

export default CommentItem; 