import apiClient from './axiosConfig';

// Comment API
const commentApi = {
  // Get comments for a game
  getComments: (game) => {
    return apiClient.get(`/comment/${game}`);
  },
  
  // Add a new comment
  addComment: (comment) => {
    return apiClient.post(`/comment`, comment);
  }
};

export default commentApi; 