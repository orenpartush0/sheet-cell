package shticell.sheet.cell.api;

import shticell.sheet.exception.LoopConnectionException;

public interface CellToXML {
    void UpdateCellWithOutVersion(String newOriginalValue) throws LoopConnectionException;
}
