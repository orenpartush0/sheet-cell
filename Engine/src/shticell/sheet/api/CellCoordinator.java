package shticell.sheet.api;

import shticell.cell.ties.api.CellConnection;
import shticell.cell.ties.impl.CellConnectionImpl;

import java.util.List;

public interface CellCoordinator {
    String GetCellEffectiveValue(String square);
    void UpdateDependentCells(List<CellConnection> cellsList);
    CellConnection GetCellConnections(String cellId);
}
