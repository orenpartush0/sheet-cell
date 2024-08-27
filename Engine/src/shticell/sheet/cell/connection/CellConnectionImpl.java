package shticell.sheet.cell.connection;

import shticell.sheet.cell.api.Cell;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.exception.LoopConnectionException;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class CellConnectionImpl implements Cloneable, CellConnection,CanRemoveFromDependsOn, Serializable {
    private final Coordinate coordinate;
    private final List<CellConnection> dependsOn = new ArrayList<>();
    private final List<CellConnection> influenceOn = new ArrayList<>();

    public CellConnectionImpl(Coordinate _coordinate) {
        coordinate = _coordinate;
    }

    public Coordinate GetCellCoordinate() {return coordinate;}

    public ArrayList<String> GetDependsOnListOfStrings() {
        return dependsOn.stream()
                .map(CellConnection::GetCellCoordinate)
                .map(Coordinate::toString)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<CellConnection> GetDependsOn() {return dependsOn;}

    @Override
    public List<CellConnection> GetInfluenceOn(){ return influenceOn; }


    @Override
    public List<CellConnection> GetSortedInfluenceOn() throws LoopConnectionException { return topologicalSort();}

    public ArrayList<String> GetInfluenceOnListOfStrings() {
        return influenceOn.stream()
                .map(CellConnection::GetCellCoordinate)
                .map(Coordinate::toString)
                .collect(Collectors.toCollection(ArrayList::new));    }

    @Override
    public CellConnectionImpl clone(){
        CellConnectionImpl clonedCellConnectionImpl = new CellConnectionImpl(coordinate);
        clonedCellConnectionImpl.dependsOn.addAll(dependsOn);

        return clonedCellConnectionImpl;
    }

    @Override
    public void AddToDependsOn(CellConnection neighbor) {
        dependsOn.add(neighbor);
    }

    @Override
    public void AddToInfluenceOn(CellConnection neighbor) { influenceOn.add(neighbor);}

    @Override
    public void RemoveFromInfluenceOn(CellConnection neighbor) {
        influenceOn.remove(neighbor);
    }

    @Override
    public void RemoveFromDependsOn(CanRemoveFromDependsOn cell){
        dependsOn.remove(cell);
    }

    public List<CellConnection> ClearDependsOn() {
        List<CellConnection> toRemove = new ArrayList<>(dependsOn);
        dependsOn.clear();
        toRemove.forEach(cellConnection -> cellConnection.RemoveFromInfluenceOn(this));

        return toRemove;
    }

    public void recoverDependsOn(List<CellConnection> backup) {
        dependsOn.addAll(backup);
        backup.forEach(cellConnection -> cellConnection.AddToInfluenceOn(this));
    }

    private boolean topologicalSortUtil(CellConnection cell, Set<CellConnection> visited,
                                        Set<CellConnection> recStack, Stack<CellConnection> stack) {
        visited.add(cell);
        recStack.add(cell);

        for (CellConnection connection : cell.GetInfluenceOn()) {

            if (!visited.contains(connection)) {
                if (topologicalSortUtil(connection, visited, recStack, stack)) {
                    return true;
                }
            } else if (recStack.contains(connection)) {
                return true;
            }
        }

        recStack.remove(cell);
        stack.push(cell);
        return false;
    }

    public List<CellConnection> topologicalSort() throws LoopConnectionException {
        Stack<CellConnection> stack = new Stack<>();
        Set<CellConnection> visited = new HashSet<>();
        Set<CellConnection> recStack = new HashSet<>();

        for (CellConnection connection : influenceOn) {
            if (!visited.contains(connection)) {
                if (topologicalSortUtil(connection, visited, recStack, stack)) {
                    throw new LoopConnectionException("This update will cause a Loop.");
                }
            }
        }

        List<CellConnection> sortedList = new ArrayList<>();
        while (!stack.isEmpty()) {
            sortedList.add(stack.pop());
        }

        return sortedList;
    }
}

