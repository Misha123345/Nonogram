import React from 'react';
import CommentItem from '../CommentItem';
import styles from './CommentsList.module.css';

const CommentsList = ({ comments }) => {
  return (
    <div className={styles.commentsList}>
      <h2>Recent Comments</h2>
      
      {comments.length > 0 ? (
        comments.map((comment) => (
          <CommentItem key={comment.id} comment={comment} />
        ))
      ) : (
        <p className={styles.noComments}>No comments yet. Be the first to comment!</p>
      )}
    </div>
  );
};

export default CommentsList; 