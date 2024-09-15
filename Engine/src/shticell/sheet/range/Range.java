package shticell.sheet.range;

import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.coordinate.CoordinateFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public record Range(String rangeName, Coordinate startCellCoordinate, Coordinate endCellCoordinate) implements Serializable {

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

    public List<Coordinate> getRangeCellsCoordinate() {
        List<Coordinate> coordinates = new ArrayList<>();

        for (int row = startCellCoordinate.row(); row <= endCellCoordinate.row(); row++) {
            for (int col = startCellCoordinate.col(); col <= endCellCoordinate.col(); col++) {
                coordinates.add(CoordinateFactory.getCoordinate(row,col));
            }
        }

        return coordinates;
    }

    public boolean containRow(int row){
        return startCellCoordinate.row() <= row && endCellCoordinate.row() >= row;
    }
}
