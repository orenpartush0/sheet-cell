package shticell.sheet.range;

import shticell.sheet.coordinate.Coordinate;

public class RangeImpl implements Range {
    private final Coordinate startCellCoordinate;
    private final Coordinate endCellCoordinate;

    public RangeImpl(Coordinate startCell, Coordinate endCell) {
        this.startCellCoordinate = startCell;
        this.endCellCoordinate = endCell;
    }

    public Coordinate getStartCell() {
        return startCellCoordinate;
    }

    public Coordinate getEndCell() {
        return endCellCoordinate;
    }

    public boolean containsCell(Coordinate cell) {
        return startCellCoordinate.col() <= cell.col() && startCellCoordinate.row() <= cell.row()
                && endCellCoordinate.col() >= cell.col() && endCellCoordinate.row() >= cell.row();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RangeImpl) {
            RangeImpl other = (RangeImpl) o;
            return startCellCoordinate.equals(other.startCellCoordinate) && endCellCoordinate.equals(other.endCellCoordinate);
        }

        return false;
    }

}