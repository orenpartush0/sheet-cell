package shticell.cell.api;

public interface Cell {
    String GetOriginalValue();
    String GetEffectiveValue();
    String GetCellId();
    int GetVersion();
}
