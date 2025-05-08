package main.java.sk.tuke.kpi.kp.nonogram.core.legend;

import lombok.Getter;
import sk.tuke.kpi.kp.nonogram.PuzzleField;

@Getter
public abstract class Legend {
    protected int[][] legend;

    public abstract void calculateLegend(PuzzleField field);

}
