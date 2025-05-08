import axios from 'axios';

// Axios instance with direct connection to the API server
const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  },
  timeout: 10000 // 10 seconds timeout
});

// Add Authorization header with JWT token to all requests
apiClient.interceptors.request.use(request => {
  const token = localStorage.getItem('jwtToken');
  if (token) {
    request.headers['Authorization'] = `Bearer ${token}`;
  }
  return request;
});

// Handle responses
apiClient.interceptors.response.use(
  response => {
    return response;
  },
  error => {
    return Promise.reject(error);
  }
);

export default apiClient; 