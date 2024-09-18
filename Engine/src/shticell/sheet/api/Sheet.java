package shticell.sheet.api;

import shticell.sheet.cell.api.Cell;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.exception.LoopConnectionException;
import shticell.sheet.impl.SheetImpl;
import shticell.sheet.range.Range;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface Sheet extends Cloneable, Serializable,SheetToFilter {
    String GetSheetName();

    int GetVersion();

    int GetNumberOfRows();

    int GetNumberOfColumns();

    Map<Coordinate, Cell> GetCells();

    Cell GetCell(Coordinate coordinate);

    void UpdateCellByCoordinate(Coordinate coordinate, String newValue) throws LoopConnectionException;

    List<Integer> GetCountOfChangesPerVersion();

    SheetImpl GetSheetByVersion(int version);

    int GetColsWidth();

    int GetRowsHeight();

    Range GetRangeDto(String rangeName);

    void AddRange(Range rangeDto);

    List<Range> GetRangesDto();

    Sheet clone();

    void RemoveRange(String rangeName) throws Exception;

    String GetOriginalValue(Coordinate coordinate);

    void applyDynamicCalculate(Coordinate coordinate, String numStr);
}
