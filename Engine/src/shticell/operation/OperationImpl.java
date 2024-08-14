package shticell.operation;

import shticell.operation.Exceptions.NumberOperationException;
import shticell.operation.Exceptions.OperationException;
import shticell.cell.ties.CellConnection;
import shticell.operation.Interface.Operation;
import shticell.operation.Enums.eOperation;
import shticell.exception.LoopConnectionException;
import shticell.cell.sheet.api.CellCoordinator;

import java.util.ArrayList;
import java.util.Arrays;

public class OperationImpl implements Operation {

    private final CellConnection connections;
    CellCoordinator coordinator;

    @Override
    public String eval(String... funcAndArgs) throws OperationException, LoopConnectionException, NumberFormatException, NumberOperationException {
        ArrayList<String> args = new ArrayList<>(Arrays.stream(funcAndArgs).toList());
        String operationName = args.removeFirst();
        eOperation operation;
        try {operation = eOperation.valueOf(operationName);} catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Operation named: " + operationName);
        }

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
            CellConnection.HasPath(coordinator.GetCellConnections(args.getFirst()),connections);
            coordinator.GetCellConnections(args.getFirst()).AddReferenceToThisCell(connections);
            connections.AddReferenceFromThisCell(coordinator.GetCellConnections(args.getFirst()));
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
            try {
                double num1 = Double.parseDouble(args.get(0));
                double num2 = Double.parseDouble(args.get(1));
                return String.valueOf(Math.pow(num1, num2));
            }
            catch (NumberFormatException e) {
                throw new OperationException("Invalid argument for Pow operation");
            }


        }

        throw new OperationException("Times operation requires two arguments");
    }

    public String Mod(ArrayList<String> args) throws OperationException, NumberFormatException, NumberOperationException {
        if (args.size() == 2) {

            try {
                double num1 = Double.parseDouble(args.get(0));
                double num2 = Double.parseDouble(args.get(1));

                if (num2 == 0) {
                    throw new NumberOperationException("NaN");
                }

                return String.valueOf(num1 % num2);
            }
            catch (NumberFormatException e) {
                throw new OperationException("Invalid argument for Mod operation");
            }
        }

        throw new OperationException("Mod operation requires two arguments");
    }

    public String Times(ArrayList<String> args) throws OperationException, NumberFormatException {
        if (args.size() == 2) {
            try {
                double num1 = Double.parseDouble(args.get(0));
                double num2 = Double.parseDouble(args.get(1));

                return String.valueOf(num1 * num2);
            }
            catch (NumberFormatException e) {
                throw new OperationException("Invalid argument for Times operation");
            }
        }

        throw new OperationException("Times operation requires two arguments");
    }

    public String Divide(ArrayList<String> args) throws OperationException, NumberOperationException {
        if (args.size() == 2) {
            try {
                double num1 = Double.parseDouble(args.get(0));
                double num2 = Double.parseDouble(args.get(1));

                if (num2 == 0) {
                    throw new NumberOperationException("NaN");
                }

                return String.valueOf(num1 / num2);
            }
            catch(NumberFormatException e) {
                throw new OperationException("Invalid argument for Divide operation");
            }
        }

        throw new OperationException("Divide operation requires two arguments");
    }

    public String Minus(ArrayList<String> args) throws OperationException {
        if (args.size() == 2) {

            try {
                double num1 = Double.parseDouble(args.get(0));
                double num2 = Double.parseDouble(args.get(1));

                return String.valueOf(num1 - num2);
            }
            catch(NumberFormatException e) {
                throw new OperationException("Invalid argument for Minus operation");
            }
        }

        throw new OperationException("Minus operation requires two arguments");
    }

    public String Plus(ArrayList<String> args) throws OperationException ,NumberFormatException{
        if (args.size() == 2) {
            try {
                double num1 = Double.parseDouble(args.get(0));
                double num2 = Double.parseDouble(args.get(1));

                return String.valueOf(num1 + num2);
            }
            catch (NumberFormatException e) {
                throw new OperationException("Invalid argument for Plus operation");
            }
        }

        throw new OperationException("Plus operation requires two arguments");
    }

    public String Abs(ArrayList<String> args) throws OperationException {
        if (args.size() == 1) {
            try {
                return String.valueOf(Math.abs(Double.parseDouble(args.getFirst())));
            }
            catch (NumberFormatException e) {
                throw new OperationException("Invalid argument for Abs operation");
            }
        }

        throw new OperationException("Abs operation requires one argument");
    }
}

