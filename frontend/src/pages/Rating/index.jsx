import React, { useState, useEffect, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import RatingOverview from '../../Components/Rating/RatingOverview';
import RatingForm from '../../Components/Rating/RatingForm';
import ThankYouCard from '../../Components/Rating/ThankYouCard';
import UserRatingsList from '../../Components/Rating/UserRatingsList';
import Loading from '../../Components/Loading';
import { ratingApi } from '../../services';
import { AuthContext } from '../../contexts/AuthContext';
import styles from './Rating.module.css';

const Rating = () => {
  const [rating, setRating] = useState(0);
  const [userRating, setUserRating] = useState(0);
  const [hoverRating, setHoverRating] = useState(0);
  const [feedback, setFeedback] = useState('');
  const [submitted, setSubmitted] = useState(false);
  const [loading, setLoading] = useState(true);
  const [averageRating, setAverageRating] = useState(0);
  const [totalRatings, setTotalRatings] = useState(0);
  const [ratingDistribution, setRatingDistribution] = useState([0, 0, 0, 0, 0]);
  const [userRatings, setUserRatings] = useState([]);
  const [isEditing, setIsEditing] = useState(false);
  const { currentUser, isAuthenticated } = useContext(AuthContext);
  const navigate = useNavigate();
  const gameName = 'puzzle'; // Game name

  useEffect(() => {
    const fetchRatings = async () => {
      setLoading(true);
      try {
        // Get average rating
        const avgResponse = await ratingApi.getAverageRating(gameName);
        const avgRating = avgResponse.data || 0;
        
        setAverageRating(avgRating);
        
        if (isAuthenticated() && currentUser) {
          try {
            const userRatingResponse = await ratingApi.getRating(gameName, currentUser.username);
            setUserRating(userRatingResponse.data);
            setRating(userRatingResponse.data);
            
            if (userRatingResponse.data > 0) {
              setSubmitted(true);
            }
          } catch (error) {
            console.log('User has not rated yet');
          }
        }
        
        // Get all ratings to display in the list and calculate distribution
        const allRatingsResponse = await ratingApi.getAllRatings(gameName);
        const allRatings = allRatingsResponse.data;
        
        if (allRatings && allRatings.length > 0) {
          setUserRatings(allRatings);
          setTotalRatings(allRatings.length);
          
          // Calculate rating distribution
          const distribution = [0, 0, 0, 0, 0];
          allRatings.forEach(item => {
            if (item.rating >= 1 && item.rating <= 5) {
              distribution[item.rating - 1]++;
            }
          });
          
          setRatingDistribution(distribution);
        } else {
          // Default values if no ratings
          setTotalRatings(0);
          setRatingDistribution([0, 0, 0, 0, 0]);
        }
      } catch (error) {
        console.error('Error fetching ratings:', error);
        // Set default values
        setAverageRating(0);
        setTotalRatings(0);
      } finally {
        setLoading(false);
      }
    };

    fetchRatings();
  }, [gameName, isAuthenticated, currentUser]);

  const handleRatingChange = (newRating) => {
    setRating(newRating);
  };

  const handleMouseEnter = (starRating) => {
    setHoverRating(starRating);
  };

  const handleMouseLeave = () => {
    setHoverRating(0);
  };

  const handleFeedbackChange = (e) => {
    setFeedback(e.target.value);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (rating === 0) {
      alert('Please select a rating');
      return;
    }
    
    if (!isAuthenticated()) {
      // Если пользователь не авторизован, перенаправляем на страницу входа
      navigate('/login');
      return;
    }
    
    try {
      // Send rating to server
      await ratingApi.setRating({
        game: gameName,
        player: currentUser.username,
        rating: rating
      });
      
      // Get updated average rating
      const avgResponse = await ratingApi.getAverageRating(gameName);
      const newAvgRating = avgResponse.data || 0;
      
      // Get updated ratings list
      const allRatingsResponse = await ratingApi.getAllRatings(gameName);
      const allRatings = allRatingsResponse.data || [];
      
      setUserRating(rating);
      setSubmitted(true);
      setIsEditing(false);
      setAverageRating(newAvgRating);
      setUserRatings(allRatings);
      setTotalRatings(allRatings.length);
      
      // Update local distribution
      const distribution = [0, 0, 0, 0, 0];
      allRatings.forEach(item => {
        if (item.rating >= 1 && item.rating <= 5) {
          distribution[item.rating - 1]++;
        }
      });
      
      setRatingDistribution(distribution);
    } catch (error) {
      console.error('Error submitting rating:', error);
      alert('Failed to submit rating. Please try again.');
    }
  };

  const handleEditRating = () => {
    setIsEditing(true);
  };

  const handleCancel = () => {
    setIsEditing(false);
    setRating(userRating);
  };

  const handleAddNewRating = () => {
    const newName = prompt('Enter a new username to add another rating:');
    if (newName) {
      // Reset form state
      setSubmitted(false);
      setRating(0);
      setFeedback('');
      setHoverRating(0);
      
      localStorage.setItem('username', newName);
    }
  };

  return (
    <div className={styles.ratingContainer}>
      <h1 className={styles.title}>Game Rating</h1>
      
      {loading ? (
        <Loading text="Loading ratings..." />
      ) : (
        <div className={styles.ratingContent}>
          <RatingOverview 
            averageRating={averageRating}
            totalRatings={totalRatings}
            distribution={ratingDistribution}
          />
          
          {!isAuthenticated() ? (
            <div className={styles.authMessage}>
              <p>You need to <a href="/login">login</a> to rate this game.</p>
            </div>
          ) : submitted && !isEditing ? (
            <div>
              <ThankYouCard userRating={userRating} feedback={feedback} />
              <div className={styles.ratingActions}>
                <button 
                  className={styles.editButton} 
                  onClick={handleEditRating}
                >
                  Edit Your Rating
                </button>
              </div>
            </div>
          ) : (
            <RatingForm
              rating={rating}
              hoverRating={hoverRating}
              feedback={feedback}
              isEditing={isEditing}
              onRatingChange={handleRatingChange}
              onMouseEnter={handleMouseEnter}
              onMouseLeave={handleMouseLeave}
              onFeedbackChange={handleFeedbackChange}
              onSubmit={handleSubmit}
              onCancel={isEditing ? handleCancel : null}
            />
          )}
          
          <UserRatingsList userRatings={userRatings} />
        </div>
      )}
    </div>
  );
};

export default Rating; 