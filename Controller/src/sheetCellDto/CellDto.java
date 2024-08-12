package sheetCellDto;

import sheet.Cell;

public class CellDto {
    public final String cellId;
    public final int currentSheetVersion;
    public final String originalValue;
    public final String effectiveValue;

    public CellDto(String cellId, int currentSheetVersion, String originalValue) {
        this.cellId = cellId;
        this.currentSheetVersion = currentSheetVersion;
        this.originalValue = originalValue;
        effectiveValue = "";
    }

    public CellDto(Cell cell) {
        this.cellId = cell.GetCellId();
        this.currentSheetVersion = cell.GetSheetVersion();
        this.originalValue = cell.GetOriginalValue();
        this.effectiveValue = cell.GetEffectiveValue();
    }
}
