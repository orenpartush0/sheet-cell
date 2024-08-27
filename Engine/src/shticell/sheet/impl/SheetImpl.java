package shticell.sheet.impl;

import shticell.sheet.api.Sheet;
import shticell.sheet.api.HasSheetData;
import shticell.sheet.api.SheetToXML;
import shticell.sheet.cell.connection.CellConnection;
import shticell.sheet.cell.value.EffectiveValue;
import shticell.sheet.cell.value.ValueType;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.coordinate.CoordinateFactory;
import shticell.sheet.exception.LoopConnectionException;
import shticell.sheet.cell.api.Cell;
import shticell.sheet.cell.impl.CellImpl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SheetImpl implements HasSheetData, Sheet, SheetToXML {
    private final int INITIAL_VERSION = 1;
    private final String sheetName;
    private int version = INITIAL_VERSION;
    Map<Coordinate, Cell> cells;
    private final int numberOfRows;
    private final int numberOfColumns;
    private final int rowHeight;
    private final int columnWidth;

    public SheetImpl(String _sheetName, int _numberOfRows, int _numberOfColumns, int _rowHeight, int _columnWidth) {
        sheetName = _sheetName;
        numberOfRows = _numberOfRows;
        numberOfColumns = _numberOfColumns;
        rowHeight = _rowHeight;
        columnWidth = _columnWidth;
        cells = new HashMap<>();

        for(int i =1; i <= numberOfRows; i++) {
            for(int j =0; j< numberOfColumns; j++) {
                Coordinate coordinate = CoordinateFactory.getCoordinate(i,j);
                cells.put(coordinate,new CellImpl(coordinate,this,INITIAL_VERSION));
            }
        }
    }

    public SheetImpl(String sheetTitle, int _numberOfRows, int _numberOfColumns, Map<Coordinate, Cell> _cells, int _rowHeight, int _columnWidth)
    {
        this(sheetTitle,_numberOfRows,_numberOfColumns,_rowHeight,_columnWidth);
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
    public int getColsWidth() {return columnWidth;}

    @Override
    public int getRowsHeight() {return rowHeight;}

    @Override
    public EffectiveValue GetCellEffectiveValue(Coordinate coordinate) {
        return cells.get(coordinate).GetEffectiveValue();
    }

    @Override
    public void UpdateCellByCoordinateWithOutVersionUpdate(Coordinate coordinate, String newValue) throws LoopConnectionException {
        try{cells.get(coordinate).UpdateCell(newValue,version);}
        catch (LoopConnectionException | RuntimeException e){throw e;};
    }

    public void UpdateCellByCoordinate(Coordinate coordinate, String newValue) throws LoopConnectionException {
        try{cells.get(coordinate).UpdateCell(newValue,++version);}
        catch (LoopConnectionException | RuntimeException e){version--; throw e;};
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
    public void UpdateDependentCells(List<Coordinate> coordinates) {
        coordinates.forEach(cellCoordinate -> {
            try {
                Cell cellImplNeedToBeUpdated = cells.get(cellCoordinate);
                cellImplNeedToBeUpdated.UpdateCell(cellImplNeedToBeUpdated.GetOriginalValue(), version);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CellConnection GetCellConnections(Coordinate coordinate) {
        return cells.get(coordinate).GetConnections();
    }

    @Override
    public boolean IsCoordinateInSheet(Coordinate coordinate) {
        return cells.containsKey(coordinate);
    }

    @Override
    public SheetImpl GetSheetByVersion(int version){

        Map<Coordinate, Cell> cellsInRequiredVersion = new HashMap<>();

        cells.forEach((key, value) -> {
            Cell cellData = value.GetCellBySheetVersion(version);
            cellsInRequiredVersion.put(key, new CellImpl(cellData));
        });

        return new SheetImpl(sheetName,numberOfRows,numberOfColumns,cellsInRequiredVersion,rowHeight,columnWidth);
    }

}
