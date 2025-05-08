package sk.tuke.gamestudio.game.core.legend;

import lombok.Getter;
import sk.tuke.gamestudio.game.core.PuzzleField;

@Getter
public abstract class Legend {
    protected int[][] legend;

    public abstract void calculateLegend(PuzzleField field);

}
