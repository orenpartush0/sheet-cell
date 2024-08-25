package shticell.sheet.coordinate;

import java.util.Objects;

public record Coordinate(int row, int col) {

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof shticell.sheet.coordinate.Coordinate) {
            return row == ((Coordinate) obj).row() && col == ((Coordinate) obj).col();
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return Character.toString(('A' + col )) + Integer.toString(row);
    }
}
