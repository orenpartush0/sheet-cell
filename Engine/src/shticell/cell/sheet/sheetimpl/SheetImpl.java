package shticell.cell.sheet.sheetimpl;

import shticell.cell.sheet.api.Sheet;
import shticell.operation.Exceptions.NumberOperationException;
import shticell.operation.Exceptions.OperationException;
import shticell.cell.sheet.api.CellCoordinator;
import shticell.cell.ties.CellConnection;
import shticell.exception.LoopConnectionException;
import shticell.cell.api.Cell;
import shticell.cell.impl.CellImpl;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SheetImpl implements CellCoordinator, Sheet {
    private final int INITIAL_VERSION = 1;
    private final String sheetName;
    private int version = INITIAL_VERSION;
    Map<String, CellImpl> cells;
    private final int numberOfRows;
    private final int numberOfColumns;

    public SheetImpl(String sheetTitle, int _numberOfRows, int _numberOfColumns) {
        sheetName = sheetTitle;
        numberOfRows = _numberOfRows;
        numberOfColumns = _numberOfColumns;
        cells = new HashMap<>();

        IntStream.range(0,_numberOfRows).forEach(i -> {
                    IntStream.range(0,_numberOfColumns).forEach(j -> {
                        String square = String.valueOf((char)('A' + i)) + String.valueOf(j+ 1);
                        cells.put(square, new CellImpl(square,this,INITIAL_VERSION));
                    });
                }
                );
    }

    public SheetImpl(String sheetTitle, int _numberOfRows, int _numberOfColumns, Map<String, CellImpl> _cells)
    {
        this(sheetTitle,_numberOfRows,_numberOfColumns);
        cells = _cells;
    }


    @Override
    public int GetVersion() {return version;}

    @Override
    public String GetSheetName() {return sheetName;}

    @Override
    public int GetNumberOfRows() {return numberOfRows;}

    @Override
    public int GetNumberOfColumns() {return numberOfColumns;}

    @Override
    public Map<String, CellImpl> GetCells() { return cells; }

    @Override
    public CellImpl GetCell(String cellId) { return cells.get(cellId);}

    @Override
    public CellConnection GetCellConnections(String cellId){return cells.get(cellId).GetConnections();}

    public void UpdateCellByCoordinate(String coordinate, String newValue) throws LoopConnectionException, OperationException, NumberOperationException {
        try{cells.get(coordinate.toUpperCase()).UpdateCell(newValue,++version);}
        catch (LoopConnectionException | OperationException | RuntimeException e){version--; throw e;};
    }

    public List<Integer> GetCountOfChangesPerVersion(){
        List<Integer> changes = new ArrayList<>(Collections.nCopies(version,0));
        final int from = 2;
        final int to = version + 1;

        for (Map.Entry<String, CellImpl> entry : cells.entrySet()) {
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
                CellImpl cellImplNeedToBeUpdated = cells.get(cellId);
                cellImplNeedToBeUpdated.UpdateCell(cellImplNeedToBeUpdated.GetOriginalValue(), version);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public List<Integer> getColsSize() {
        return IntStream.range(0, numberOfColumns)
                .mapToObj(col -> IntStream.range(0, numberOfRows)
                        .map(row -> {
                            char rowLabel = (char) ('A' + row);
                            String cellKey = rowLabel + String.valueOf(col + 1);
                            CellImpl cellImpl = cells.get(cellKey);
                            String effectiveValue = cellImpl.GetEffectiveValue();
                            return effectiveValue.length();
                        })
                        .max()
                        .orElse(5))
                .collect(Collectors.toList());
    }

    public SheetImpl GetSheetByVersion(int version){

        Map<String, CellImpl> cellsInRequiredVersion = new HashMap<>();

        cells.forEach((key, value) -> {
            Cell cellData = value.GetCellBySheetVersion(version);
            cellsInRequiredVersion.put(key, new CellImpl(cellData));
        });

        return new SheetImpl(sheetName,numberOfRows,numberOfColumns,cellsInRequiredVersion);
    }

}
