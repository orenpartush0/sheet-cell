package shticell.util;

import dto.SheetDto;
import shticell.sheet.api.Sheet;
import shticell.sheet.api.SheetToFilter;
import shticell.sheet.cell.value.ValueType;
import shticell.sheet.coordinate.CoordinateFactory;
import shticell.sheet.impl.SheetImpl;
import shticell.sheet.range.Range;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface Filter {


    private static boolean passesFilter(int row,SheetToFilter sheet,  Map<Integer, List<String>> filters){
        return filters.
                entrySet().
                stream().
                allMatch(entry -> entry.getValue().contains(sheet.GetCell(CoordinateFactory.getCoordinate(row,entry.getKey())).GetEffectiveValue().getValue().toString()));
    }

    static SheetDto getFilteredSheetDto(SheetToFilter sheet, Range range , Map<Integer, List<String>> filters) {
        Sheet sheetInRange = new SheetImpl("", sheet.GetNumberOfRows(), sheet.GetNumberOfColumns(), sheet.GetRowsHeight(), sheet.GetColsWidth());

        IntStream.
                range(1, sheet.GetNumberOfRows() + 1).
                forEach(row -> {
                    if (range.containRow(row) && passesFilter(row, sheet, filters)) {
                        IntStream.
                                range(1, sheet.GetNumberOfColumns() + 1).
                                forEach(col -> {
                                    if(range.containCol(col)) {
                                        sheetInRange.GetCell(CoordinateFactory.getCoordinate(row, col)).
                                                GetEffectiveValue().setValue(sheet.GetCell(CoordinateFactory.getCoordinate(row, col)).GetEffectiveValue().getValue().toString(), ValueType.STRING);
                                    }
                                });
                    }
                });

        return new SheetDto(sheetInRange);
    }
}

