package sheet;

import sheet.Exceptions.LoopConnectionException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CellConnection implements Cloneable {
    String cell;
    List<CellConnection> connections;

    public CellConnection(String val) {
        this.cell = val;
        this.connections = new ArrayList<>();
    }

    public CellConnection clone(){
        CellConnection clonedCellConnection = new CellConnection(cell);
        for(CellConnection c : this.connections){
            clonedCellConnection.connections.add(c.clone());
        }

        return clonedCellConnection;
    }

    public void addNeighbor(CellConnection neighbor) {
        this.connections.add(neighbor);
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

        for (CellConnection neighbor : current.connections) {
            if (!visited.contains(neighbor)) {
                if (hasPathDFS(neighbor, end, visited)) {
                    return true;
                }
            }
        }

        return false;
    }
}
