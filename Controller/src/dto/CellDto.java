package dto;

import shticell.sheet.cell.api.Cell;
import shticell.sheet.coordinate.Coordinate;

import java.util.ArrayList;


public record CellDto(Coordinate coordinate, int LatestSheetVersionUpdated, String originalValue,
                      String effectiveValue, ArrayList<String> referencesFromThisCell,
                      ArrayList<String> referencesToThisCell)
{
    public CellDto(Cell cellImpl)
    {
        this(cellImpl.GetCellCoordinate(), cellImpl.GetVersion(), cellImpl.GetOriginalValue(), cellImpl.GetEffectiveValue()
                , cellImpl.GetDependsOnListOfStrings(), cellImpl.GetInfluenceOnListOfStrings());
    }

    @Override
    public String toString() {
        return  "Cell Coordinate= " + coordinate + "\n"
                + "Latest Sheet Version Updated = " + LatestSheetVersionUpdated + "\n"
                + "Original Value = " + originalValue + "\n"
                + "Effective Value = " + effectiveValue + "\n"
                + "References From This Cell = " + referencesFromThisCell +"\n"
                + "References To This Cell = " + referencesToThisCell + "\n";
    }
}