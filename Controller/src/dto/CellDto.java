package dto;

import shticell.cell.impl.CellImpl;
import java.util.ArrayList;


public record CellDto(String cellId,int LatestSheetVersionUpdated,String originalValue,
                      String effectiveValue,ArrayList<String> referencesFromThisCell,
                      ArrayList<String> referencesToThisCell)
{
    public CellDto(CellImpl cellImpl)
    {
        this(cellImpl.GetCellId(), cellImpl.GetSheetVersion(), cellImpl.GetOriginalValue(), cellImpl.GetEffectiveValue()
                , cellImpl.GetConnections().GetDependsOn(), cellImpl.GetConnections().GetInfluenceOn());
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