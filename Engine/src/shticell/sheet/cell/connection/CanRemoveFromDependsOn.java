package shticell.sheet.cell.connection;

import shticell.sheet.cell.api.Cell;

public interface CanRemoveFromDependsOn {
    void RemoveFromDependsOn(CanRemoveFromDependsOn cell);
}
