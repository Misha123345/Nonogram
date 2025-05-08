package sk.tuke.kpi.kp.nonogram.consoleui;

import sk.tuke.kpi.kp.nonogram.core.FieldState;
import sk.tuke.kpi.kp.nonogram.core.PuzzleField;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);
    private final PuzzleField field;
    private static final Pattern INPUT_PATTERN = Pattern.compile("([a-zA-Z])([1-9][0-9]?)");

    // Symbols for rendering
    private static final String EMPTY_CELL = "□";
    private static final String MARKED_CELL = "■";
    private static final String HORIZONTAL_LINE = "─";
    private static final String VERTICAL_LINE = "│";
    private static final String TOP_LEFT = "┌";
    private static final String TOP_RIGHT = "┐";
    private static final String BOTTOM_LEFT = "└";
    private static final String BOTTOM_RIGHT = "┘";

    public ConsoleUI(PuzzleField field) {
        this.field = field;
    }

    public void startGame() {
        printField();

        do {
            String input = getUserInput();
            
            // Если пользователь хочет выйти из игры
            if ("X".equals(input)) {
                System.out.println("Ďakujeme za hru!");
                System.exit(0);
            }

            // Обрабатываем ход пользователя
            boolean moveSuccess = processUserMove(input);
            
            // Если ход успешный, перерисовываем поле и проверяем состояние игры
            if (moveSuccess) {
                printField();
                checkGameState();
            }
            
        } while (field.getFieldState() == FieldState.PLAYING);
    }

    private String getUserInput() {
        System.out.print("Bunka, ktorú chcete označiť (napríklad A1) alebo X pre ukončenie: ");
        return scanner.nextLine().trim().toUpperCase();
    }

    private boolean processUserMove(String input) {
        Matcher matcher = INPUT_PATTERN.matcher(input);
        
        if (!matcher.matches()) {
            System.err.println("Nesprávny formát vstupu! Použite formát 'A1'");
            return false;
        }
        
        char columnChar = matcher.group(1).charAt(0);
        int row = columnChar - 'A';
        int col = Integer.parseInt(matcher.group(2)) - 1;
        
        // Проверяем валидность координат
        if (row < 0 || row >= field.getRows() || col < 0 || col >= field.getColumns()) {
            System.err.println("Súradnice sú mimo poľa!");
            return false;
        }
        
        // Выполняем ход
        boolean success = field.markTile(row, col);
        if (!success) {
            System.err.println("Nesprávna bunka! Táto bunka nemá byť označená.");
            return false;
        }
        
        return true;
    }
    
    private void checkGameState() {
        if (field.getFieldState() != FieldState.PLAYING) {
            if (field.getFieldState() == FieldState.SOLVED) {
                System.out.println("Gratulujeme! Vyriešili ste nonogram!");
            } else {
                System.out.println("Hra je ukončená.");
            }
        }
    }

    private void printField() {
        int[][] upperLegend = field.getUpperLegend().getLegend();
        int[][] leftLegend = field.getLeftLegend().getLegend();

        // Print upper legends
        printUpperLegends(upperLegend);

        // Print the top border
        printTopBorder();

        // Print the field with left legends
        printFieldWithLegends(leftLegend);

        // Print the bottom border
        printBottomBorder();

        // Print column numbers
        printColumnNumbers();
    }

    private void printUpperLegends(int[][] upperLegend) {
        // Находим максимальное количество подсказок для столбцов
        int maxUpperLegendHeight = 0;
        for (int col = 0; col < field.getColumns() && col < upperLegend.length; col++) {
            int count = 0;
            for (int hint : upperLegend[col]) {
                if (hint > 0) count++;
            }
            maxUpperLegendHeight = Math.max(maxUpperLegendHeight, count);
        }

        // Если нет подсказок, выходим
        if (maxUpperLegendHeight == 0) return;

        // Получаем ширину левых подсказок для правильного выравнивания
        int leftLegendWidth = getMaxLeftLegendWidth();

        // Выводим верхние подсказки
        for (int row = 0; row < maxUpperLegendHeight; row++) {
            // Печатаем левый отступ для выравнивания
            System.out.print(" ".repeat(leftLegendWidth + 1));

            // Печатаем подсказки для каждого столбца
            for (int col = 0; col < field.getColumns() && col < upperLegend.length; col++) {
                // Вычисляем индекс подсказки для данной строки
                int hintIndex = row;
                if (hintIndex < upperLegend[col].length && upperLegend[col][hintIndex] > 0) {
                    // Выводим подсказку с центрированием в ячейке шириной 3 символа
                    String hint = String.valueOf(upperLegend[col][hintIndex]);
                    if (hint.length() == 1) {
                        System.out.print(" " + hint + " ");
                    } else {
                        System.out.print(" " + hint);
                    }
                } else {
                    // Пустая ячейка
                    System.out.print("   ");
                }
            }
            System.out.println();
        }
    }

    private int getMaxLeftLegendWidth() {
        int[][] leftLegend = field.getLeftLegend().getLegend();
        int maxWidth = 0;

        for (int[] row : leftLegend) {
            int rowWidth = 0;
            for (int hint : row) {
                if (hint > 0) {
                    rowWidth += String.valueOf(hint).length() + 1; // +1 for space
                }
            }
            maxWidth = Math.max(maxWidth, rowWidth);
        }

        return maxWidth + 1; // Add extra space for alignment
    }

    private void printTopBorder() {
        int leftPadding = getMaxLeftLegendWidth();
        System.out.print(" ".repeat(leftPadding) + TOP_LEFT);
        System.out.print(HORIZONTAL_LINE.repeat(field.getColumns() * 3));
        System.out.println(TOP_RIGHT);
    }

    private void printFieldWithLegends(int[][] leftLegend) {
        for (int row = 0; row < field.getRows(); row++) {
            // Print left hints for this row
            printLeftHints(leftLegend[row]);

            // Print field row
            System.out.print(VERTICAL_LINE);
            for (int col = 0; col < field.getColumns(); col++) {
                String cell = field.getTile(row, col).isMarked() ? MARKED_CELL : EMPTY_CELL;
                System.out.print(" " + cell + " ");
            }
            System.out.println(VERTICAL_LINE + " " + (char)('A' + row));
        }
    }

    private void printLeftHints(int[] rowHints) {
        StringBuilder hints = new StringBuilder();
        for (int hint : rowHints) {
            if (hint > 0) {
                hints.append(hint).append(" ");
            }
        }

        int padding = getMaxLeftLegendWidth() - hints.length();
        System.out.print(" ".repeat(Math.max(0, padding)) + hints);
    }

    private void printBottomBorder() {
        int leftPadding = getMaxLeftLegendWidth();
        System.out.print(" ".repeat(leftPadding) + BOTTOM_LEFT);
        System.out.print(HORIZONTAL_LINE.repeat(field.getColumns() * 3));
        System.out.println(BOTTOM_RIGHT);
    }

    private void printColumnNumbers() {
        int leftPadding = getMaxLeftLegendWidth();
        System.out.print(" ".repeat(leftPadding));

        for (int col = 0; col < field.getColumns(); col++) {
            System.out.print("  " + (col + 1));
        }
        System.out.println();
    }
}
