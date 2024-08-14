package sheet.Interface;

public interface HasCellData {
    String GetOriginalValue();
    String GetEffectiveValue();
    String GetCellId();
    int LatestSheetVersionUpdated();
    HasCellData clone();
}
