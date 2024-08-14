package operation;

import operation.Exceptions.NumberOperationException;
import operation.Exceptions.OperationException;
import sheet.CellConnection;
import operation.Interface.Operation;
import operation.Enums.eOperation;
import sheet.exception.LoopConnectionException;
import sheet.Interface.CellCoordinator;

import java.util.ArrayList;
import java.util.Arrays;

public class OperationImpl implements Operation {

    private final CellConnection connections;
    CellCoordinator coordinator;

    @Override
    public String eval(String... funcAndArgs) throws OperationException, LoopConnectionException, NumberFormatException, NumberOperationException {
        ArrayList<String> args = new ArrayList<>(Arrays.stream(funcAndArgs).toList());
        String operationName = args.removeFirst();
        eOperation operation = eOperation.valueOf(operationName);

        return switch (operation) {
            case eOperation.PLUS -> Plus(args);
            case eOperation.MINUS -> Minus(args);
            case eOperation.DIVIDE -> Divide(args);
            case eOperation.TIMES -> Times(args);
            case eOperation.MOD -> Mod(args);
            case eOperation.POW -> Pow(args);
            case eOperation.ABS -> Abs(args);
            case eOperation.SUB -> Sub(args);
            case eOperation.CONCAT -> Concat(args);
            case eOperation.REF -> ref(args);
        };
    }

    public OperationImpl(CellConnection _connections , CellCoordinator _coordinator)
    {
        connections = _connections;
        coordinator = _coordinator;
    }

    private String ref(ArrayList<String> args) throws OperationException, LoopConnectionException {
        if(args.size() == 1){
            CellConnection.HasPath(coordinator.GetCellConnection(args.getFirst()),connections);
            coordinator.GetCellConnection(args.getFirst()).AddReferenceToThisCell(connections);
            connections.AddReferenceFromThisCell(coordinator.GetCellConnection(args.getFirst()));
            return coordinator.GetCellEffectiveValue(args.getFirst());
        }

        throw new OperationException("Ref operation requires one argument");
    }

    public String Sub(ArrayList<String> args) throws OperationException{
        if(args.size() == 3){
            int startIndex = Integer.parseInt(args.get(1));
            int endIndex = Integer.parseInt(args.get(2));

            return args.getFirst().substring(startIndex, endIndex);
        }

        throw new OperationException("Sub operation requires three arguments");
    }

    public String Concat(ArrayList<String> args) throws OperationException {
        if(args.size() == 2){
            return args.getFirst().concat(args.get(1));
        }

        throw new OperationException("Concat operation requires two arguments");
    }

    public String Pow(ArrayList<String> args) throws OperationException,NumberFormatException {
        if (args.size() == 2) {
            double num1 = Double.parseDouble(args.get(0));
            double num2 = Double.parseDouble(args.get(1));

            return String.valueOf(Math.pow(num1, num2));
        }

        throw new OperationException("Times operation requires two arguments");
    }

    public String Mod(ArrayList<String> args) throws OperationException, NumberFormatException, NumberOperationException {
        if (args.size() == 2) {
            double num1 = Double.parseDouble(args.get(0));
            double num2 = Double.parseDouble(args.get(1));

            if (num2 == 0) {
                throw new NumberOperationException("NaN");
            }

            return String.valueOf(num1 % num2);
        }

        throw new OperationException("Mod operation requires two arguments");
    }

    public String Times(ArrayList<String> args) throws OperationException, NumberFormatException {
        if (args.size() == 2) {
            double num1 = Double.parseDouble(args.get(0));
            double num2 = Double.parseDouble(args.get(1));

            return String.valueOf(num1 * num2);
        }

        throw new OperationException("Times operation requires two arguments");
    }

    public String Divide(ArrayList<String> args) throws OperationException, NumberOperationException {
        if (args.size() == 2) {
            double num1 = Double.parseDouble(args.get(0));
            double num2 = Double.parseDouble(args.get(1));

            if (num2 == 0) {
                throw new NumberOperationException("NaN");
            }

            return String.valueOf(num1 / num2);
        }

        throw new OperationException("Divide operation requires two arguments");
    }

    public String Minus(ArrayList<String> args) throws OperationException {
        if (args.size() == 2) {
            double num1 = Double.parseDouble(args.get(0));
            double num2 = Double.parseDouble(args.get(1));

            return String.valueOf(num1 - num2);
        }

        throw new OperationException("Minus operation requires two arguments");
    }

    public String Plus(ArrayList<String> args) throws OperationException ,NumberFormatException{
        if (args.size() == 2) {
            double num1 = Double.parseDouble(args.get(0));
            double num2 = Double.parseDouble(args.get(1));

            return String.valueOf(num1 + num2);
        }

        throw new OperationException("Plus operation requires two arguments");
    }

    public String Abs(ArrayList<String> args) throws OperationException {
        if (args.size() == 1) {
            return String.valueOf(Math.abs(Double.parseDouble(args.getFirst())));
        }

        throw new OperationException("Abs operation requires one argument");
    }
}

