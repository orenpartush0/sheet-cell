package shticell.sheet.coordinate;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

public record Coordinate(int row, int col) implements Serializable,Comparable<Coordinate> {


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


    public static String getColumnLabel(int colIndex) {
        StringBuilder columnLabel = new StringBuilder();
        while (colIndex > 0) {
            colIndex--;
            columnLabel.insert(0, (char) ('A' + (colIndex % 26)));
            colIndex /= 26;
        }
        return columnLabel.toString();
    }

    @Override
    public String toString() {
        return getColumnLabel(col) + Integer.toString(row);
    }

    @Override
    public int compareTo(Coordinate o) {
        if(o != null) {
            int rowComparison = Integer.compare(this.row, ((Coordinate) o).row);
            if (rowComparison != 0) {
                return rowComparison;
            }
            return Integer.compare(this.col, ((Coordinate) o).col);
        }

        return -1;
    }
}
