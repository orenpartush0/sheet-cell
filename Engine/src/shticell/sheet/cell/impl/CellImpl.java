package shticell.sheet.cell.impl;

import shticell.expression.impl.BooleanFuncUnknown;
import shticell.sheet.cell.connection.CellConnection;
import shticell.sheet.cell.connection.CellConnectionImpl;
import shticell.sheet.api.HasSheetData;
import shticell.sheet.cell.value.EfectiveValueFactory;
import shticell.sheet.cell.value.EffectiveValue;
import shticell.sheet.cell.value.EffectiveValueImpl;
import shticell.sheet.cell.value.ValueType;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.exception.LoopConnectionException;
import shticell.sheet.cell.api.Cell;
import shticell.sheet.range.Range;
import shticell.sheet.range.RangeWithCounter;
import shticell.sheet.range.RangeWithCounterImpl;

import java.io.Serializable;
import java.util.*;

import static shticell.function.functionIdentifier.calcFunc;
import static shticell.function.functionIdentifier.isFunc;

public class CellImpl implements Cell, Serializable {

    private final String NAN = "NaN";
    private final String UNDEFINED = "!Undefined!";
    private final String UNKNOWN = "Unknown";
    private HasSheetData sheet;
    private Coordinate coordinate;
    private String originalValue = "";
    private EffectiveValue effectiveValue;
    private final TreeMap<Integer, Cell> cellByVersion= new TreeMap<>();
    private int LatestSheetVersionUpdated;
    private CellConnection connections = new CellConnectionImpl(coordinate);
    private Map<String, RangeWithCounter> usedRanges = new HashMap<>();

    public CellImpl(){};

    public CellImpl(Coordinate _coordinate, HasSheetData sheet, int currentSheetVersion){
        originalValue = "";
        effectiveValue = new EffectiveValueImpl("",ValueType.STRING);
        coordinate = _coordinate;
        this.sheet = sheet;
        LatestSheetVersionUpdated  = currentSheetVersion;
        connections = new CellConnectionImpl(coordinate);
        cellByVersion.put(currentSheetVersion,this.clone());
    }

    public CellImpl(Cell cell){
        coordinate = cell.GetCellCoordinate();
        originalValue = cell.GetOriginalValue();
        effectiveValue = cell.GetEffectiveValue();
    }

    @Override
    public boolean IsChangedInThisVersion(int version) {return cellByVersion.get(version) != null; }

    @Override
    public String GetOriginalValue() { return originalValue; }

    @Override
    public EffectiveValue GetEffectiveValue() { return effectiveValue; }

    @Override
    public Coordinate GetCellCoordinate() { return coordinate; }

    @Override
    public int GetVersion() { return LatestSheetVersionUpdated; }

    @Override
    public CellConnection GetConnections() { return connections; }

    @Override
    public Cell GetCellBySheetVersion(int version){
        Cell tmp;
        return cellByVersion.get(cellByVersion.floorKey(version));
    }

    @Override
    public ArrayList<Coordinate> GetDependsOnCoordinates() { return connections.GetDependsOnCoordinates(); }

    @Override
    public ArrayList<Coordinate> GetInfluenceOnCoordinates() {
        return connections.GetInfluenceOnCoordinates();
    }

    @Override
    public Cell clone(){
        CellImpl clonedCellImpl = new CellImpl();
        clonedCellImpl.originalValue = originalValue;
        clonedCellImpl.coordinate = coordinate;
        clonedCellImpl.effectiveValue = effectiveValue.Clone();
        clonedCellImpl.LatestSheetVersionUpdated = LatestSheetVersionUpdated;

        return clonedCellImpl;
    }

    private Map<String, RangeWithCounter> clearUsedRanges() {
        Map<String, RangeWithCounter> preUpdateUsedRanges = new HashMap<>(usedRanges);
        usedRanges.clear();
        preUpdateUsedRanges.forEach((key, value) -> sheet.UnUseRange(value.GetRange()));
        return preUpdateUsedRanges;
    }

    private void recoverUsedRanges(Map<String, RangeWithCounter> preUpdateUsedRanges){
        usedRanges.clear();
        preUpdateUsedRanges.forEach((key,value)->sheet.UseRange(coordinate,value.GetRange()));
    }

    @Override
    public void UpdateCell(String newOriginalValue, int sheetVersion) throws  LoopConnectionException{
        List<CellConnection> removed = new ArrayList<>(connections.ClearDependsOn());
        EffectiveValue preUpdateEffectiveValue = effectiveValue.Clone();
        Map<String, RangeWithCounter> preUpdateUsedRanges = clearUsedRanges();
        usedRanges.clear();

        try
        {
            effectiveValue = parseEffectiveValue(newOriginalValue);
            sheet.UpdateDependentCells(connections.GetSortedInfluenceOn().stream().map(CellConnection::GetCellCoordinate).toList());
        }
        catch (ArithmeticException e){
            effectiveValue = new EffectiveValueImpl(NAN, ValueType.NAN);
        }
        catch (IndexOutOfBoundsException | IllegalArgumentException e){
            effectiveValue = new EffectiveValueImpl(UNDEFINED, ValueType.UNDEFINED);
        }
        catch (BooleanFuncUnknown e){
            effectiveValue = new EffectiveValueImpl(UNKNOWN, ValueType.UNKNOWN);
        }
        catch (UnsupportedOperationException | LoopConnectionException e){
            connections.recoverDependencies(removed);
            effectiveValue = preUpdateEffectiveValue;
            recoverUsedRanges(preUpdateUsedRanges);
            throw e;
        }

        LatestSheetVersionUpdated = sheetVersion;
        originalValue = newOriginalValue;
        cellByVersion.put(sheetVersion, this.clone());
    }

    private EffectiveValue parseEffectiveValue(String newOriginalValue) throws NumberFormatException, LoopConnectionException {
        return isFunc(newOriginalValue)
                    ? calcFunc(new EffectiveValueImpl(newOriginalValue,ValueType.STRING),connections, sheet)
                    : EfectiveValueFactory.getEffectiveValue(newOriginalValue,sheet);

    }

    @Override
    public void UseRange(Range range){
        if(usedRanges.get(range.rangeName()) == null){
            usedRanges.put(range.rangeName(),new RangeWithCounterImpl(range,1));
        }
        else{
            usedRanges.get(range.rangeName()).AddUsing();
        }
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CellImpl) {
            return coordinate.equals(((CellImpl)obj).GetCellCoordinate());
        }

        return false;
    }

}

