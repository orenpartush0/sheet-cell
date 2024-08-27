package shticell.sheet.cell.connection;

import shticell.sheet.cell.api.Cell;

import java.io.Serializable;

public interface CanRemoveFromDependsOn extends CellConnection, Serializable {
    void RemoveFromDependsOn(CanRemoveFromDependsOn cell);
}
