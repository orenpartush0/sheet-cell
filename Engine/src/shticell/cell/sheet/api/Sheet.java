package shticell.cell.sheet.api;

import shticell.cell.impl.CellImpl;

import java.util.Map;

public interface Sheet {
    String GetSheetName();
    int GetVersion();
    int GetNumberOfRows();
    int GetNumberOfColumns();
    Map<String, CellImpl> GetCells();
    CellImpl GetCell(String cellId);
}
