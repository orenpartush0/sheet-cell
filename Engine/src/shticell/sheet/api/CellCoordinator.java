package shticell.sheet.api;

import shticell.sheet.cell.connection.CellConnection;

import java.util.List;

public interface CellCoordinator {
    String GetCellEffectiveValue(String square);
    void UpdateDependentCells(List<CellConnection> cellsList);
    CellConnection GetCellConnections(String cellId);
}
