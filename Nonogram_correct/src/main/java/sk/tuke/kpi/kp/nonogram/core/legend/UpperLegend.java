package sk.tuke.kpi.kp.nonogram.core.legend;

import sk.tuke.kpi.kp.nonogram.core.PuzzleField;

public class UpperLegend extends Legend {
    @Override
    public void calculateLegend(PuzzleField field) {
        // Создаем временный массив для подсчета подсказок
        int[][] tempLegend = new int[field.getColumns()][field.getRows()];
        int[] hintCounts = new int[field.getColumns()];

        // Подсчитываем последовательности для каждого столбца
        for (int col = 0; col < field.getColumns(); col++) {
            int currentLength = 0;
            int hintCount = 0;

            // Проходим сверху вниз
            for (int row = 0; row < field.getRows(); row++) {
                if (field.getTile(row, col).isSolutionTile()) {
                    currentLength++;
                } else {
                    if (currentLength > 0) {
                        tempLegend[col][hintCount] = currentLength;
                        hintCount++;
                        currentLength = 0;
                    }
                }
            }

            // Проверяем последнюю последовательность
            if (currentLength > 0) {
                tempLegend[col][hintCount] = currentLength;
                hintCount++;
            }
            
            // Сохраняем количество подсказок для столбца
            hintCounts[col] = hintCount;
        }

        // Определяем максимальное количество подсказок для всех столбцов
        int maxHints = 0;
        for (int count : hintCounts) {
            maxHints = Math.max(maxHints, count);
        }

        // Создаем итоговый массив подсказок
        legend = new int[field.getColumns()][maxHints];

        // Копируем подсказки снизу вверх
        for (int col = 0; col < field.getColumns(); col++) {
            for (int i = 0; i < hintCounts[col]; i++) {
                // Записываем в обратном порядке (снизу вверх)
                legend[col][maxHints - hintCounts[col] + i] = tempLegend[col][i];
            }
        }
    }
}
