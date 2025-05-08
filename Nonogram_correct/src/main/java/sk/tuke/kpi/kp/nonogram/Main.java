package sk.tuke.kpi.kp.nonogram;

import sk.tuke.kpi.kp.nonogram.consoleui.ConsoleUI;
import sk.tuke.kpi.kp.nonogram.core.PuzzleField;

public class Main {
    public static void main(String[] args) {
        boolean[][] image = {
                {true, false, true, false, true, true},
                {false, true, true, true, false, true},
                {false, false, true, false, false, true},
                {false, true, true, true, false, true},
                {true, false, true, false, true, true}
        };

        PuzzleField puzzleField = new PuzzleField(5, 6, image);

        var consoleUI = new ConsoleUI(puzzleField);

        consoleUI.startGame();
    }
}
