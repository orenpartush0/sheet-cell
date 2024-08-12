package sheet.Interface;

import sheet.Exception.LoopConnectionException;

import java.util.ArrayList;

public interface CellCoordinator {
    String GetCellEffectiveValue(String square);
    void SetInfluenceBetweenTwoCells(String referrerCell, String referencedCell ) throws LoopConnectionException;
    ArrayList<String> GetListOfReferencedCells(String referrerCell);
    ArrayList<String> GetListOfReferencerCells(String referrerCell);
}
