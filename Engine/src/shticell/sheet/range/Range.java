package shticell.sheet.range;

import shticell.sheet.coordinate.Coordinate;

public interface Range {
    Coordinate getStartCell();
    Coordinate getEndCell();
    boolean containsCell(Coordinate cell);
}
