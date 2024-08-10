package sheet;
import Interfaces.CellCoordinator;
import Operation.Exceptions.OperationException;
import Operation.Operation;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import Operation.OperationImpl;
import sheet.Exceptions.LoopConnectionException;

public class Cell implements Cloneable {
    private final CellCoordinator cellCoordinator;
    private final String CELL_ID;
    private String originalValue;
    private String effectiveValue;
    ArrayList<Cell> cellVersions = new ArrayList<>();

    Cell(String cellId, CellCoordinator sheet){
        originalValue = effectiveValue = "";
        CELL_ID = cellId;
        this.cellCoordinator = sheet;
    }

    public String GetCellId() { return CELL_ID; }

    @Override
    public Cell clone(){
        Cell clonedCell = new Cell(CELL_ID, cellCoordinator);
        clonedCell.originalValue = originalValue;
        clonedCell.effectiveValue = effectiveValue;

        return clonedCell;
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
            Operation operation = new OperationImpl(cellCoordinator,CELL_ID);

            for(String arg : funcAndArgs){
                arg = calcFunc(arg);
            }

            return operation.eval(funcAndArgs.toArray(new String[0]));
        }
        else{
            return func;
        }
    }

    public String GetOriginalValue() { return originalValue; }

    public String GetEffectiveValue() { return effectiveValue; }

    public void UpdateCell(String newOriginalValue ) throws OperationException, LoopConnectionException {
        try{
            originalValue = addThousandsSeparator(String.valueOf(newOriginalValue));
        }
        catch (NumberFormatException e){
            effectiveValue = isFunc(newOriginalValue) ? calcFunc(newOriginalValue) : newOriginalValue;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Cell at square ").append(CELL_ID).append("\n");
        builder.append("-----\n");
        builder.append("Original Value: ").append(originalValue).append("\n");
        builder.append("Effective Value: ").append(effectiveValue).append("\n");
        builder.append("--------\n");

        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Cell) {
            return CELL_ID.equals(((Cell)obj).GetCellId());
        }

        return false;
    }
}

