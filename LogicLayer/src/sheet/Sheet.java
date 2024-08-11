package sheet;

import Interfaces.CellCoordinator;
import Interfaces.Operation.Exceptions.OperationException;
import sheet.Exceptions.LoopConnectionException;

import java.util.HashMap;
import java.util.Map;

public class Sheet implements CellCoordinator {
    private final int INITIAL_VERSION = 1;
    public String sheetName;
    private int version = INITIAL_VERSION;
    Map<String, Cell> cells = new HashMap<>();
    Map<Integer,HashMap<String,CellConnection>> connectionsVersions = new HashMap<>();
    private final int numberOfRows;
    private final int numberOfColumns;

    public Sheet(String sheetTitle, int _numberOfRows, int _numberOfColumns) {
        sheetName = sheetTitle;
        numberOfRows = _numberOfRows;
        numberOfColumns = _numberOfColumns;

        HashMap<String,CellConnection> connections =  new HashMap<>();
        for (int i = 0; i < _numberOfRows; i++) {
            for (int j = 0; j < _numberOfColumns; j++) {
                String square = 'A' + i + String.valueOf(j + 1);
                cells.put(square, new Cell(square,this,INITIAL_VERSION));
                connections.put(square,new CellConnection(square));
            }
        }

        connectionsVersions.put(INITIAL_VERSION,connections);
    }

    public int GetVersion() {
        return version;
    }

    public String GetSheetName() {
        return sheetName;
    }

    public void UpdateCellByIndex(String square, String newValue) throws OperationException, LoopConnectionException {
        cells.get(square.toUpperCase()).UpdateCell(newValue,version);
        version++;
    }

    @Override
    public String GetCellEffectiveValue(String square) {
        return cells.get(square).GetEffectiveValue();
    }

    public void SetInfluenceBetweenTwoCells(String referrerCell, String referencedCell ) throws LoopConnectionException {
        CellConnection.hasPath(connectionsVersions.get(version).get(referencedCell),connectionsVersions.get(version).get(referrerCell));
        connectionsVersions.put(version + 1, (HashMap<String, sheet.CellConnection>) connectionsVersions.get(version).clone());
        connectionsVersions.get(version + 1).get(referrerCell).addNeighbor(connectionsVersions.get(version + 1).get(referencedCell));
    }

    public int getColSize(int col){
        int max = 5;
        for(int i= 0 ; i < numberOfRows; i++){
            char row = (char)('A' + i);
            int cellLength = cells.get(row + String.valueOf(col)).GetEffectiveValue().length();
            max = Math.max(max,cellLength);
        }

        return max;
    }

}