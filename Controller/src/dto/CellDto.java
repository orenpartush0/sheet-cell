package dto;

import sheet.Cell;
import java.util.ArrayList;


public record CellDto(String cellId,int LatestSheetVersionUpdated,String originalValue,
                      String effectiveValue,ArrayList<String> referencesFromThisCell,
                      ArrayList<String> referencesToThisCell)
{
    public CellDto(Cell cell)
    {
        this(cell.GetCellId(),cell.GetSheetVersion(),cell.GetOriginalValue(),cell.GetEffectiveValue()
                ,cell.GetConnection().GetReferencesFromThisCell(), cell.GetConnection().GetReferencesToThisCell());
    }

    @Override
    public String toString() {
        return  "cellId= " + cellId + "\n"
                + "Latest Sheet Version Updated = " + LatestSheetVersionUpdated + "\n"
                + "Original Value = " + originalValue + "\n"
                + "Effective Value = " + effectiveValue + "\n"
                + "References From This Cell = " + referencesFromThisCell +"\n"
                + "References To This Cell = " + referencesToThisCell + "\n";
    }
}