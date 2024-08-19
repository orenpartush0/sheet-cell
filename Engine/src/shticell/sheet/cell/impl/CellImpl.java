package shticell.sheet.cell.impl;

import shticell.sheet.cell.connection.CellConnection;
import shticell.sheet.cell.connection.CellConnectionImpl;
import shticell.sheet.api.HasSheetData;
import shticell.sheet.cell.value.EffectiveValue;
import shticell.sheet.cell.value.EffectiveValueImpl;
import shticell.sheet.cell.value.ValueType;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.coordinate.CoordinateFactory;
import shticell.sheet.exception.LoopConnectionException;
import shticell.sheet.cell.api.Cell;
import java.text.NumberFormat;
import java.util.*;

import static shticell.function.api.functionIdentifier.calcFunc;
import static shticell.function.api.functionIdentifier.isFunc;

public class CellImpl implements Cloneable, Cell {

    private final String NAN = "NaN";
    private final String UNDEFINED = "!Undefined!";
    private HasSheetData sheet;
    private Coordinate coordinate;
    private String originalValue = "";
    private EffectiveValue effectiveValue;
    private final TreeMap<Integer, Cell> cellByVersion= new TreeMap<>();
    private int LatestSheetVersionUpdated;
    private CellConnection connections = new CellConnectionImpl(coordinate);

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
    public ArrayList<String> GetDependsOnListOfStrings() {
        return connections.GetDependsOnListOfStrings();
    }

    @Override
    public ArrayList<String> GetInfluenceOnListOfStrings() {
        return connections.GetInfluenceOnListOfStrings();
    }

    @Override
    public Cell clone(){
        CellImpl clonedCellImpl = new CellImpl();
        clonedCellImpl.originalValue = originalValue;
        clonedCellImpl.effectiveValue = effectiveValue;
        clonedCellImpl.coordinate = coordinate;
        clonedCellImpl.sheet = sheet;
        clonedCellImpl.effectiveValue = effectiveValue.Clone();

        return clonedCellImpl;
    }

    @Override
    public void UpdateCell(String newOriginalValue, int sheetVersion) throws  LoopConnectionException{
            List<CellConnection> removed = new ArrayList<>(connections.ClearDependsOn());
            Cell backUp = this.clone();
        try{
            LatestSheetVersionUpdated = sheetVersion;
            originalValue = newOriginalValue;
            effectiveValue = parseEffectiveValue(newOriginalValue);
            sheet.UpdateDependentCells(connections.GetSortedInfluenceOn().stream().map(CellConnection::GetCellCoordinate).toList());
            cellByVersion.put(sheetVersion, this.clone());
        }
        catch (ArithmeticException e){
            effectiveValue = new EffectiveValueImpl(NAN, ValueType.STRING);
            cellByVersion.put(sheetVersion, this.clone());
        }
        catch(IndexOutOfBoundsException e){
            effectiveValue = new EffectiveValueImpl(UNDEFINED, ValueType.STRING);
            cellByVersion.put(sheetVersion, this.clone());
        }
        catch (Exception e) {
            connections.AddListToInfluenceOn(removed);
            effectiveValue = backUp.GetEffectiveValue();
            LatestSheetVersionUpdated = backUp.GetVersion();
            originalValue = backUp.GetOriginalValue();
            throw e;
        }
    }

    private EffectiveValue parseEffectiveValue(String newOriginalValue) throws NumberFormatException, LoopConnectionException {
        return isFunc(newOriginalValue)
                    ? calcFunc(new EffectiveValueImpl(newOriginalValue,ValueType.STRING),connections, sheet)
                    : getEffectiveValue(newOriginalValue);

    }

    private EffectiveValue getEffectiveValue(String originalValue){
        if(originalValue.matches("-?(0|[1-9][0-9]*)\\.?[0-9]*")){
            return new EffectiveValueImpl(Double.parseDouble(originalValue), ValueType.NUMERIC);
        }
        if(originalValue.equals("True") || originalValue.equals("False")){
            return new EffectiveValueImpl(Boolean.parseBoolean(originalValue),ValueType.BOOLEAN);
        }
        else
            return new EffectiveValueImpl(originalValue,ValueType.STRING);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CellImpl) {
            return coordinate.equals(((CellImpl)obj).GetCellCoordinate());
        }

        return false;
    }

}

