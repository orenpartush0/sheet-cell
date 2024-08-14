package sheet;

import operation.Exceptions.NumberOperationException;
import operation.Exceptions.OperationException;
import sheet.Interface.CellCoordinator;
import sheet.exception.LoopConnectionException;
import dto.CellDto;
import sheet.Interface.HasCellData;

import java.util.*;
import java.util.stream.IntStream;

public class Sheet implements CellCoordinator {
    private final int INITIAL_VERSION = 1;
    private final String sheetName;
    private int version = INITIAL_VERSION;
    Map<String, Cell> cells;
    private final int numberOfRows;
    private final int numberOfColumns;

    public Sheet(String sheetTitle, int _numberOfRows, int _numberOfColumns) {
        sheetName = sheetTitle;
        numberOfRows = _numberOfRows;
        numberOfColumns = _numberOfColumns;
        cells = new HashMap<>();

        for (int i = 0; i < _numberOfRows; i++) {
            for (int j = 0; j < _numberOfColumns; j++) {
                String square = String.valueOf((char)('A' + i)) + String.valueOf(j+ 1);
                cells.put(square, new Cell(square,this,INITIAL_VERSION));
            }
        }
    }

    public Sheet(String sheetTitle, int _numberOfRows, int _numberOfColumns,Map<String, Cell> _cells)
    {
        this(sheetTitle,_numberOfRows,_numberOfColumns);
        cells = _cells;
    }


    public int GetVersion() {return version;}

    public String GetSheetName() {return sheetName;}

    public int GetNumberOfRows() {return numberOfRows;}

    public int GetNumberOfColumns() {return numberOfColumns;}

    public Map<String, CellDto> GetCellsDTO(){
        Map<String, CellDto> cellDto = new HashMap<>();
        for (Map.Entry<String, Cell> entry : cells.entrySet()) {
            cellDto.put(entry.getKey(),new CellDto(entry.getValue()));
        }

        return cellDto;
    }

    public CellDto GetCellData(String cellId) {
        return new CellDto(cells.get(cellId));
    }

    @Override
    public CellConnection GetCellConnection(String cellId){return cells.get(cellId).GetConnection();}

    public void UpdateCellByIndex(String square, String newValue) throws LoopConnectionException, OperationException, NumberOperationException {
        try{cells.get(square.toUpperCase()).UpdateCell(newValue,++version);}
        catch (LoopConnectionException | OperationException e){version--; throw e;};
    }

    public List<Integer> GetCountOfChangesPerVersion(){
        List<Integer> changes = new ArrayList<>(Collections.nCopies(version,0));
        final int from = 2;
        final int to = version + 1;

        for (Map.Entry<String, Cell> entry : cells.entrySet()) {
            IntStream.range(from,to).forEach(i -> {
                if (entry.getValue().IsChangedInThisVersion(i)) {
                    changes.set(i - 1, changes.get(i - 1) + 1);
                }
            });
        }

        return changes;
    }

    @Override
    public String GetCellEffectiveValue(String square) {
        return cells.get(square).GetEffectiveValue();
    }

    public void UpdateDependentCells(List<String> dependentCells) {
        dependentCells.stream().skip(1).forEach(cellId -> {
            try {
                Cell cellNeedToBeUpdated = cells.get(cellId);
                cellNeedToBeUpdated.UpdateCell(cellNeedToBeUpdated.GetOriginalValue(), version);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public List<Integer> getColsSize() {
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

    public Sheet GetSheetByVersion(int version){

        Map<String,Cell> cellsInRequiredVersion = new HashMap<>();

        for(Map.Entry<String, Cell> entry : cells.entrySet()){
            HasCellData cellData = entry.getValue().GetCellBySheetVersion(version);
            cellsInRequiredVersion.put(entry.getKey(),new Cell(cellData));
        }

        return new Sheet(sheetName,numberOfRows,numberOfColumns,cellsInRequiredVersion);
    }

}
