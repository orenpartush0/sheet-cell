package shticell.sheet.cell.api;

import shticell.sheet.cell.connection.CellConnection;
import shticell.sheet.exception.LoopConnectionException;
import shticell.operation.Exceptions.OperationException;

public interface Cell {
    String GetOriginalValue();
    String GetEffectiveValue();
    String GetCellId();
    int GetVersion();
    void UpdateCell(String newOriginalValue, int sheetVersion) throws LoopConnectionException, OperationException;
    CellConnection GetConnections();
    boolean IsChangedInThisVersion(int version);
    Cell GetCellBySheetVersion(int version);
}
