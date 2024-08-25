package shticell.sheet.api;

import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.exception.LoopConnectionException;

public interface SheetToXML {
    void UpdateCellByCoordinateWithOutVersionUpdate(Coordinate coordinate, String newValue) throws LoopConnectionException;
}
