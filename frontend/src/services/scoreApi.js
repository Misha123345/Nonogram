import apiClient from './axiosConfig';

// Score API
const scoreApi = {
  // Get top scores for a game
  getTopScores: (game) => {
    return apiClient.get(`/score/${game}`);
  },
  
  // Add a new score
  addScore: (score) => {
    return apiClient.post(`/score`, score);
  }
};

export default scoreApi; 