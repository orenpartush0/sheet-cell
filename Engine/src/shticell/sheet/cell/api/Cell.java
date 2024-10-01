package shticell.sheet.cell.api;

import shticell.sheet.cell.connection.CellConnection;
import shticell.sheet.cell.value.EffectiveValue;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.exception.LoopConnectionException;
import shticell.sheet.range.Range;

import java.io.Serializable;
import java.util.ArrayList;

public interface Cell extends Serializable ,Cloneable {
    String GetOriginalValue();
    EffectiveValue GetEffectiveValue();
    Coordinate GetCellCoordinate();
    int GetVersion();

    String GetUserName();

    void UpdateCell(String newOriginalValue, int sheetVersion, String user) throws LoopConnectionException;
    CellConnection GetConnections();
    boolean IsChangedInThisVersion(int version);
    Cell GetCellBySheetVersion(int version);
    ArrayList<Coordinate> GetDependsOnCoordinates();
    ArrayList<Coordinate> GetInfluenceOnCoordinates();
    Cell clone();
    void UseRange(Range range);
}
