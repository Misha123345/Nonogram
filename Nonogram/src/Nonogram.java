import sk.tuke.kpi.kp.consoleUI.ConsoleUI;
import sk.tuke.kpi.kp.nonogram.PuzzleField;

public class Nonogram {
    public static void main(String[] args) {

        boolean[][] image = {
                {true, false, true, false, true},
                {false, true, true, true, false},
                {false, true, true, true, false},
                {false, true, true, true, false},
                {true, false, true, false, true}
        };

        PuzzleField puzzleField = new PuzzleField(5, 5, image);

        var consoleUI = new ConsoleUI(puzzleField);

        consoleUI.startGame();
    }
}