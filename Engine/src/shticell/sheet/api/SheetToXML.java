package shticell.sheet.api;

import shticell.sheet.coordinate.Coordinate;

import java.util.List;

public interface SheetToXML {
    void UpdateCellByCoordinateWithOutVersions(Coordinate coordinate, String newValue);
    void UpdateDependentCellsForXML(List<Coordinate> coordinates);
}
