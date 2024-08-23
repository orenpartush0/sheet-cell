package shticell.sheet.coordinate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public interface CoordinateFactory {

    static Map<String,Coordinate> coordinateCache = new HashMap<>();

    static Coordinate getCoordinate(int row, int col) {
        String key = row + ":" + col;
        return Optional.ofNullable(coordinateCache.get(key)).orElseGet(() ->{
            coordinateCache.put(key,new Coordinate(row,col));
            return coordinateCache.get(key);
        });
    }

    static Coordinate getCoordinate(String square) {
        int col = square.substring(0,2).toUpperCase().charAt(0) - 'A';
        int row = Integer.parseInt(square.substring(1));
        return getCoordinate(row,col);
    }
}
