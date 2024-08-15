package shticell.sheet.api;

import shticell.sheet.cell.api.Cell;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.exception.LoopConnectionException;
import shticell.operation.Exceptions.NumberOperationException;
import shticell.operation.Exceptions.OperationException;
import shticell.sheet.impl.SheetImpl;
import java.util.List;
import java.util.Map;

public interface Sheet {
    String GetSheetName();
    int GetVersion();
    int GetNumberOfRows();
    int GetNumberOfColumns();
    Map<Coordinate, Cell> GetCells();
    Cell GetCell(Coordinate coordinate);
    void UpdateCellByCoordinate(Coordinate coordinate, String newValue) throws LoopConnectionException, OperationException, NumberOperationException;
    List<Integer> GetCountOfChangesPerVersion();
    SheetImpl GetSheetByVersion(int version);
    List<Integer> getColsSize();
}
