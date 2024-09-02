package dto;

import shticell.sheet.coordinate.Coordinate;

public record RangeDto(String rangeName, Coordinate startCell, Coordinate endCell) {
}
