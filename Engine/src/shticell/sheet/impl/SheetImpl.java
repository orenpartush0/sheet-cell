package shticell.sheet.impl;

import shticell.sheet.api.Sheet;
import shticell.sheet.api.HasSheetData;
import shticell.sheet.api.SheetToXML;
import shticell.sheet.cell.connection.CellConnection;
import shticell.sheet.cell.value.EffectiveValue;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.coordinate.CoordinateFactory;
import shticell.sheet.exception.LoopConnectionException;
import shticell.sheet.cell.api.Cell;
import shticell.sheet.cell.impl.CellImpl;
import shticell.sheet.range.Range;
import shticell.sheet.range.RangeWithCounter;
import shticell.sheet.range.RangeWithCounterImpl;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SheetImpl implements HasSheetData, Sheet, SheetToXML, Serializable ,Cloneable {
    private final int INITIAL_VERSION = 1;
    private final String sheetName;
    private int numOfChanges = 0;
    private int version = INITIAL_VERSION;
    Map<Coordinate, Cell> cells = new HashMap<>();
    Map<String, RangeWithCounter> ranges = new HashMap<>();
    private final int numberOfRows;
    private final int numberOfColumns;
    private final int rowHeight;
    private final int columnWidth;

    public final  ReadWriteLock sheetReadWriteLock = new ReentrantReadWriteLock();
    public final  Object RANGE_LOCK = new Object();
    public final  Object NUM_OF_CHANGE_LOCK = new Object();


    public SheetImpl(String _sheetName, int _numberOfRows, int _numberOfColumns, int _rowHeight, int _columnWidth) {
        sheetName = _sheetName;
        numberOfRows = _numberOfRows;
        numberOfColumns = _numberOfColumns;
        rowHeight = _rowHeight;
        columnWidth = _columnWidth;

        for(int i =1 ; i <= numberOfRows; i++) {
            for(int j = 1 ; j <=  numberOfColumns; j++) {
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
    public ReadWriteLock GetSheetReadWriteLock(){
        return sheetReadWriteLock;
    }

    @Override
    public int GetNumOfChanges(){
        return numOfChanges;
    }

    private void DecreaseNumOfChanges(){
        synchronized (NUM_OF_CHANGE_LOCK) {
            numOfChanges--;
        }
    }


    private void IncreaseNumOfChanges(){
        synchronized (NUM_OF_CHANGE_LOCK) {
            numOfChanges++;
        }
    }

    @Override
    public int GetVersion() {
        sheetReadWriteLock.readLock().lock();
        try {
            return version;
        }
        finally {
            sheetReadWriteLock.readLock().unlock();
        }
    }

    @Override
    public String GetSheetName() {return sheetName;}

    @Override
    public int GetNumberOfRows() {return numberOfRows;}

    @Override
    public int GetNumberOfColumns() {return numberOfColumns;}

    @Override
    public Map<Coordinate, Cell> GetCells() {
        sheetReadWriteLock.readLock().lock();
        try {
            return cells;
        }
        finally {
            sheetReadWriteLock.readLock().unlock();
        }
    }

    @Override
    public Cell GetCell(Coordinate coordinate) {
        sheetReadWriteLock.readLock().lock();
        try {
            return cells.get(coordinate);
        }
        finally {
            sheetReadWriteLock.readLock().unlock();
        }
    }

    @Override
    public int GetColsWidth() {return columnWidth;}

    @Override
    public int GetRowsHeight() {return rowHeight;}

    @Override
    public EffectiveValue GetCellEffectiveValue(Coordinate coordinate) {
        sheetReadWriteLock.readLock().lock();
        try {
            return cells.get(coordinate).GetEffectiveValue();
        }
        finally {
            sheetReadWriteLock.readLock().unlock();
        }
    }

    @Override
    public void UpdateCellByCoordinateWithOutVersionUpdate(Coordinate coordinate, String newValue) throws LoopConnectionException {
        cells.get(coordinate).UpdateCell(newValue,version,cells.get(coordinate).GetUserName());
    }

    @Override
    public void UpdateCellByCoordinate(Coordinate coordinate, String newValue,String userName) throws LoopConnectionException {
        sheetReadWriteLock.writeLock().lock();
        try{
            cells.get(coordinate).UpdateCell(newValue, ++version,userName);
            IncreaseNumOfChanges();
        }
        catch (LoopConnectionException | RuntimeException e){
            version--;
            DecreaseNumOfChanges();
            throw e;
        }
        finally {
            sheetReadWriteLock.writeLock().unlock();
        }
    }



    @Override//dont need to synchronize
    public void UpdateDependentCells(List<Coordinate> coordinates,String user) {
        coordinates.forEach(cellCoordinate -> {
            try {
                Cell cellImplNeedToBeUpdated = cells.get(cellCoordinate);
                cellImplNeedToBeUpdated.UpdateCell(cellImplNeedToBeUpdated.GetOriginalValue(), version,user);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CellConnection GetCellConnections(Coordinate coordinate) {
        sheetReadWriteLock.readLock().lock();
        try {
            return cells.get(coordinate).GetConnections();
        }finally {
            sheetReadWriteLock.readLock().unlock();
        }
    }

    @Override
    public boolean IsCoordinateInSheet(Coordinate coordinate) {
        return cells.containsKey(coordinate);
    }

    @Override
    public boolean IsRangeInSheet(String rangeName) {
       return ranges.containsKey(rangeName);
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

    @Override
    public void AddRange(Range rangeDto){
        synchronized (RANGE_LOCK) {
            if (ranges.containsKey(rangeDto.rangeName()) || ranges.values().stream().map(RangeWithCounter::GetRange).anyMatch(range -> range.equals(rangeDto))) {
                throw new RuntimeException("Range already exists");
            } else if (!cells.containsKey(rangeDto.endCellCoordinate()) || !cells.containsKey(rangeDto.startCellCoordinate())) {
                throw new RuntimeException("Range out of bounds");
            }
            ranges.put(rangeDto.rangeName(), new RangeWithCounterImpl(new Range(rangeDto.rangeName(), rangeDto.startCellCoordinate(), rangeDto.endCellCoordinate()), 0));
        }
        IncreaseNumOfChanges();
    }

    @Override
    public Range GetRangeDto(String rangeName){
        return ranges.get(rangeName).GetRange();
    }

    @Override//dont need to synchronize
    public void UseRange(Coordinate coordinate,Range range) {
        cells.get(coordinate).UseRange(range);
        ranges.get(range.rangeName()).AddUsing();
    }

    @Override
    public List<Range> GetRangesDto(){
        return ranges.values().stream().map(RangeWithCounter::GetRange).toList();
    }

    @Override
    public void UnUseRange(Range range){
        ranges.get(range.rangeName()).RemoveUsing();
    }

    @Override
    public Sheet clone(){
        Sheet clone = new SheetImpl(sheetName,numberOfRows,numberOfColumns,rowHeight,columnWidth);
        cells.forEach((key, value) -> clone.GetCells().put(key, value.clone()));
        ranges.forEach((key, value) ->clone.AddRange(new Range(key,value.GetRange().startCellCoordinate(),value.GetRange().endCellCoordinate())));
        return clone;
    }

    @Override
    public void RemoveRange(String rangeName) {
        synchronized (RANGE_LOCK) {
            if(ranges.containsKey(rangeName)){
                if (ranges.get(rangeName).GetCounter() != 0) {
                    throw new RuntimeException("Range in use");
                }
                ranges.remove(rangeName);
            }
        }
        IncreaseNumOfChanges();

    }

    @Override
    public String GetOriginalValue(Coordinate coordinate){
        sheetReadWriteLock.readLock().lock();
        try {
            return cells.get(coordinate).GetOriginalValue();
        }finally {
            sheetReadWriteLock.readLock().unlock();
        }
    }

    @Override
    public void applyDynamicCalculate(Coordinate coordinate, String numStr){
        try{cells.get(coordinate).UpdateCell(numStr,version,cells.get(coordinate).GetUserName());} catch (Exception ignored){}
    }

    @Override
    public boolean IsRangeInUse(String rangeName){
        synchronized (RANGE_LOCK) {
            return ranges.containsKey(rangeName);
        }
    }

}

