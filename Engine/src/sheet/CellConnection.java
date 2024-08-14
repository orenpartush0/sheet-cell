package sheet;

import sheet.exception.LoopConnectionException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CellConnection implements Cloneable {
    private final String cell;
    private final List<CellConnection> referencesFromThisCell ;
    private final List<CellConnection> referencesToThisCell;

    public CellConnection(String val) {
        cell = val;
        referencesFromThisCell  = new ArrayList<>();
        referencesToThisCell = new ArrayList<>();
        referencesToThisCell.add(this);
    }

    public String GetCell() {return cell;}

    @Override
    public String toString(){
        return cell;
    }


    public ArrayList<String> GetReferencesFromThisCell() {
        return referencesFromThisCell.stream()
                .map(CellConnection::toString)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<String> GetReferencesToThisCell() {
        return referencesToThisCell.stream()
                .map(CellConnection::toString)
                .collect(Collectors.toCollection(ArrayList::new));    }

    @Override
    public CellConnection clone(){
        CellConnection clonedCellConnection = new CellConnection(cell);
        for(CellConnection c : referencesFromThisCell ){
            clonedCellConnection.referencesFromThisCell .add(c.clone());
        }

        return clonedCellConnection;
    }

    public void AddReferenceFromThisCell(CellConnection neighbor) {
        referencesFromThisCell.add(neighbor);
    }

    public void RemoveReferenceToThisCell(CellConnection neighbor) {
        referencesToThisCell.remove(neighbor);
    }

    public void AddReferenceToThisCell(CellConnection neighbor) {
        referencesToThisCell.add(neighbor);
    }

    public List<CellConnection> RemoveReferencesFromThisCell() {
        List<CellConnection> toRemove = new ArrayList<>(referencesFromThisCell);
        referencesFromThisCell.clear();
        toRemove.forEach(cellConnection -> cellConnection.RemoveReferenceToThisCell(this));

        return toRemove;
    }

    public void AddReferencesToThisCell(List<CellConnection> neighbors) {
        referencesToThisCell.addAll(neighbors);
        neighbors.forEach(cellConnection -> cellConnection.AddReferenceToThisCell(this));
    }

    public static void HasPath(CellConnection start, CellConnection end) throws LoopConnectionException {
        Set<CellConnection> visited = new HashSet<>();
        if(hasPathDFS(start, end, visited)){
            throw new LoopConnectionException("this function contain Ref that will cause a Loop!");
        }
    }


    private static boolean hasPathDFS(CellConnection current, CellConnection end, Set<CellConnection> visited) {
        if (current == end) {
            return true;
        }

        visited.add(current);

        for (CellConnection neighbor : current.referencesFromThisCell) {
            if (!visited.contains(neighbor)) {
                if (hasPathDFS(neighbor, end, visited)) {
                    return true;
                }
            }
        }

        return false;
    }
}
