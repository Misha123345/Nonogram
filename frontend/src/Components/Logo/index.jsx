import React from 'react';
import styles from './Logo.module.css';

const Logo = () => {
  return (
    <div className={styles.logo}>
      <span className={styles.logoText}>
        <span className={styles.logoN}>N</span>
        <span className={styles.logoO}>o</span>
        <span className={styles.logoN2}>n</span>
        <span className={styles.logoO2}>o</span>
        <span className={styles.logoG}>g</span>
        <span className={styles.logoR}>r</span>
        <span className={styles.logoA}>a</span>
        <span className={styles.logoM}>m</span>
      </span>
      <div className={styles.logoGrid}>
        <div className={styles.gridCell}></div>
        <div className={`${styles.gridCell} ${styles.filled}`}></div>
        <div className={styles.gridCell}></div>
        <div className={`${styles.gridCell} ${styles.filled}`}></div>
        <div className={styles.gridCell}></div>
        <div className={`${styles.gridCell} ${styles.filled}`}></div>
        <div className={styles.gridCell}></div>
        <div className={`${styles.gridCell} ${styles.filled}`}></div>
        <div className={styles.gridCell}></div>
      </div>
    </div>
  );
};

export default Logo; 