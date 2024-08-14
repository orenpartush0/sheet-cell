package shticell.cell.impl;

import shticell.operation.Exceptions.NumberOperationException;
import shticell.operation.Exceptions.OperationException;
import shticell.cell.ties.CellConnection;
import shticell.cell.sheet.api.CellCoordinator;
import shticell.exception.LoopConnectionException;
import shticell.cell.api.Cell;
import java.text.NumberFormat;
import java.util.*;

import static shticell.function.functionIdentifier.calcFunc;
import static shticell.function.functionIdentifier.isFunc;

public class CellImpl implements Cloneable, Cell {

    private final String NAN = "NaN";
    private final String UNDEFINED = "!Undefined!";

    private CellCoordinator cellCoordinator;
    private String cellId = "";
    private String originalValue = "";
    private String effectiveValue = "";
    private final TreeMap<Integer, Cell> cellByVersion= new TreeMap<>();
    private int LatestSheetVersionUpdated;
    private CellConnection connections = new CellConnection(cellId);

    public CellImpl(String cellId, CellCoordinator sheet, int currentSheetVersion){
        originalValue = effectiveValue = "";
        cellId = cellId;
        cellCoordinator = sheet;
        cellByVersion.put(currentSheetVersion,this.clone());
        LatestSheetVersionUpdated  = currentSheetVersion;
        connections = new CellConnection(cellId);
    }

    public CellImpl(Cell cell){
        cellId = cell.GetCellId();
        originalValue = cell.GetOriginalValue();
        effectiveValue = cell.GetEffectiveValue();
    }

    public boolean IsChangedInThisVersion(int version) {return cellByVersion.get(version) != null; }

    @Override
    public String GetOriginalValue() { return originalValue; }

    @Override
    public String GetEffectiveValue() { return effectiveValue; }

    @Override
    public String GetCellId() { return cellId; }

    @Override
    public int GetVersion() { return LatestSheetVersionUpdated; }

    public int GetSheetVersion() { return LatestSheetVersionUpdated; }

    public CellConnection GetConnections() { return connections; }

    public Cell GetCellBySheetVersion(int version){
        return cellByVersion.get(cellByVersion.floorKey(version));
    }

    @Override
    public CellImpl clone(){
        CellImpl clonedCellImpl = new CellImpl(cellId,cellCoordinator,LatestSheetVersionUpdated);
        clonedCellImpl.originalValue = originalValue;
        clonedCellImpl.effectiveValue = effectiveValue;
        clonedCellImpl.cellByVersion.putAll(cellByVersion);

        return clonedCellImpl;
    }

    private String addThousandsSeparator(String number) throws NumberFormatException {
        return NumberFormat.getNumberInstance(Locale.US).format(Double.parseDouble(number));
    }


    public void UpdateCell(String newOriginalValue, int sheetVersion) throws  LoopConnectionException,OperationException{
            List<CellConnection> removed = new ArrayList<>(connections.RemoveReferencesFromThisCell());
        try{
            effectiveValue = parseEffectiveValue(newOriginalValue);
            LatestSheetVersionUpdated = sheetVersion;
            originalValue = newOriginalValue;
            cellCoordinator.UpdateDependentCells(connections.GetInfluenceOn());
            cellByVersion.put(sheetVersion, this.clone());
        }
        catch (NumberOperationException e){
            LatestSheetVersionUpdated = sheetVersion;
            effectiveValue = NAN;
            cellByVersion.put(sheetVersion, this.clone());
        }
        catch(IndexOutOfBoundsException e){
            LatestSheetVersionUpdated = sheetVersion;
            effectiveValue = UNDEFINED;
            cellByVersion.put(sheetVersion, this.clone());
        }
        catch (OperationException | LoopConnectionException e) {
            connections.AddInfluenceToThisCell(removed);
            throw e;
        }
    }

    private String parseEffectiveValue(String newOriginalValue) throws NumberFormatException, LoopConnectionException, OperationException, NumberOperationException {
            String effectiveValue = isFunc(newOriginalValue) ? calcFunc(newOriginalValue,connections,cellCoordinator) : String.valueOf(newOriginalValue);
            return !effectiveValue.isEmpty() && effectiveValue.chars().allMatch(Character::isDigit)
                    ? addThousandsSeparator(String.valueOf(effectiveValue))
                    : effectiveValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CellImpl) {
            return cellId.equals(((CellImpl)obj).GetCellId());
        }

        return false;
    }
}

