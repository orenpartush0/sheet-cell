package shticell.sheet.cell.connection;

import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.exception.LoopConnectionException;

import java.io.Serializable;
import java.util.*;

public interface CellConnection extends Serializable {
    Coordinate GetCellCoordinate();

    List<CellConnection> GetDependsOn();

    List<CellConnection> GetInfluenceOn();

    List<CellConnection> GetSortedInfluenceOn() throws LoopConnectionException;

    void AddToDependsOn(CellConnection neighbor);

    void AddToInfluenceOn(CellConnection neighbor);

    void RemoveFromInfluenceOn(CellConnection neighbor);

    List<CellConnection> ClearDependsOn();

    void recoverDependsOn(List<CellConnection> neighbors);

    ArrayList<Coordinate> GetDependsOnCoordinates();

    ArrayList<Coordinate> GetInfluenceOnCoordinates();

}
