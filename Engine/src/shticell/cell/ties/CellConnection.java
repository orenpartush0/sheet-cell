package shticell.cell.ties;

import shticell.exception.LoopConnectionException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CellConnection implements Cloneable {
    private final String cellId;
    private final List<CellConnection> dependsOn = new ArrayList<>();
    private final List<CellConnection> influenceOn = new ArrayList<>();

    public CellConnection(String val) {
        cellId = val;
        influenceOn.add(this);
    }

    public String GetCell() {return cellId;}

    @Override
    public String toString(){
        return cellId;
    }


    public ArrayList<String> GetDependsOn() {
        return dependsOn.stream()
                .map(CellConnection::toString)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<String> GetInfluenceOn() {
        return influenceOn.stream()
                .map(CellConnection::toString)
                .collect(Collectors.toCollection(ArrayList::new));    }

    @Override
    public CellConnection clone(){
        CellConnection clonedCellConnection = new CellConnection(cellId);
        clonedCellConnection.dependsOn.addAll(dependsOn);

        return clonedCellConnection;
    }

    public void AddReferenceFromThisCell(CellConnection neighbor) {
        dependsOn.add(neighbor);
    }

    public void RemoveReferenceToThisCell(CellConnection neighbor) {
        influenceOn.remove(neighbor);
    }

    public void AddReferenceToThisCell(CellConnection neighbor) {
        influenceOn.add(neighbor);
    }

    public List<CellConnection> RemoveReferencesFromThisCell() {
        List<CellConnection> toRemove = new ArrayList<>(dependsOn);
        dependsOn.clear();
        toRemove.forEach(cellConnection -> cellConnection.RemoveReferenceToThisCell(this));

        return toRemove;
    }

    public void AddInfluenceToThisCell(List<CellConnection> neighbors) {
        influenceOn.addAll(neighbors);
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

        return current.dependsOn.stream()
                .filter(neighbor -> !visited.contains(neighbor))
                .anyMatch(neighbor -> hasPathDFS(neighbor, end, visited));
    }
}
