package shticell.sheet.cell.impl;

import shticell.sheet.cell.connection.CellConnection;
import shticell.operation.Exceptions.NumberOperationException;
import shticell.operation.Exceptions.OperationException;
import shticell.sheet.cell.connection.CellConnectionImpl;
import shticell.sheet.api.CellCoordinator;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.exception.LoopConnectionException;
import shticell.sheet.cell.api.Cell;
import java.text.NumberFormat;
import java.util.*;

import static shticell.function.api.functionIdentifier.calcFunc;
import static shticell.function.api.functionIdentifier.isFunc;

public class CellImpl implements Cloneable, Cell {

    private final String NAN = "NaN";
    private final String UNDEFINED = "!Undefined!";
    private CellCoordinator sheet;
    private Coordinate coordinate;
    private String originalValue = "";
    private String effectiveValue = "";
    private final TreeMap<Integer, Cell> cellByVersion= new TreeMap<>();
    private int LatestSheetVersionUpdated;
    private CellConnection connections = new CellConnectionImpl(coordinate);

    public CellImpl(){};

    public CellImpl(Coordinate _coordinate, CellCoordinator sheet, int currentSheetVersion){
        originalValue = effectiveValue = "";
        coordinate = _coordinate;
        this.sheet = sheet;
        cellByVersion.put(currentSheetVersion,this.clone());
        LatestSheetVersionUpdated  = currentSheetVersion;
        connections = new CellConnectionImpl(coordinate);
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
    public String GetEffectiveValue() { return effectiveValue; }

    @Override
    public Coordinate GetCellCoordinate() { return coordinate; }

    @Override
    public int GetVersion() { return LatestSheetVersionUpdated; }

    @Override
    public CellConnection GetConnections() { return connections; }

    @Override
    public Cell GetCellBySheetVersion(int version){
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
    public CellImpl clone(){
        CellImpl clonedCellImpl = new CellImpl();
        clonedCellImpl.originalValue = originalValue;
        clonedCellImpl.effectiveValue = effectiveValue;
        clonedCellImpl.coordinate = coordinate;
        clonedCellImpl.sheet = sheet;

        return clonedCellImpl;
    }

    private String addThousandsSeparator(String number) throws NumberFormatException {
        return NumberFormat.getNumberInstance(Locale.US).format(Double.parseDouble(number));
    }


    @Override
    public void UpdateCell(String newOriginalValue, int sheetVersion) throws  LoopConnectionException,OperationException{
            List<CellConnection> removed = new ArrayList<>(connections.ClearDependsOn());
        try{
            cellByVersion.put(sheetVersion, this.clone());
            LatestSheetVersionUpdated = sheetVersion;
            originalValue = newOriginalValue;
            effectiveValue = parseEffectiveValue(newOriginalValue);
            sheet.UpdateDependentCells(connections.GetSortedInfluenceOn().stream().map(CellConnection::GetCellCoordinate).toList());
        }
        catch (NumberOperationException e){
            effectiveValue = NAN;
        }
        catch(IndexOutOfBoundsException e){
            effectiveValue = UNDEFINED;
        }
        catch (OperationException | LoopConnectionException e) {
            connections.AddListToInfluenceOn(removed);
            Cell backUp = cellByVersion.lastEntry().getValue();
            effectiveValue = backUp.GetEffectiveValue();
            LatestSheetVersionUpdated = backUp.GetVersion();
            originalValue = backUp.GetOriginalValue();
            cellByVersion.remove(cellByVersion.lastEntry().getKey());
            throw e;
        }
    }

    private String parseEffectiveValue(String newOriginalValue) throws NumberFormatException, LoopConnectionException, OperationException, NumberOperationException {
            String effectiveValue = isFunc(newOriginalValue) ? calcFunc(newOriginalValue,connections, sheet) : String.valueOf(newOriginalValue);
            return !effectiveValue.isEmpty() && effectiveValue.chars().allMatch(Character::isDigit)
                    ? addThousandsSeparator(String.valueOf(effectiveValue))
                    : effectiveValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CellImpl) {
            return coordinate.equals(((CellImpl)obj).GetCellCoordinate());
        }

        return false;
    }

}

