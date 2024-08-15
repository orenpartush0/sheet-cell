package shticell.sheet.coordinate;

import java.util.Objects;

public class CoordinateImpl implements Coordinate {
    private int row;
    private int col;

    public CoordinateImpl(int row, int col) {
        this.row = row;
        this.col = col;
    }
    @Override
    public int getRow() {
        return row;
    }
    @Override
    public void setRow(int row) {
        this.row = row;
    }
    @Override
    public int getCol() {
        return col;
    }
    @Override
    public void setCol(int col) {
        this.col = col;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Coordinate) {
            return row == ((Coordinate) obj).getRow() && col == ((Coordinate) obj).getCol();
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}
