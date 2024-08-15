package shticell.sheet.api;

import shticell.cell.api.Cell;
import shticell.exception.LoopConnectionException;
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
    Map<String, Cell> GetCells();
    Cell GetCell(String cellId);
    void UpdateCellByCoordinate(String cellId,String newValue) throws LoopConnectionException, OperationException, NumberOperationException;
    List<Integer> GetCountOfChangesPerVersion();
    SheetImpl GetSheetByVersion(int version);
    List<Integer> getColsSize();
}
