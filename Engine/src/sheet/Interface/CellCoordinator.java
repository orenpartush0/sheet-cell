package sheet.Interface;

import sheet.CellConnection;

import java.util.List;

public interface CellCoordinator {
    String GetCellEffectiveValue(String square);
    void UpdateDependentCells(List<String> cellsList);
    CellConnection GetCellConnection(String cellId);
}
