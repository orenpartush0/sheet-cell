package shticell.sheet.cell.connection;

import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.exception.LoopConnectionException;

import java.util.*;

public interface CellConnection{
    Coordinate GetCellCoordinate();

    List<CellConnection> GetDependsOn();

    List<CellConnection> GetInfluenceOn();

    List<CellConnection> GetSortedInfluenceOn() throws LoopConnectionException;

    void AddToDependsOn(CellConnection neighbor);

    void AddToInfluenceOn(CellConnection neighbor);

    void RemoveFromInfluenceOn(CellConnection neighbor);

    List<CellConnection> ClearDependsOn();

    void AddListToInfluenceOn(List<CellConnection> neighbors);

    ArrayList<String> GetDependsOnListOfStrings();

    ArrayList<String> GetInfluenceOnListOfStrings();

}
