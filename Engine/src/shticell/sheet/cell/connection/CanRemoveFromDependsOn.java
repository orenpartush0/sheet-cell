package shticell.sheet.cell.connection;

import shticell.sheet.cell.api.Cell;

public interface CanRemoveFromDependsOn extends CellConnection {
    void RemoveFromDependsOn(CanRemoveFromDependsOn cell);
}
