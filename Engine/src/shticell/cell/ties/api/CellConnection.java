package shticell.cell.ties.api;

import shticell.exception.LoopConnectionException;

import java.util.*;

public interface CellConnection{
    String GetCellID();

    String toString();

    List<CellConnection> GetDependsOn();

    List<CellConnection> GetInfluenceOn();

    void AddReferenceFromThisCell(CellConnection neighbor);

    void RemoveReferenceToThisCell(CellConnection neighbor);

    void AddReferenceToThisCell(CellConnection neighbor);

    List<CellConnection> RemoveReferencesFromThisCell();

    void AddInfluenceToThisCell(List<CellConnection> neighbors);

    void HasPath(CellConnection end) throws LoopConnectionException;

    ArrayList<String> GetDependsOnListOfStrings();

    ArrayList<String> GetInfluenceOnListOfStrings();

}
