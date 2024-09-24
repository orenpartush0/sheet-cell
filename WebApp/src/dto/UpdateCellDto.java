package dto;

import shticell.sheet.coordinate.Coordinate;

public record UpdateCellDto(Coordinate coordinate, String newValue) { }
