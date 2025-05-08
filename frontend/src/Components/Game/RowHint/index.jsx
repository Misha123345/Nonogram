import React from 'react';
import styles from './RowHint.module.css';

const RowHint = ({ hints }) => {
  return (
    <div className={styles.rowHintCell}>
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

export default RowHint; 