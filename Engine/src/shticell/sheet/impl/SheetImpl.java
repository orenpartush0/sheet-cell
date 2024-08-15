package shticell.sheet.impl;

import shticell.sheet.cell.connection.CellConnection;
import shticell.sheet.api.Sheet;
import shticell.operation.Exceptions.NumberOperationException;
import shticell.operation.Exceptions.OperationException;
import shticell.sheet.api.CellCoordinator;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.coordinate.CoordinateFactory;
import shticell.sheet.exception.LoopConnectionException;
import shticell.sheet.cell.api.Cell;
import shticell.sheet.cell.impl.CellImpl;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SheetImpl implements CellCoordinator, Sheet {
    private final int INITIAL_VERSION = 1;
    private final String sheetName;
    private int version = INITIAL_VERSION;
    Map<Coordinate, Cell> cells;
    private final int numberOfRows;
    private final int numberOfColumns;

    public SheetImpl(String _sheetName, int _numberOfRows, int _numberOfColumns) {
        sheetName = _sheetName;
        numberOfRows = _numberOfRows;
        numberOfColumns = _numberOfColumns;
        cells = new HashMap<>();

        for(int i =0; i<numberOfRows; i++) {
            for(int j =1; j<= numberOfColumns; j++) {
                Coordinate coordinate = CoordinateFactory.getCoordinate(i,j);
                cells.put(coordinate,new CellImpl(coordinate,this,INITIAL_VERSION));
            }
        }
    }

    public SheetImpl(String sheetTitle, int _numberOfRows, int _numberOfColumns, Map<Coordinate, Cell> _cells)
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
    public Map<Coordinate, Cell> GetCells() { return cells; }

    @Override
    public Cell GetCell(Coordinate coordinate) { return cells.get(coordinate);}

    @Override
    public CellConnection GetCellConnections(Coordinate coordinate){return cells.get(coordinate).GetConnections();}

    @Override
    public String GetCellEffectiveValue(Coordinate coordinate) {
        return cells.get(coordinate).GetEffectiveValue();
    }

    public void UpdateCellByCoordinate(Coordinate coordinate, String newValue) throws LoopConnectionException, OperationException, NumberOperationException {
        try{cells.get(coordinate).UpdateCell(newValue,++version);}
        catch (LoopConnectionException | OperationException | RuntimeException e){version--; throw e;};
    }

    @Override
    public List<Integer> GetCountOfChangesPerVersion(){
        List<Integer> changes = new ArrayList<>(Collections.nCopies(version,0));
        final int from = 2;
        final int to = version + 1;

        for (Map.Entry<Coordinate, Cell> entry : cells.entrySet()) {
            IntStream.range(from,to).forEach(i -> {
                if (entry.getValue().IsChangedInThisVersion(i)) {
                    changes.set(i - 1, changes.get(i - 1) + 1);
                }
            });
        }

        return changes;
    }

    @Override
    public void UpdateDependentCells(List<CellConnection> dependentCells) {
        dependentCells.stream().skip(1).forEach(dependentCell -> {
            try {
                Cell cellImplNeedToBeUpdated = cells.get(dependentCell.GetCellCoordinate());
                cellImplNeedToBeUpdated.UpdateCell(cellImplNeedToBeUpdated.GetOriginalValue(), version);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public List<Integer> getColsSize() {
        return IntStream.range(1, numberOfColumns + 1)
                .mapToObj(col -> IntStream.range(0, numberOfRows)
                        .map(row -> {
                            Cell cellImpl = cells.get(CoordinateFactory.getCoordinate(row,col));
                            String effectiveValue = cellImpl.GetEffectiveValue();
                            return effectiveValue.length();
                        })
                        .max()
                        .orElse(5))
                .collect(Collectors.toList());
    }

    @Override
    public SheetImpl GetSheetByVersion(int version){

        Map<Coordinate, Cell> cellsInRequiredVersion = new HashMap<>();

        cells.forEach((key, value) -> {
            Cell cellData = value.GetCellBySheetVersion(version);
            cellsInRequiredVersion.put(key, new CellImpl(cellData));
        });

        return new SheetImpl(sheetName,numberOfRows,numberOfColumns,cellsInRequiredVersion);
    }

}
