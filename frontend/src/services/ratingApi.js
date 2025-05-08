import apiClient from './axiosConfig';

// Rating API
const ratingApi = {
  // Get rating for a specific player and game
  getRating: (game, player) => {
    return apiClient.get(`/rating/${game}/${player}`);
  },

  // Get average rating for a game
  getAverageRating: (game) => {
    return apiClient.get(`/rating/${game}`);
  },
  
  // Get all ratings for a game with usernames
  getAllRatings: (game) => {
    return apiClient.get(`/rating/all/${game}`);
  },
  
  // Set rating
  setRating: (rating) => {
    return apiClient.post(`/rating`, rating);
  }
};

export default ratingApi; 