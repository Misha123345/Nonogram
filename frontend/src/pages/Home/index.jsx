import React from 'react';
import { Link } from 'react-router-dom';
import styles from './Home.module.css';

const Home = () => {
  return (
    <div className={styles.homeContainer}>
      <div className={styles.hero}>
        <h1>Welcome to Nonogram</h1>
        <p>Challenge your mind with this entertaining puzzle game!</p>
        <Link to="/play" className={styles.playButton}>
          Start Playing
        </Link>
      </div>
      <div className={styles.features}>
        <div className={styles.featureCard}>
          <h2>Learn to Play</h2>
          <p>Nonograms, also known as Picross or Griddlers, are picture logic puzzles in which cells in a grid must be colored or left blank according to numbers at the side of the grid to reveal a hidden picture.</p>
        </div>
        <div className={styles.featureCard}>
          <h2>Multiple Difficulty Levels</h2>
          <p>Choose between easy, medium, hard, or random puzzles to match your skill level.</p>
        </div>
        <div className={styles.featureCard}>
          <h2>Track Your Progress</h2>
          <p>See your scores and compare with other players on our leaderboards.</p>
        </div>
      </div>
    </div>
  );
};

export default Home; 