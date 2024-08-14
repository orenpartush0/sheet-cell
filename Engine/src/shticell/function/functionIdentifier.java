package shticell.function;

import shticell.operation.Exceptions.NumberOperationException;
import shticell.operation.Exceptions.OperationException;
import shticell.operation.Interface.Operation;
import shticell.operation.OperationImpl;
import shticell.cell.ties.CellConnection;
import shticell.exception.LoopConnectionException;
import shticell.cell.sheet.api.CellCoordinator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class functionIdentifier {

    public static Collection<String> decipherFunc(String funcText){
        Collection<String> funcNameAndArguments = new ArrayList<>();
        int startIndex = 1; int endIndex = 0; int inFunction = 0;

        while(true){
            inFunction = funcText.charAt(endIndex) == '{' ? inFunction + 1: inFunction;
            inFunction = funcText.charAt(endIndex) == '}' ? inFunction - 1 : inFunction;

            if (inFunction == 1) {
                if (funcText.charAt(endIndex) == ',') {
                    funcNameAndArguments.add(funcText.substring(startIndex, endIndex).replaceAll(" ", ""));
                    startIndex = endIndex + 1;
                }
            } else if (inFunction == 0) {
                funcNameAndArguments.add(funcText.substring(startIndex, endIndex).replaceAll(" ", ""));
                break;
            }

            endIndex++;
        }

        return funcNameAndArguments;
    }

    public static boolean isFunc(String func){
        return func.charAt(0) =='{' && func.charAt(func.length()-1) == '}';
    }

    public static String calcFunc(String func, CellConnection connections, CellCoordinator cellCoordinator) throws NumberFormatException, LoopConnectionException, OperationException, NumberOperationException {
        if(isFunc(func)){
            List<String> funcAndArgs = new ArrayList<>(decipherFunc(func).stream().toList());
            Operation operation = new OperationImpl(connections,cellCoordinator);

            for(int i = 0; i < funcAndArgs.size(); i++) {
                funcAndArgs.set(i, calcFunc(funcAndArgs.get(i),connections,cellCoordinator));
            }

            return operation.eval(funcAndArgs.toArray(new String[0]));
        }
        else{
            return func;
        }
    }
}
