package shticell.cell.sheet.api;

import shticell.cell.ties.CellConnection;

import java.util.List;

public interface CellCoordinator {
    String GetCellEffectiveValue(String square);
    void UpdateDependentCells(List<String> cellsList);
    CellConnection GetCellConnections(String cellId);
}
