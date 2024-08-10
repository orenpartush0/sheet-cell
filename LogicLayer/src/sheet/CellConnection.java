package sheet;

import sheet.Exceptions.LoopConnectionException;
import java.util.*;

public class CellConnection {
    String cell;
    List<CellConnection> connections;

    public CellConnection(String val) {
        this.cell = val;
        this.connections = new ArrayList<>();
    }

    public void addNeighbor(CellConnection neighbor) {
        this.connections.add(neighbor);
    }

    public static void hasPath(CellConnection start, CellConnection end) throws LoopConnectionException {
        Set<CellConnection> visited = new HashSet<>();
        if(hasPathDFS(start, end, visited)){
            String messageTemplate = "The cell {startCell} already has a reference to {endCell}";
            String formattedMessage = messageTemplate
                    .replace("{startCell}", start.cell)
                    .replace("{endCell}", end.cell);
            throw new LoopConnectionException(formattedMessage);
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
