import React from 'react';
import styles from './ColumnHint.module.css';

const ColumnHint = ({ hints }) => {
  return (
    <div className={styles.colHintCell}>
      {hints.map((hint, i) => (
        hint !== 0 ? (
          <span key={`hint-${i}`} className={styles.hint}>
            {hint}
          </span>
        ) : null
      ))}
    </div>
  );
};

export default ColumnHint; 