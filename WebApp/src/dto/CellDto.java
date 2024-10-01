package dto;

import shticell.sheet.cell.api.Cell;
import shticell.sheet.coordinate.Coordinate;

import java.util.ArrayList;


public record CellDto(Coordinate coordinate, int LatestSheetVersionUpdated, String originalValue,
                      String effectiveValue, ArrayList<Coordinate> dependsOn,
                      ArrayList<Coordinate> influenceOn,String userName)
{
    public CellDto(Cell cellImpl)
    {
        this(cellImpl.GetCellCoordinate(), cellImpl.GetVersion(), cellImpl.GetOriginalValue()
                , cellImpl.GetEffectiveValue().getValue().toString()
                , cellImpl.GetDependsOnCoordinates(), cellImpl.GetInfluenceOnCoordinates(), cellImpl.GetUserName());
    }
}