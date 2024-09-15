package shticell.sheet.api;

import shticell.sheet.cell.api.Cell;
import shticell.sheet.coordinate.Coordinate;

import java.util.Map;

public interface SheetToFilter {
    int GetNumberOfRows();
    int GetNumberOfColumns();
    int GetRowsHeight();
    int GetColsWidth();
    Cell GetCell(Coordinate coordinate);
    Map<Coordinate, Cell> GetCells();
}
