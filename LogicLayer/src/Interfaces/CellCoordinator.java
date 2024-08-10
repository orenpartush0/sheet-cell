package Interfaces;

import sheet.Exceptions.LoopConnectionException;

public interface CellCoordinator {
    String GetCellEffectiveValue(String square);
    void SetInfluenceBetweenTwoCells(String referrerCell, String referencedCell ) throws LoopConnectionException;
}
