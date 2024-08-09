package sheet;

import Interfaces.HasDataOnOtherCells;
import Operation.Operation;

import java.util.HashMap;
import java.util.Map;

public class Sheet implements HasDataOnOtherCells {
    private final int INITIAL_VERSION = 1;

    public String sheetName;
    private int version = INITIAL_VERSION;
    Map<String, Cell> cells = new HashMap<>();
    private int currentVersion;

    public Sheet(String sheetTitle, int numberOfRows, int numberOfColumns) {
        sheetName = sheetTitle;
        int cellId = 0;
        currentVersion = 0;

        for (int i = 0; i < numberOfRows; i++) {
            for (int j = 0; j < numberOfColumns; j++) {
                String square = String.valueOf('A' + i) + String.valueOf(j + 1);
                cells.put(square, new Cell(square,this,0));
            }
        }
    }

    public void UpdateVersion() {
        version++;
    }

    public int GetVersion() {
        return version;
    }

    public String GetSheetName() {
        return sheetName;
    }

    public void UpdateCellByIndex(String square, String newValue) {
        currentVersion++;
        cells.get(square).UpdateCell(newValue,currentVersion);
    }

    @Override
    public String GetCellEffectiveValue(String square) {
        return cells.get(square).GetEffectiveValue();
    }

}