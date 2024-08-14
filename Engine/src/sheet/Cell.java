package sheet;

import operation.Exceptions.NumberOperationException;
import operation.Exceptions.OperationException;
import sheet.Interface.CellCoordinator;
import sheet.exception.LoopConnectionException;
import sheet.Interface.HasCellData;
import java.text.NumberFormat;
import java.util.*;

import static function.functionIdentifier.calcFunc;
import static function.functionIdentifier.isFunc;

public class Cell implements Cloneable, HasCellData {

    private final String NAN = "NaN";
    private final String UNDEFINED = "!Undefined!";

    private CellCoordinator cellCoordinator;
    private String cellId = "";
    private String originalValue = "";
    private String effectiveValue = "";
    private final TreeMap<Integer, HasCellData> cellByVersion= new TreeMap<>();
    private int LatestSheetVersionUpdated;
    private CellConnection connections = new CellConnection(cellId);

    public Cell(){}

    public Cell(String cellId, CellCoordinator sheet, int currentSheetVersion){
        originalValue = effectiveValue = "";
        this.cellId = cellId;
        this.cellCoordinator = sheet;
        cellByVersion.put(currentSheetVersion,this.clone());
        LatestSheetVersionUpdated  = currentSheetVersion;
        connections = new CellConnection(cellId);
    }

    public Cell(HasCellData hasCellData){
        cellId = hasCellData.GetCellId();
        originalValue = hasCellData.GetOriginalValue();
        effectiveValue = hasCellData.GetEffectiveValue();
    }

    public boolean IsChangedInThisVersion(int version) {return cellByVersion.get(version) != null; }

    @Override
    public String GetOriginalValue() { return originalValue; }

    @Override
    public String GetEffectiveValue() { return effectiveValue; }

    @Override
    public String GetCellId() { return cellId; }

    @Override
    public int LatestSheetVersionUpdated() { return LatestSheetVersionUpdated; }

    public int GetSheetVersion() { return LatestSheetVersionUpdated; }

    public CellConnection GetConnections() { return connections; }

    public HasCellData GetCellBySheetVersion(int version){
        return cellByVersion.get(cellByVersion.floorKey(version));
    }

    @Override
    public Cell clone(){
        Cell clonedCell = new Cell();
        clonedCell.cellId = cellId;
        clonedCell.cellCoordinator = cellCoordinator;
        clonedCell.LatestSheetVersionUpdated = LatestSheetVersionUpdated;
        clonedCell.originalValue = originalValue;
        clonedCell.effectiveValue = effectiveValue;
        clonedCell.cellByVersion.putAll(cellByVersion);

        return clonedCell;
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
            cellCoordinator.UpdateDependentCells(connections.GetReferencesToThisCell());
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
            connections.AddReferencesToThisCell(removed);
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
        if (obj instanceof Cell) {
            return cellId.equals(((Cell)obj).GetCellId());
        }

        return false;
    }
}

