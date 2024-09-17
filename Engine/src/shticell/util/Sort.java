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

        sortLst(lst, sheet, cols);

        for (int newRowIndex = 0; newRowIndex < lst.size(); newRowIndex++) {
            int oldRowIndex = lst.get(newRowIndex);
            for (int col = 0; col < sheet.GetNumberOfColumns(); col++) {
                Coordinate oldCoordinate = CoordinateFactory.getCoordinate(oldRowIndex, col);
                Coordinate newCoordinate = CoordinateFactory.getCoordinate(newRowIndex, col);
                try{sheetInRange.UpdateCellByCoordinate(newCoordinate, sheet.GetCell(oldCoordinate).GetEffectiveValue().getValue().toString());}
                catch (Exception ignored){};

            }
        }

        return new SheetDto(sheetInRange);
    }

    private static void sortLst(List<Integer> lst, SheetToFilter sheet, Queue<String> cols) {
        lst.sort((row1, row2) -> {
            int compare = 0;
            Queue<String> colsCopy = new LinkedList<>(cols);
            while (!colsCopy.isEmpty()) {
                int col = columnLabelToNumber(colsCopy.poll());
                EffectiveValue effectiveValue1 = sheet.GetCell(CoordinateFactory.getCoordinate(row1, col)).GetEffectiveValue();
                EffectiveValue effectiveValue2 = sheet.GetCell(CoordinateFactory.getCoordinate(row2, col)).GetEffectiveValue();

                if (effectiveValue1.getValueType() != ValueType.NUMERIC || effectiveValue2.getValueType() != ValueType.NUMERIC) {
                    return 0;
                } else {
                    compare = Integer.compare(effectiveValue1.getValueWithExpectation(Integer.class), effectiveValue2.getValueWithExpectation(Integer.class));
                    if (compare != 0) {
                        return compare;
                    }
                }
            }
            return compare;
        });
    }

    private static List<Integer> creteListOfRowsInRange(SheetToFilter sheet, Range range) {
        return sheet.GetCells()
                .keySet()
                .stream()
                .filter(range::containsCell)
                .filter(cell -> cell.col() == range.startCellCoordinate().col())
                .mapToInt(Coordinate::row)
                .boxed()
                .collect(Collectors.toCollection(ArrayList::new)); // Use ArrayList here
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
