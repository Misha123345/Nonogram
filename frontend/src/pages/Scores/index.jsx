import React, { useState, useEffect } from 'react';
import ScoresTabs from '../../Components/Scores/ScoresTabs';
import ScoresTable from '../../Components/Scores/ScoresTable';
import Loading from '../../Components/Loading';
import { scoreApi } from '../../services';
import styles from './Scores.module.css';

const Scores = () => {
  const [activeTab, setActiveTab] = useState('easy');
  const [scores, setScores] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchScores = async () => {
      setLoading(true);
      try {
        const gameName = `puzzle-${activeTab}`;
        
        const response = await scoreApi.getTopScores(gameName);
        
        const formattedScores = response.data.map((score, index) => ({
          id: index + 1,
          player: score.player,
          score: score.points,
          time: '-',
          date: new Date(score.playedOn).toLocaleDateString()
        }));
        
        setScores(formattedScores);
      } catch (error) {
        console.error('Error fetching scores:', error);
        setScores([]);
      } finally {
        setLoading(false);
      }
    };

    fetchScores();
  }, [activeTab]);

  const handleTabChange = (tab) => {
    setActiveTab(tab);
  };

  return (
    <div className={styles.scoresContainer}>
      <h1 className={styles.title}>Top Scores</h1>
      
      <ScoresTabs 
        activeTab={activeTab} 
        onTabChange={handleTabChange} 
      />
      
      {loading ? (
        <Loading text="Loading scores..." />
      ) : (
        <ScoresTable scores={scores} />
      )}
    </div>
  );
};

export default Scores; 