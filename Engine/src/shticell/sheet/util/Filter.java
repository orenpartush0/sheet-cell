package shticell.sheet.util;

import shticell.sheet.api.Sheet;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.coordinate.CoordinateFactory;
import shticell.sheet.range.Range;

import java.util.*;
import java.util.stream.Collectors;

public class Filter {
    private final List<Coordinate> coordinatesInRange;
    private int col;
    private Sheet sheet;
    private final List<String> filterdValues = new ArrayList<>();

    public Filter(String col, Coordinate upperLeft, Coordinate lowerRight,Sheet sheet) {
        this.col = (int)col.toUpperCase().charAt(0)- (int)'A'+1;
        Range range = new Range("", upperLeft, lowerRight);
        coordinatesInRange = range.getRangeCellsCoordinate();
        this.sheet = sheet;
    }

    public List<String> getValuesInColumn() {
        List<String>relevantValues = new ArrayList<>();
        coordinatesInRange.forEach(((coordinate) -> {
            if(coordinate.col() == this.col && !Objects.equals(sheet.GetCell(coordinate).GetOriginalValue(),"")) {
                relevantValues.add(sheet.GetCell(coordinate).GetEffectiveValue().toString());
            }

        }));

        return relevantValues;
    }

    public void Setcol(String col){
        this.col = this.col = (int)col.toUpperCase().charAt(0)- (int)'A'+1;
    }

    public void AddFilterValues(List<String> vals) {
        filterdValues.addAll(vals);
    }

    private List<Coordinate> getCellsFromRangeInRow(int row) {
        List<Coordinate> relevantCoordinates = new ArrayList<>();
        coordinatesInRange.forEach((coordinate) -> {
            if(coordinate.row() == row) {
                relevantCoordinates.add(coordinate);
            }
        });
        return relevantCoordinates;
    }

    private List<Coordinate> getCellsFromRangeInColumn(int col) {
        List<Coordinate> relevantCoordinates = new ArrayList<>();
        coordinatesInRange.forEach((coordinate) -> {
            if(coordinate.col() == col) {
                relevantCoordinates.add(coordinate);
            }
        });
        return relevantCoordinates;
    }

    public List<Coordinate> getFilterdCordinates() {
        List <Coordinate> relevantRows = new ArrayList<>();
        getCellsFromRangeInColumn(this.col).forEach((coordinate) -> {
            if(filterdValues.stream().anyMatch((val)-> {
                return sheet.GetCell(coordinate).GetEffectiveValue().toString().equals(val);
            })){
                relevantRows.add(coordinate);
        }});
        return new ArrayList<>(relevantRows.stream()
                .flatMap(coordinate -> getCellsFromRangeInRow(coordinate.row()).stream())
                .collect(Collectors.toList()));
    }
}

