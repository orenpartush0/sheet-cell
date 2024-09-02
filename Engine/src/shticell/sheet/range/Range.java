package shticell.sheet.range;

import shticell.sheet.coordinate.Coordinate;

public record Range(String rangeName, Coordinate startCellCoordinate, Coordinate endCellCoordinate) {

    public boolean containsCell(Coordinate cell) {
        return startCellCoordinate.col() <= cell.col() && startCellCoordinate.row() <= cell.row()
                && endCellCoordinate.col() >= cell.col() && endCellCoordinate.row() >= cell.row();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Range other)) return false;
        return startCellCoordinate.equals(other.startCellCoordinate)
                && endCellCoordinate.equals(other.endCellCoordinate);
    }
}
