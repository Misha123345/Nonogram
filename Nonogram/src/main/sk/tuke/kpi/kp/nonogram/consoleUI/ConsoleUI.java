package main.sk.tuke.kpi.kp.nonogram.consoleUI;

import sk.tuke.kpi.kp.nonogram.FieldState;
import sk.tuke.kpi.kp.nonogram.PuzzleField;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);
    private final PuzzleField field;
    private static final Pattern INPUT_PATTERN = Pattern.compile("([a-zA-Z])([1-9][0-9]?)");

    public ConsoleUI(PuzzleField field) {
        this.field = field;
    }

    public void startGame() {
        printField();

        do {
            parseInput();
            printField();
        } while (field.getFieldState() == FieldState.PLAYING);
    }

    private void parseInput() {
        System.out.print("Клетка которую хотите открыть (или X для выхода): ");
        String line = scanner.nextLine().trim().toUpperCase();

        if ("X".equals(line)) {
            System.exit(0);
        }

        Matcher matcher = INPUT_PATTERN.matcher(line);
        if (matcher.matches()) {
            char columnChar = matcher.group(1).charAt(0);

            int row = columnChar - 'A';
            int col = Integer.parseInt(matcher.group(2)) - 1;

            field.markTile(row, col);
        } else {
            System.err.println("Неверный формат ввода!");
        }
    }


    private void printField() {
        int[][] upperLegend = field.getUpperLegend().getLegend();
        int[][] leftLegend = field.getLeftLegend().getLegend();

        int maxLeftLegendLength = getMaxLeftLegendLength(leftLegend);
        int maxUpperHintLength = getMaxUpperHintLength(upperLegend, field);

        printUpperLegends(upperLegend, field, maxLeftLegendLength, maxUpperHintLength);
        printDivider(maxLeftLegendLength, field.getColumns());
        printFieldWithLegends(leftLegend, upperLegend, field, maxLeftLegendLength, maxUpperHintLength);
    }

    private int getMaxLeftLegendLength(int[][] leftLegend) {
        int maxLeftLegendLength = 0;
        for (int[] row : leftLegend) {
            maxLeftLegendLength = Math.max(maxLeftLegendLength, row.length);
        }
        return maxLeftLegendLength;
    }

    private int getMaxUpperHintLength(int[][] upperLegend, PuzzleField field) {
        int maxUpperHintLength = 0;
        for (int col = 0; col < field.getColumns(); col++) {
            if (upperLegend[col][0] > 0) {
                maxUpperHintLength = Math.max(maxUpperHintLength, String.valueOf(upperLegend[col][0]).length());
            }
        }
        return maxUpperHintLength;
    }

    private void printUpperLegends(int[][] upperLegend, PuzzleField field, int maxLeftLegendLength, int maxUpperHintLength) {
        for (int row = field.getRows() - 1; row >= 0; row--) {
            System.out.print(" ".repeat((maxLeftLegendLength + 1) * 3));

            for (int col = 0; col < field.getColumns(); col++) {
                if (upperLegend[col][row] > 0) {
                    System.out.printf("%" + maxUpperHintLength + "d ", upperLegend[col][row]);
                } else {
                    System.out.print(" ".repeat(maxUpperHintLength + 1));
                }

            }
            System.out.println();
        }

        System.out.print(" ".repeat((maxLeftLegendLength + 1) * 3));

        for (int col = 0; col < field.getColumns(); col++) {
            System.out.print(col + 1 + " ");
        }
    }

    private void printDivider(int maxLeftLegendLength, int columns) {
        System.out.print(" ".repeat((maxLeftLegendLength) * 3)); // Пропускаем место под левые подсказки
        for (int col = 0; col < columns; col++) {
            System.out.print("___");
        }
        System.out.println();
    }

    private void printFieldWithLegends(int[][] leftLegend, int[][] upperLegend, PuzzleField field, int maxLeftLegendLength, int maxUpperHintLength) {
        char leftCoordinate = 'A';

        for (int row = 0; row < field.getRows(); row++) {
            for (int j = 0; j < maxLeftLegendLength; j++) {
                if (j < leftLegend[row].length && leftLegend[row][j] > 0) {
                    System.out.print(String.format("%2d", leftLegend[row][j]) + " ");
                } else {
                    System.out.print("   ");
                }
            }

            System.out.print(leftCoordinate++);
            System.out.print(" | ");

            for (int col = 0; col < field.getColumns(); col++) {
                if (!field.getTile(row, col).isMarked()) {
                    System.out.print(" -");
                } else {
                    System.out.print(" @");
                }
            }
            System.out.println();
        }
    }
}
