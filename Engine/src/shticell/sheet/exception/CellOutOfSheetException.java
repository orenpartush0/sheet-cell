package shticell.sheet.exception;

import shticell.sheet.coordinate.Coordinate;

public class CellOutOfSheetException extends Exception{
    public CellOutOfSheetException(int row, int col) {
        super("Cell " + 'A'+row + col+ " is out of sheet");
    }
}
