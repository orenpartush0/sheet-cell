package sheet;

import sheet.Interface.CellCoordinator;
import Operation.Exceptions.OperationException;
import Operation.Interface.Operation;
import Operation.OperationImpl;
import sheet.Exception.LoopConnectionException;
import sheet.Exception.VersionControlException;
import sheet.Interface.HasCellData;
import java.text.NumberFormat;
import java.util.*;

public class Cell implements Cloneable, HasCellData {
    private CellCoordinator cellCoordinator;
    private String cellId = "";
    private String originalValue = "";
    private String effectiveValue = "";
    private final TreeMap<Integer, HasCellData> cellByVersion= new TreeMap<>();
    private int sheetVersion;

    public Cell(){}

    public Cell(String cellId, CellCoordinator sheet, int currentSheetVersion){
        originalValue = effectiveValue = "";
        this.cellId = cellId;
        this.cellCoordinator = sheet;
        cellByVersion.put(currentSheetVersion,this.clone());
        sheetVersion  = currentSheetVersion;
    }

    public String GetOriginalValue() { return originalValue; }

    public String GetEffectiveValue() { return effectiveValue; }

    public String GetCellId() { return cellId; }

    public int GetSheetVersion() { return sheetVersion; }

    public String GetCellEffectiveValueBySheetVersion(int version) throws VersionControlException {
        return cellByVersion.get(cellByVersion.floorKey(version)).GetEffectiveValue();
    }

    @Override
    public Cell clone(){
        Cell clonedCell = new Cell();
        clonedCell.cellId = cellId;
        clonedCell.cellCoordinator = cellCoordinator;
        clonedCell.sheetVersion = sheetVersion;
        clonedCell.originalValue = originalValue;
        clonedCell.effectiveValue = effectiveValue;
        clonedCell.cellByVersion.putAll(cellByVersion);

        return clonedCell;
    }

    public String GetCellOriginalValueBySheetVersion(int version) throws VersionControlException {
        if (cellByVersion.get(version) != null) {
            return cellByVersion.get(version).GetOriginalValue();
        }
        else{
            return cellByVersion.get(cellByVersion.lastKey()).GetOriginalValue();
        }
    }

    private String addThousandsSeparator(String number) throws NumberFormatException {
        return NumberFormat.getNumberInstance(Locale.US).format(Double.parseDouble(number));
    }

    private Collection<String> decipherFunc(String funcText){
        Collection<String> funcNameAndArguments = new ArrayList<>();
        int startIndex = 1; int endIndex = 0; int inFunction = 0;

        while(true){
            inFunction = funcText.charAt(endIndex) == '{' ? ++inFunction : inFunction;
            inFunction = funcText.charAt(endIndex) == '}' ? --inFunction : inFunction;

            if (inFunction == 1) {
                if (funcText.charAt(endIndex) == ',') {
                    funcNameAndArguments.add(funcText.substring(startIndex, endIndex).replaceAll(" ", ""));
                    startIndex = endIndex + 1;
                }
            } else if (inFunction == 0) {
                funcNameAndArguments.add(funcText.substring(startIndex, endIndex).replaceAll(" ", ""));
                break;
            }

            endIndex++;
        }
        
        return funcNameAndArguments;
    }

    private boolean isFunc(String func){
        return func.charAt(0) =='{' && func.charAt(func.length()-1) == '}';
    }

    private String calcFunc(String func) throws OperationException, LoopConnectionException {
        if(isFunc(func)){
            List<String> funcAndArgs = new ArrayList<>(decipherFunc(func).stream().toList());
            Operation operation = new OperationImpl(cellCoordinator, cellId);

            for(int i = 0; i < funcAndArgs.size(); i++) {
                funcAndArgs.set(i, calcFunc(funcAndArgs.get(i)));
            }

            return operation.eval(funcAndArgs.toArray(new String[0]));
        }
        else{
            return func;
        }
    }

    public void UpdateCell(String newOriginalValue, int sheetVersion) throws OperationException, LoopConnectionException {
        originalValue = newOriginalValue;
        effectiveValue = parseEffectiveValue(newOriginalValue);
        cellByVersion.put(sheetVersion, this.clone());
    }

    private String parseEffectiveValue(String newOriginalValue) throws OperationException, LoopConnectionException {
        try {
            return addThousandsSeparator(String.valueOf(newOriginalValue));
        } catch (NumberFormatException e) {
            return isFunc(newOriginalValue) ? calcFunc(newOriginalValue) : String.valueOf(newOriginalValue);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Cell at square ").append(cellId).append("\n");
        builder.append("-----\n");
        builder.append("Original Value: ").append(originalValue).append("\n");
        builder.append("Effective Value: ").append(effectiveValue).append("\n");
        builder.append("List of cells influence by this cell:\n");
        builder.append(cellCoordinator.GetListOfReferencedCells(cellId));
        builder.append("\nList of cells influence this cell:\n");
        builder.append(cellCoordinator.GetListOfReferencerCells(cellId));
        builder.append("\n--------\n");

        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Cell) {
            return cellId.equals(((Cell)obj).GetCellId());
        }

        return false;
    }
}

