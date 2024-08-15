package dto;

import shticell.sheet.cell.api.Cell;

import java.util.ArrayList;


public record CellDto(String cellId,int LatestSheetVersionUpdated,String originalValue,
                      String effectiveValue,ArrayList<String> referencesFromThisCell,
                      ArrayList<String> referencesToThisCell)
{
    public CellDto(Cell cellImpl)
    {
        this(cellImpl.GetCellId(), cellImpl.GetVersion(), cellImpl.GetOriginalValue(), cellImpl.GetEffectiveValue()
                , cellImpl.GetConnections().GetDependsOnListOfStrings(), cellImpl.GetConnections().GetInfluenceOnListOfStrings());
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