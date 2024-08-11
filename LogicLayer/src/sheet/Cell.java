package sheet;

import Interfaces.CellCoordinator;
import Interfaces.Operation.Exceptions.OperationException;
import Interfaces.Operation.Operation;
import Interfaces.Operation.OperationImpl;
import sheet.Exceptions.LoopConnectionException;
import sheet.Exceptions.VersionControlException;
import sheet.Interfaces.HasCellData;

import java.text.NumberFormat;
import java.util.*;

public class Cell implements Cloneable, HasCellData {
    private final CellCoordinator cellCoordinator;
    private final String cellId;
    private String originalValue;
    private String effectiveValue;
    private final TreeMap<Integer, HasCellData> cellByVersion= new TreeMap<>();
    private final int sheetVersion;

    Cell(String cellId, CellCoordinator sheet, int currentSheetVersion){
        originalValue = effectiveValue = "";
        this.cellId = cellId;
        this.cellCoordinator = sheet;
        cellByVersion.put(currentSheetVersion,this.clone());
        sheetVersion  = currentSheetVersion;
    }

    public String GetOriginalValue() { return originalValue; }

    public String GetEffectiveValue() { return effectiveValue; }

    public String GetCellId() { return cellId; }

    @Override
    public Cell clone(){
        Cell clonedCell = new Cell(cellId, cellCoordinator,sheetVersion);
        clonedCell.originalValue = originalValue;
        clonedCell.effectiveValue = effectiveValue;
        for(Map.Entry<Integer, HasCellData> entry: cellByVersion.entrySet()){
            clonedCell.cellByVersion.put(entry.getKey(), entry.getValue().clone());
        }

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

    public boolean IsModifiedInThisVersion(int version){
        return cellByVersion.containsKey(version);
    }

    public String GetCellEffectiveValueBySheetVersion(int version) throws VersionControlException {
        if (cellByVersion.get(version).GetEffectiveValue() != null){
            return cellByVersion.get(version).GetEffectiveValue();
        }
        else{
            return cellByVersion.get(cellByVersion.lastKey()).GetEffectiveValue();
        }
    }

    private String addThousandsSeparator(String number) throws NumberFormatException {
        return NumberFormat.getNumberInstance(Locale.US).format(Double.parseDouble(number));
    }

    private Collection<String> decipherFunc(String funcText){
        Collection<String> funcNameAndArguments = new ArrayList<>();
        int startIndex = 0; int endIndex = 0; int inFunction = 0;

        do {
            inFunction = funcText.charAt(endIndex) == '{' ? ++inFunction : inFunction;
            inFunction = funcText.charAt(endIndex) == '}' ? --inFunction : inFunction;

            if(inFunction == 1)
            {
                if(endIndex == funcText.length() - 2 || funcText.charAt(endIndex) == ','){
                    funcNameAndArguments.add(funcText.substring(startIndex, endIndex));
                    startIndex = endIndex + 1;
                }
            }

            endIndex++;
        }while(startIndex < funcText.length());
        
        return funcNameAndArguments;
    }

    private boolean isFunc(String func){
        return func.charAt(0) =='{' && func.charAt(func.length()-1) == '}';
    }

    private String calcFunc(String func) throws OperationException, LoopConnectionException {
        if(isFunc(func)){
            Collection<String> funcAndArgs = decipherFunc(func).stream().toList();
            Operation operation = new OperationImpl(cellCoordinator, cellId);

            for(String arg : funcAndArgs){
                arg = calcFunc(arg);
            }

            return operation.eval(funcAndArgs.toArray(new String[0]));
        }
        else{
            return func;
        }
    }

    public void UpdateCell(String newOriginalValue,int sheetVersion) throws OperationException, LoopConnectionException {
        try {
            this.originalValue = addThousandsSeparator(String.valueOf(newOriginalValue));
        } catch (NumberFormatException e) {
            this.effectiveValue = isFunc(newOriginalValue) ? calcFunc(newOriginalValue) : newOriginalValue;
        }
        cellByVersion.put(sheetVersion + 1, this.clone());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Cell at square ").append(cellId).append("\n");
        builder.append("-----\n");
        builder.append("Original Value: ").append(originalValue).append("\n");
        builder.append("Effective Value: ").append(effectiveValue).append("\n");
        builder.append("--------\n");

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

