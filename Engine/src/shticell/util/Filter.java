package shticell.util;

import dto.SheetDto;
import shticell.sheet.api.Sheet;
import shticell.sheet.cell.value.ValueType;
import shticell.sheet.coordinate.CoordinateFactory;
import shticell.sheet.impl.SheetImpl;
import shticell.sheet.range.Range;

import java.util.*;

public interface Filter {

    static List<String> getValuesInColumn(Sheet sheet, Range range, int col) {
        return sheet.GetCells().
                entrySet().
                stream().
                filter(entry -> entry.getKey().col() == col).
                filter(entry->range.containsCell(entry.getKey())).
                map(entry -> entry.getValue().GetEffectiveValue().getValue().toString()).
                filter(str -> !str.isEmpty()).
                distinct().
                toList();
    }

    private static int CountNumOfRows(Sheet sheet, int col, Range range, List<String> filters){
        return (int)sheet.GetCells().
                entrySet().
                stream().
                filter(entry -> filters.contains(sheet.GetCell(CoordinateFactory.getCoordinate(entry.getKey().row(),col)).GetEffectiveValue().getValue().toString())
                        && range.containsCell(entry.getKey())).
                count() / (range.endCellCoordinate().col() - range.startCellCoordinate().col() + 1);
    }

    private static int calculateRow(int[] counter , int numOfCols){
        return (counter[0] / numOfCols) + 1;
    }

    private static int calculateCol(int[] counter , int numOfCols){
        return (counter[0]++ % numOfCols) + 1;
    }

    static SheetDto getFilteredSheetDto(Sheet sheet, int col,Range range ,List<String> filters){
        Sheet SheetInRange = new SheetImpl("",CountNumOfRows(sheet,col,range,filters),range.endCellCoordinate().col(),sheet.GetRowsHeight(),sheet.GetColsWidth());
        int[] counter = new int[1];

        sheet.GetCells().
                entrySet().
                stream().
                filter(entry -> filters.contains(sheet.GetCell(CoordinateFactory.getCoordinate(entry.getKey().row(),col)).GetEffectiveValue().getValue().toString())
                        && range.containsCell(entry.getKey())).
                sorted(Map.Entry.comparingByKey()).
                forEach(entry->
                            (SheetInRange.
                                GetCell(CoordinateFactory.getCoordinate(calculateRow(counter,SheetInRange.GetNumberOfColumns()),calculateCol(counter, SheetInRange.GetNumberOfColumns()))).
                                    GetEffectiveValue()).
                                    setValue(entry.getValue().GetEffectiveValue().getValue().toString(),ValueType.STRING));


        return new SheetDto(SheetInRange);
    }
}

