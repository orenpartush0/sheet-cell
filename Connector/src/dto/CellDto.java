package dto;

import shticell.sheet.cell.api.Cell;
import shticell.sheet.coordinate.Coordinate;

import java.util.ArrayList;


public record CellDto(Coordinate coordinate, int LatestSheetVersionUpdated, String originalValue,
                      EffectiveValueDto effectiveValue, ArrayList<Coordinate> dependsOn,
                      ArrayList<Coordinate> influenceOn)
{
    public CellDto(Cell cellImpl)
    {
        this(cellImpl.GetCellCoordinate(), cellImpl.GetVersion(), cellImpl.GetOriginalValue()
                , new EffectiveValueDto(cellImpl.GetEffectiveValue())
                , cellImpl.GetDependsOnCoordinates(), cellImpl.GetInfluenceOnCoordinates());
    }
}