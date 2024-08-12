package sheet.Interface;

public interface HasCellData {
    String GetOriginalValue();
    String GetEffectiveValue();
    String GetCellId();
    HasCellData clone();
}
