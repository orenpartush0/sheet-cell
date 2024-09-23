package dto;

import shticell.sheet.api.Sheet;
import shticell.sheet.coordinate.Coordinate;

import java.util.Map;
import java.util.stream.Collectors;


public record SheetDto(String Name, int version, int numberOfRows, int numberOfColumns,
                       int colsWidth,int rowsHeight,
                       Map<Coordinate, CellDto> cells)
{

    public SheetDto(Sheet sheet) {
        this(sheet.GetSheetName(), sheet.GetVersion(), sheet.GetNumberOfRows(), sheet.GetNumberOfColumns(),
        sheet.GetColsWidth(), sheet.GetRowsHeight(),
                sheet.GetCells().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry ->new CellDto(entry.getValue()))));
    }

    public SheetDto(String _sheetName,int _version,int _numOfCols,int _numOfRows,int _colsWidth,int _rowsHeight)
    {
        this(_sheetName,_version,_numOfRows,_numOfCols,_colsWidth,_rowsHeight,null);
    }
}


