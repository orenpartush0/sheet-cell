package sheetCellDto;

import sheet.Sheet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SheetDto {
    public final String sheetName;
    public final int version;
    public final int numberOfRows;
    public final int numberOfColumns;
    public final Map<String, CellDto> cells;
    public final ArrayList<Integer> colWidth = new ArrayList<>();

    public SheetDto(String sheetName, int version, int numberOfRows, int numberOfColumns, Map<String, CellDto> cells) {
        this.sheetName = sheetName;
        this.version = version;
        this.numberOfRows = numberOfRows;
        this.numberOfColumns = numberOfColumns;
        this.cells = cells;
        this.colWidth.addAll(colWidth);
    }

    public SheetDto(Sheet sheet, List<Integer> colWidth){
        this.sheetName = sheet.GetSheetName();
        this.version = sheet.GetVersion();
        this.numberOfRows = sheet.GetNumberOfRows();
        this.numberOfColumns = sheet.GetNumberOfColumns();
        this.cells = sheet.GetCellsDTO();
        this.colWidth.addAll(colWidth);
    }
}
