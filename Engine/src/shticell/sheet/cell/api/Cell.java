package shticell.sheet.cell.api;

import shticell.sheet.cell.connection.CellConnection;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.exception.LoopConnectionException;
import shticell.operation.Exceptions.OperationException;

import java.util.ArrayList;

public interface Cell {
    String GetOriginalValue();
    String GetEffectiveValue();
    Coordinate GetCellCoordinate();
    int GetVersion();
    void UpdateCell(String newOriginalValue, int sheetVersion) throws LoopConnectionException, OperationException;
    CellConnection GetConnections();
    boolean IsChangedInThisVersion(int version);
    Cell GetCellBySheetVersion(int version);
    ArrayList<String> GetDependsOnListOfStrings();
    ArrayList<String> GetInfluenceOnListOfStrings();
}
