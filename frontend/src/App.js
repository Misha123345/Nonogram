import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import './App.css';
import Header from './Components/Header';
import Home from './pages/Home';
import Play from './pages/Play';
import Game from './pages/Game';
import Scores from './pages/Scores';
import Comments from './pages/Comments';
import Rating from './pages/Rating';
import Login from './pages/Login';
import Register from './pages/Register';
import Profile from './pages/Profile';
import { AuthProvider } from './contexts/AuthContext';

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="App">
          <Header />
          <main className="main-content">
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/play" element={<Play />} />
              <Route path="/game/:difficulty" element={<Game />} />
              <Route path="/scores" element={<Scores />} />
              <Route path="/comments" element={<Comments />} />
              <Route path="/rating" element={<Rating />} />
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route path="/profile" element={<Profile />} />
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </main>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
