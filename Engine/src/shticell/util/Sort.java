package shticell.util;

import dto.SheetDto;
import shticell.sheet.api.Sheet;
import shticell.sheet.api.SheetToFilter;
import shticell.sheet.cell.value.EffectiveValue;
import shticell.sheet.cell.value.ValueType;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.coordinate.CoordinateFactory;
import shticell.sheet.impl.SheetImpl;
import shticell.sheet.range.Range;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public interface Sort {

    public static SheetDto SortRange(SheetToFilter sheet, Queue<String> cols, Range range)  {
        Sheet sheetInRange = new SheetImpl("", sheet.GetNumberOfRows(), sheet.GetNumberOfColumns(), sheet.GetRowsHeight(), sheet.GetColsWidth());

        List<Integer> lst = creteListOfRowsInRange(sheet, range);
        lst = sortLst(lst, sheet, cols);
        for (int newRowIndex = 0; newRowIndex < lst.size(); newRowIndex++) {
            int oldRowIndex = lst.get(newRowIndex);
            for (int col = 1; col <= sheet.GetNumberOfColumns(); col++) {
                Coordinate oldCoordinate = CoordinateFactory.getCoordinate(oldRowIndex, col);
                Coordinate newCoordinate = CoordinateFactory.getCoordinate(newRowIndex+1, col);
                try{sheetInRange.UpdateCellByCoordinate(newCoordinate, sheet.GetCell(oldCoordinate).GetEffectiveValue().getValue().toString(),sheet.GetCell(oldCoordinate).GetUserName());}
                catch (Exception ignored){};

            }
        }

        return new SheetDto(sheetInRange);
    }

    private static List<Integer> getEmpties(List<Integer> lst,String colL,SheetToFilter sheet) {
        List<Integer> empties = new ArrayList<>();
        int col = columnLabelToNumber(colL);
        for(int row:lst){
            EffectiveValue effectiveValue = sheet.GetCell(CoordinateFactory.getCoordinate(row, col)).GetEffectiveValue();
            if(effectiveValue.getValueType() != ValueType.NUMERIC){
                empties.add(row);
            }
        }
        lst.removeAll(empties);
        return empties;
    }
//
    private static List<Integer> sortLst(List<Integer> lst, SheetToFilter sheet, Queue<String> cols) {
        return lst.stream().sorted((row1, row2) -> {
            int compare = 0;
            Queue<String> colsCopy = new LinkedList<>(cols);
            while (!colsCopy.isEmpty()) {
                int col = columnLabelToNumber(colsCopy.poll());
                EffectiveValue effectiveValue1 = sheet.GetCell(CoordinateFactory.getCoordinate(row1, col)).GetEffectiveValue();
                EffectiveValue effectiveValue2 = sheet.GetCell(CoordinateFactory.getCoordinate(row2, col)).GetEffectiveValue();
                if(effectiveValue1.getValueType() != ValueType.NUMERIC){
                    return 1;
                }
                if(effectiveValue2.getValueType() != ValueType.NUMERIC){
                    return -1;
                }
                compare = Double.compare(effectiveValue1.getValueWithExpectation(Double.class), effectiveValue2.getValueWithExpectation(Double.class));
                if (compare != 0) {
                    return compare;
                }
            }
            return compare;
        }).collect(Collectors.toList());
    }

    private static List<Integer> creteListOfRowsInRange(SheetToFilter sheet, Range range) {
        return sheet.GetCells()
                .keySet()
                .stream()
                .filter(range::containsCell)
                .filter(cell -> cell.col() == range.startCellCoordinate().col())
                .mapToInt(Coordinate::row)
                .boxed()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private static int columnLabelToNumber(String label) {
        int result = 0;
        for (int i = 0; i < label.length(); i++) {
            result *= 26;
            result += label.charAt(i) - 'A' + 1;
        }
        return result;
    }

}
