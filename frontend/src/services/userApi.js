import apiClient from './axiosConfig';

// User API
const userApi = {
  // Register a new user
  register: (username, password) => {
    return apiClient.post('/user/register', { username, password });
  },
  
  // Login user
  login: (username, password) => {
    return apiClient.post('/user/login', { username, password });
  },
  
  // Get user by username
  getUser: (username) => {
    return apiClient.get(`/user/${username}`);
  },
  
  // Check if username exists
  userExists: (username) => {
    return apiClient.get(`/user/exists/${username}`);
  },
  
  // Get all users
  getAllUsers: () => {
    return apiClient.get('/user/all');
  },
  
  // Update user
  updateUser: (userData) => {
    return apiClient.put('/user/update', userData);
  }
};

export default userApi; 