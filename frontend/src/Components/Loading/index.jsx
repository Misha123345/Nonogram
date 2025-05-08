import React from 'react';
import styles from './Loading.module.css';

const Loading = ({ text = 'Loading...' }) => {
  return (
    <div className={styles.loadingContainer}>
      <div className={styles.spinner}></div>
      <p>{text}</p>
    </div>
  );
};

export default Loading; 