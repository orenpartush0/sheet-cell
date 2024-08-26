package shticell.sheet.exception;

import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.coordinate.CoordinateFactory;

public class CellOutOfSheetException extends Exception{
    public CellOutOfSheetException(int row, int col) {
        super("Cell " + CoordinateFactory.getCoordinate(row,col).toString()+" is out of sheet");
    }
}
