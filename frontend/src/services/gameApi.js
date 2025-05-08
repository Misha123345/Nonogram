import apiClient from './axiosConfig';

// Game API
const gameApi = {
  // Get a new game
  getNewGame: (size = 10, livesCount = 3) => {
    return apiClient.get(`/game/puzzle/new`, { 
      params: { 
        size, 
        livesCount 
      } 
    });
  },
  
  makeMove: (row, col, originalDifficulty = null) => {
    const moveData = {
      row: Number(row), 
      col: Number(col) 
    };
    
    if (originalDifficulty) {
      moveData.originalDifficulty = originalDifficulty;
    }
    
    return apiClient.post(
      `/game/puzzle/move`, 
      moveData
    ).catch(error => {
      console.error('Error making move:', error);
      throw error;
    });
  },
  
  saveGame: (timeElapsed, originalDifficulty = null) => {
    const params = {
      timeElapsed: Number(timeElapsed)
    };
    
    if (originalDifficulty) {
      params.originalDifficulty = originalDifficulty;
    }
    
    return apiClient.get(`/game/puzzle/save`, { params });
  },
  
  loadSavedGame: () => {
    return apiClient.get(`/game/puzzle/load`);
  },
  
  deleteSavedGame: () => {
    return apiClient.get(`/game/puzzle/delete`);
  },
  
  hasSavedGame: () => {
    return apiClient.get(`/game/puzzle/has`);
  }
};

export default gameApi; 