import React from 'react';
import Cell from '../Cell';
import RowHint from '../RowHint';
import ColumnHint from '../ColumnHint';
import styles from './GameBoard.module.css';

const GameBoard = ({ game, boardState, isCompleted, onCellClick }) => {
  const numRows = boardState.length;
  const numCols = boardState[0]?.length || 0; 

  const gridContainerStyle = {
    gridTemplateColumns: `repeat(${numCols}, 30px)`, 
    gridTemplateRows: `repeat(${numRows}, 30px)`, 
  };

  const colHintsContainerStyle = {
    gridTemplateColumns: `repeat(${numCols}, 30px)`, 
  };

  return (
    <div className={styles.gameBoard}>
      <div className={styles.emptyCorner}></div>

      <div className={styles.colHintsContainer} style={colHintsContainerStyle}>
        {game.colHints.map((hints, colIndex) => (
          <ColumnHint 
            key={`colhint-${colIndex}`} 
            hints={hints} 
          />
        ))}
      </div>

      <div className={styles.rowHintsContainer}>
         {game.rowHints.map((hints, rowIndex) => (
          <RowHint 
            key={`rowhint-${rowIndex}`} 
            hints={hints} 
          />
        ))}
      </div>

      <div className={styles.gridContainer} style={gridContainerStyle}>
        {boardState.map((row, rowIndex) => (
           row.map((cell, colIndex) => (
              <Cell 
                key={`cell-${rowIndex}-${colIndex}`}
                filled={cell}
                completed={isCompleted}
                onClick={() => onCellClick(rowIndex, colIndex)}
              />
            ))
        ))}
      </div>

    </div>
  );
};

export default GameBoard; 