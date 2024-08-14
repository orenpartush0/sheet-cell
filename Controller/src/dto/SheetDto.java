package dto;

import sheet.Sheet;

import java.util.List;
import java.util.Map;


public record SheetDto(String Name, int version, int numberOfRows, int numberOfColumns,
                       Map<String, CellDto> cells, List<Integer> colWidth)
{
    public SheetDto(Sheet sheet)
    {
        this(sheet.GetSheetName(),sheet.GetVersion(), sheet.GetNumberOfRows(), sheet.GetNumberOfColumns()
        ,sheet.GetCellsDTO(),sheet.getColsSize());
    }

    public SheetDto(String _sheetName,int _version,int _numOfCols,int _numOfRows)
    {
        this(_sheetName,_version,_numOfCols,_numOfRows,null,null);
    }
}


