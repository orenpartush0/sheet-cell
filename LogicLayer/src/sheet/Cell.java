package sheet;
import Interfaces.HasDataOnOtherCells;
import Operation.Operation;
import Operation.MathOperations;
import java.text.NumberFormat;
import java.util.*;

public class Cell implements Cloneable {
    private HasDataOnOtherCells hasDataOnOtherCells;
    private final String CELL_ID;
    private String originalValue;
    private String effectiveValue;
    ArrayList<String> cellsIdInfluencedByThisCell = new ArrayList<>();
    ArrayList<String> cellsIdInfluencingThisCell = new ArrayList<>();
    ArrayList<Cell> cellVersions = new ArrayList<>();

    private Dictionary <Integer, versionInfo> versionHistory= new Hashtable<>();
    int lastModify;



    private class versionInfo{
        int lastVersionModify;
        String originalValue;

        public versionInfo(){
            lastVersionModify = -1;
            originalValue = "";
        }
        public versionInfo(int lastVersionModify, String originalValue){
            this.lastVersionModify = lastVersionModify;
            this.originalValue = originalValue;
        }

        public int getLastVersionModify(){
            return lastVersionModify;
        }

        public String getOriginalValue(){
            return originalValue;
        }
    }
    Cell(String cellId,HasDataOnOtherCells sheet, int currentSheetVersion){
        originalValue = effectiveValue = "";
        CELL_ID = cellId;
        this.hasDataOnOtherCells = sheet;
        versionHistory.put(currentSheetVersion,new versionInfo());
        lastModify = currentSheetVersion;
    }

    public String GetCellId() { return CELL_ID; }

    public void setCellsInfluencingThisCell(Collection <String> cellsInfluencingThisCel){
        this.cellsIdInfluencingThisCell.addAll(cellsInfluencingThisCel);
    }

    public void setCellsInfluencedByThisCell(Collection <String> cellsInfluencedByThisCel){
        this.cellsIdInfluencedByThisCell.addAll(cellsInfluencedByThisCel);
    }

    public boolean addCellInfluencedByThisCell(String cellId){
        boolean done = false;
        if(!cellsIdInfluencedByThisCell.contains(cellId)){
            this.cellsIdInfluencedByThisCell.add(cellId);
            done = true;
        }

        return done;
    }
    public boolean addCellIdInfluencingThisCell(String cellId){
        boolean done = false;
        if(!cellsIdInfluencingThisCell.contains(cellId)){
            this.cellsIdInfluencingThisCell.add(cellId);
            done = true;
        }
        return done;
    }

    @Override
    public Cell clone(int currentSheetVersion){
        Cell clonedCell = new Cell(CELL_ID,hasDataOnOtherCells,currentSheetVersion);
        clonedCell.originalValue = originalValue;
        clonedCell.effectiveValue = effectiveValue;
        clonedCell.setCellsInfluencedByThisCell(cellsIdInfluencedByThisCell);
        clonedCell.setCellsInfluencingThisCell(cellsIdInfluencingThisCell);

        return clonedCell;
    }

    private String addThousandsSeparator(String number) throws NumberFormatException {
        return NumberFormat.getNumberInstance(Locale.US).format(Double.parseDouble(number));
    }

    private boolean isFunc(String func){
        return func.charAt(0) =='{' && func.charAt(func.length()-1) == '}';
    }

    private String calcFunc(String func){
        if(isFunc(func)){
            int index = func.indexOf(",");
            String subFunc = func.substring(1, index);
            Operation operation = MathOperations.valueOf(func.substring(1, index));
            String[] args = func.substring(1, index+1).split(",");
            for(String arg : args){
                arg = calcFunc(arg);
            }

            return operation.eval(args);
        }
        else{
            return func;
        }
    }

    public String GetOriginalValue() { return originalValue; }

    public String GetEffectiveValue() { return effectiveValue; }

    public void UpdateCell(String newOriginalValue,int currentSheetVersion ){
        originalValue = newOriginalValue;
        effectiveValue = calcFunc(newOriginalValue);
        versionHistory.put(currentSheetVersion,new versionInfo(lastModify,newOriginalValue));
        lastModify = currentSheetVersion;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Cell at square ").append(CELL_ID).append("\n");
        builder.append("-----\n");
        builder.append("Original Value: ").append(originalValue).append("\n");
        builder.append("Effective Value: ").append(effectiveValue).append("\n");
        builder.append("--------\n");
        builder.append("Cells Influenced by This Cell:\n");
        builder.append("------------------------------\n");
        for (String cell : cellsIdInfluencedByThisCell) {
            builder.append("cell at square = ").append(cell).append("\n");
        }
        builder.append("Cells Influencing This Cell:\n");
        builder.append("------------------------------\n");
        for (String cell : cellsIdInfluencingThisCell) {
            builder.append("cell at square = ").append(cell).append("\n");
        }

        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Cell) {
            return CELL_ID.equals(((Cell)obj).GetCellId());
        }

        return false;
    }

    public String getCellOriginalValue(int version){
        versionInfo info = versionHistory.get(version);
        if(info == null){
            return retriveDate(versionHistory.get(lastModify),version);
        }
        return info.originalValue;
    }

    private String retriveDate(versionInfo info, int version){
        while (info.getLastVersionModify() > version){
            info = versionHistory.get(info.getLastVersionModify());
        }
        return info.getOriginalValue();
    }


}

