package shticell.sheet.cell.api;

import shticell.sheet.cell.connection.CellConnection;
import shticell.sheet.cell.value.EffectiveValue;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.exception.LoopConnectionException;

import java.io.Serializable;
import java.util.ArrayList;

public interface Cell extends Serializable {
    String GetOriginalValue();
    EffectiveValue GetEffectiveValue();
    Coordinate GetCellCoordinate();
    int GetVersion();
    void UpdateCell(String newOriginalValue, int sheetVersion) throws LoopConnectionException;
    CellConnection GetConnections();
    boolean IsChangedInThisVersion(int version);
    Cell GetCellBySheetVersion(int version);
    ArrayList<String> GetDependsOnListOfStrings();
    ArrayList<String> GetInfluenceOnListOfStrings();
}
