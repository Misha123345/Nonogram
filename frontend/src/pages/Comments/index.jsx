import React, { useState, useEffect, useContext } from 'react';
import { Link } from 'react-router-dom';
import CommentForm from '../../Components/Comments/CommentForm';
import CommentsList from '../../Components/Comments/CommentsList';
import Loading from '../../Components/Loading';
import { commentApi } from '../../services';
import { AuthContext } from '../../contexts/AuthContext';
import styles from './Comments.module.css';

const Comments = () => {
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState('');
  const [loading, setLoading] = useState(true);
  const { currentUser, isAuthenticated } = useContext(AuthContext);
  const gameName = 'puzzle';

  useEffect(() => {
    const fetchComments = async () => {
      setLoading(true);
      try {
        const response = await commentApi.getComments(gameName);
        setComments(response.data);
      } catch (error) {
        setComments([]);
      } finally {
        setLoading(false);
      }
    };

    fetchComments();
  }, []);

  const handleCommentChange = (e) => {
    setNewComment(e.target.value);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!isAuthenticated()) {
      alert('Please log in to add a comment');
      return;
    }
    
    if (!newComment.trim()) {
      alert('Please enter a comment');
      return;
    }
    
    const commentObj = {
      player: currentUser.username,
      game: gameName,
      comment: newComment,
      commentedOn: new Date().getTime()
    };
    
    try {
      await commentApi.addComment(commentObj);
      
      const response = await commentApi.getComments(gameName);
      setComments(response.data);
      
      setNewComment('');
    } catch (error) {
      console.error('Error adding comment:', error);
      alert('Failed to add comment. Please try again.');
    }
  };

  return (
    <div className={styles.commentsContainer}>
      <h1 className={styles.title}>Player Comments</h1>
      
      {isAuthenticated() ? (
        <CommentForm 
          name={currentUser.username}
          comment={newComment}
          onCommentChange={handleCommentChange}
          onSubmit={handleSubmit}
          isLoggedIn={true}
        />
      ) : (
        <div className={styles.authPrompt}>
          <p>
            <Link to="/login" className={styles.authLink}>Log in</Link> or{' '}
            <Link to="/register" className={styles.authLink}>Register</Link>{' '}
            to add your comment.
          </p>
        </div>
      )}
      
      {loading ? (
        <Loading text="Loading comments..." />
      ) : (
        <CommentsList comments={comments} />
      )}
    </div>
  );
};

export default Comments; 