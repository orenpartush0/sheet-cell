package dto;

import shticell.sheet.cell.api.Cell;
import shticell.sheet.coordinate.Coordinate;

import java.util.ArrayList;


public record CellDto(Coordinate coordinate, int LatestSheetVersionUpdated, String originalValue,
                      EffectiveValueDto effectiveValue, ArrayList<String> referencesFromThisCell,
                      ArrayList<String> referencesToThisCell)
{
    public CellDto(Cell cellImpl)
    {
        this(cellImpl.GetCellCoordinate(), cellImpl.GetVersion(), cellImpl.GetOriginalValue()
                , new EffectiveValueDto(cellImpl.GetEffectiveValue())
                , cellImpl.GetDependsOnListOfStrings(), cellImpl.GetInfluenceOnListOfStrings());
    }
}