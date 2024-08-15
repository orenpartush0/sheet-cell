package shticell.sheet.cell.connection;

import shticell.sheet.exception.LoopConnectionException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CellConnectionImpl implements Cloneable, CellConnection {
    private final String cellId;
    private final List<CellConnection> dependsOn = new ArrayList<>();
    private final List<CellConnection> influenceOn = new ArrayList<>();

    public CellConnectionImpl(String val) {
        cellId = val;
        influenceOn.add(this);
    }

    public String GetCellID() {return cellId;}

    @Override
    public String toString(){
        return cellId;
    }


    public ArrayList<String> GetDependsOnListOfStrings() {
        return dependsOn.stream()
                .map(CellConnection::toString)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<CellConnection> GetDependsOn() {return dependsOn;}

    @Override
    public List<CellConnection> GetInfluenceOn() { return influenceOn; }

    public ArrayList<String> GetInfluenceOnListOfStrings() {
        return influenceOn.stream()
                .map(CellConnection::toString)
                .collect(Collectors.toCollection(ArrayList::new));    }

    @Override
    public CellConnectionImpl clone(){
        CellConnectionImpl clonedCellConnectionImpl = new CellConnectionImpl(cellId);
        clonedCellConnectionImpl.dependsOn.addAll(dependsOn);

        return clonedCellConnectionImpl;
    }

    @Override
    public void AddReferenceFromThisCell(CellConnection neighbor) {
        dependsOn.add(neighbor);
    }

    @Override
    public void RemoveReferenceToThisCell(CellConnection neighbor) {
        influenceOn.remove(neighbor);
    }

    @Override
    public void AddReferenceToThisCell(CellConnection neighbor) { influenceOn.add(neighbor);}

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

    public void HasPath(CellConnection end) throws LoopConnectionException {
        Set<CellConnection> visited = new HashSet<>();
        if(hasPathDFS(this, end, visited)){
            throw new LoopConnectionException("this function contain Ref that will cause a Loop!");
        }
    }

    private boolean hasPathDFS(CellConnection current, CellConnection end, Set<CellConnection> visited) {
        if (current.equals(end)) {
            return true;
        }

        visited.add(current);

        return current.GetDependsOn().stream()
                .filter(neighbor -> !visited.contains(neighbor))
                .anyMatch(neighbor -> hasPathDFS(neighbor, end, visited));
    }

}
