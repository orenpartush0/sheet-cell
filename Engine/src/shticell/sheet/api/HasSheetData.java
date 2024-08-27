package shticell.sheet.api;

import shticell.sheet.cell.connection.CellConnection;
import shticell.sheet.cell.value.EffectiveValue;
import shticell.sheet.coordinate.Coordinate;

import java.io.Serializable;
import java.util.List;

public interface HasSheetData extends Serializable {
    EffectiveValue GetCellEffectiveValue(Coordinate coordinate);
    void UpdateDependentCells(List<Coordinate> cellsList);
    CellConnection GetCellConnections(Coordinate coordinate);
    public boolean IsCoordinateInSheet(Coordinate coordinate);
}
