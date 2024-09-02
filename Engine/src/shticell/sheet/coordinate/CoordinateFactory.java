package shticell.sheet.coordinate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public interface CoordinateFactory extends Serializable {

    static Map<String,Coordinate> coordinateCache = new HashMap<>();

    static Coordinate getCoordinate(int row, int col) {
        String key = col + ":" + row;
        return Optional.ofNullable(coordinateCache.get(key)).orElseGet(() ->{
            coordinateCache.put(key,new Coordinate(row,col));
            return coordinateCache.get(key);
        });
    }

    static Coordinate getCoordinate(String square) {
        String columnPart = square.replaceAll("[^A-Za-z]", "").toUpperCase();
        int row = Integer.parseInt(square.replaceAll("[^0-9]", ""));
        int col = 0;

        for (int i = 0; i < columnPart.length(); i++) {
            col = col * 26 + (columnPart.charAt(i) - 'A' + 1);
        }

        return getCoordinate(row, col);
    }


    static boolean isValidCoordinate(String coordinate) {
        String pattern = "^[a-zA-Z]+\\d+$";
        return coordinate.matches(pattern);
    }
}
