package dto;

import shticell.sheet.api.Sheet;
import shticell.sheet.coordinate.Coordinate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public record SheetDto(String Name, int version, int numberOfRows, int numberOfColumns,
                       Map<Coordinate, CellDto> cells, List<Integer> colWidth)
{
    public SheetDto(Sheet sheet) {
        this(sheet.GetSheetName(), sheet.GetVersion(), sheet.GetNumberOfRows(), sheet.GetNumberOfColumns()
        , sheet.GetCells().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry ->new CellDto(entry.getValue())))
                , sheet.getColsSize());
    }

    public SheetDto(String _sheetName,int _version,int _numOfCols,int _numOfRows)
    {
        this(_sheetName,_version,_numOfCols,_numOfRows,null,null);
    }
}


