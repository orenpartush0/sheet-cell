package shticell.sheet.api;

import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.exception.LoopConnectionException;

import java.util.List;

public interface SheetToXML {
    void UpdateCellByCoordinateWithOutVersions(Coordinate coordinate, String newValue) throws LoopConnectionException;
    void UpdateDependentCellsForXML(List<Coordinate> coordinates);

}
