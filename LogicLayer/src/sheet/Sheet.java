package sheet;

import Interfaces.CellCoordinator;
import Operation.Exceptions.OperationException;
import sheet.Exceptions.LoopConnectionException;
import java.util.HashMap;
import java.util.Map;

public class Sheet implements CellCoordinator {
    private final int INITIAL_VERSION = 1;

    public String sheetName;
    private int version = INITIAL_VERSION;
    Map<String, Cell> cells = new HashMap<>();
    HashMap<String,CellConnection> connections = new HashMap<>();

    public Sheet(String sheetTitle, int numberOfRows, int numberOfColumns) {
        sheetName = sheetTitle;
        int cellId = 0;

        for (int i = 0; i < numberOfRows; i++) {
            for (int j = 0; j < numberOfColumns; j++) {
                String square = String.valueOf('A' + i) + String.valueOf(j + 1);
                cells.put(square, new Cell(square,this));
                connections.put(square,new CellConnection(square));
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

    public void UpdateCellByIndex(String square, String newValue) throws OperationException, LoopConnectionException {
        cells.get(square).UpdateCell(newValue);
    }

    @Override
    public String GetCellEffectiveValue(String square) {
        return cells.get(square).GetEffectiveValue();
    }

    public void SetInfluenceBetweenTwoCells(String referrerCell, String referencedCell ) throws LoopConnectionException {
        CellConnection.hasPath(connections.get(referencedCell),connections.get(referrerCell));
        connections.get(referrerCell).addNeighbor(connections.get(referencedCell));
    }

}