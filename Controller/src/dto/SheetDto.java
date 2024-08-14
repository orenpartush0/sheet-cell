package dto;

import shticell.cell.impl.CellImpl;
import shticell.cell.sheet.sheetimpl.SheetImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public record SheetDto(String Name, int version, int numberOfRows, int numberOfColumns,
                       Map<String, CellDto> cells, List<Integer> colWidth)
{
    public SheetDto(SheetImpl sheetImpl)
    {
        this(sheetImpl.GetSheetName(), sheetImpl.GetVersion(), sheetImpl.GetNumberOfRows(), sheetImpl.GetNumberOfColumns()
        , sheetImpl.GetCells().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,entry ->new CellDto(entry.getValue())))
                , sheetImpl.getColsSize());
    }

    public SheetDto(String _sheetName,int _version,int _numOfCols,int _numOfRows)
    {
        this(_sheetName,_version,_numOfCols,_numOfRows,null,null);
    }
}


