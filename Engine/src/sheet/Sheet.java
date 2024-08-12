package sheet;

import sheet.Interface.CellCoordinator;
import Operation.Exceptions.OperationException;
import sheet.Exception.LoopConnectionException;
import dto.CellDto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sheet implements CellCoordinator {
    private final int INITIAL_VERSION = 1;
    private final String sheetName;
    private int version = INITIAL_VERSION;
    Map<String, Cell> cells = new HashMap<>();
    HashMap<String,CellConnection> connections = new HashMap<>();
    private final int numberOfRows;
    private final int numberOfColumns;

    public Sheet(String sheetTitle, int _numberOfRows, int _numberOfColumns) {
        sheetName = sheetTitle;
        numberOfRows = _numberOfRows;
        numberOfColumns = _numberOfColumns;

        for (int i = 0; i < _numberOfRows; i++) {
            for (int j = 0; j < _numberOfColumns; j++) {
                String square = String.valueOf((char)('A' + i)) + String.valueOf(j+ 1);
                cells.put(square, new Cell(square,this,INITIAL_VERSION));
                connections.put(square,new CellConnection(square));
            }
        }


    }

    public int GetVersion() {
        return version;
    }

    public String GetSheetName() {
        return sheetName;
    }

    public int GetNumberOfRows() {return numberOfRows;}

    public int GetNumberOfColumns() {return numberOfColumns;}

    public Map<String, CellDto> GetCellsDTO(){
        Map<String, CellDto> cellDto = new HashMap<>();
        for (Map.Entry<String, Cell> entry : cells.entrySet()) {
            cellDto.put(entry.getKey(),new CellDto(entry.getValue()));
        }

        return cellDto;
    }

    public String GetCellData(String cellId) {
        return cells.get(cellId).toString();
    }


    public void UpdateCellByIndex(String square, String newValue) throws OperationException, LoopConnectionException {
        cells.get(square.toUpperCase()).UpdateCell(newValue,++version);
    }

    @Override
    public String GetCellEffectiveValue(String square) {
        return cells.get(square).GetEffectiveValue();
    }

    public void SetInfluenceBetweenTwoCells(String referrerCell, String referencedCell ) throws LoopConnectionException {
        CellConnection.hasPath(connections.get(referencedCell), connections.get(referrerCell));
        connections.get(referrerCell).addReferenceFromThisCell(connections.get(referencedCell));
    }


    @Override
    public ArrayList<String> GetListOfReferencedCells(String referrerCell) {
        return connections.get(referrerCell).getReferencesFromThisCell();
    }


    @Override
    public ArrayList<String> GetListOfReferencerCells(String referredCell) {
        return connections.get(referredCell).getReferencesToThisCell();
    }

    public List<Integer> getColSize() {
        List<Integer> columnSizes = new ArrayList<>();

        for (int col = 0; col < numberOfColumns; col++) {
            int maxColWidth = 0;

            for (int row = 0; row < numberOfRows; row++) {
                char rowLabel = (char) ('A' + row);
                String cellKey = rowLabel + String.valueOf(col + 1);
                Cell cell = cells.get(cellKey);

                if (cell != null) {
                    String effectiveValue = cell.GetEffectiveValue();
                    if (effectiveValue != null) {
                        int cellLength = effectiveValue.length();
                        maxColWidth = Math.max(maxColWidth, cellLength);
                    }
                }
            }

            columnSizes.add(maxColWidth);
        }

        return columnSizes;
    }


}
