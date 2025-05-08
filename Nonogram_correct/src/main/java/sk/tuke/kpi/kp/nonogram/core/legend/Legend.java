package sk.tuke.kpi.kp.nonogram.core.legend;

import lombok.Getter;
import sk.tuke.kpi.kp.nonogram.core.PuzzleField;

@Getter
public abstract class Legend {
    protected int[][] legend;

    public abstract void calculateLegend(PuzzleField field);

}
