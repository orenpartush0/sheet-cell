package shticell.sheet.api;

import shticell.sheet.cell.connection.CellConnection;
import shticell.sheet.coordinate.Coordinate;

import java.util.List;

public interface CellCoordinator {
    String GetCellEffectiveValue(Coordinate coordinate);
    void UpdateDependentCells(List<Coordinate> cellsList);
    CellConnection GetCellConnections(Coordinate coordinate);
}
