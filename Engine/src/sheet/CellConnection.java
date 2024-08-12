package sheet;

import sheet.Exception.LoopConnectionException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CellConnection implements Cloneable {
    String cell;
    List<CellConnection> referencesFromThisCell ;
    List<CellConnection> referencesToThisCell;

    public CellConnection(String val) {
        cell = val;
        referencesFromThisCell  = new ArrayList<>();
        referencesToThisCell = new ArrayList<>();
    }

    @Override
    public String toString(){
        return cell;
    }

    public ArrayList<String> getReferencesFromThisCell() {
        return referencesFromThisCell.stream()
                .map(CellConnection::toString)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<String> getReferencesToThisCell() {
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

    public void addReferenceFromThisCell(CellConnection neighbor) {
        referencesFromThisCell.add(neighbor);
        neighbor.referencesToThisCell.add(this);
    }

    public static void hasPath(CellConnection start, CellConnection end) throws LoopConnectionException {
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
