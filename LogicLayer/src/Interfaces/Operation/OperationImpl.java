package Interfaces.Operation;

import Interfaces.CellCoordinator;
import Interfaces.Operation.Exceptions.OperationException;
import Interfaces.Operation.Enums.eOperation;
import sheet.Exceptions.LoopConnectionException;

import java.util.ArrayList;
import java.util.Arrays;

public class OperationImpl implements Operation {

    private final CellCoordinator cellCoordinator;
    private final String callerCell;

    @Override
    public String eval(String... funcAndArgs) throws OperationException, LoopConnectionException {
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

    public OperationImpl(CellCoordinator _cellCoordinator,String _callerCell)
    {
        cellCoordinator = _cellCoordinator;
        callerCell = _callerCell;
    }

    private String ref(ArrayList<String> args) throws OperationException, LoopConnectionException {
        if(args.size() == 1){
            cellCoordinator.SetInfluenceBetweenTwoCells(callerCell,args.getFirst());
            return cellCoordinator.GetCellEffectiveValue(args.getFirst());
        }

        throw new OperationException("Ref operation requires one argument");
    }

    public String Concat(ArrayList<String> args) throws OperationException {
        if(args.size() == 3){
            int startIndex = Integer.parseInt(args.get(1));
            int endIndex = Integer.parseInt(args.get(2));

            return args.getFirst().substring(startIndex, endIndex);
        }

        throw new OperationException("Sub operation requires three arguments");
    }

    public String Sub(ArrayList<String> args) throws OperationException {
        if(args.size() == 2){
            return args.getFirst().concat(args.get(1));
        }

        throw new OperationException("Concat operation requires two arguments");
    }

    public String Pow(ArrayList<String> args) throws OperationException {
        if (args.size() == 2) {
            double num1 = Double.parseDouble(args.get(0));
            double num2 = Double.parseDouble(args.get(1));

            return String.valueOf(Math.pow(num1, num2));
        }

        throw new OperationException("Times operation requires two arguments");
    }

    public String Mod(ArrayList<String> args) throws OperationException {
        if (args.size() == 2) {
            double num1 = Double.parseDouble(args.get(0));
            double num2 = Double.parseDouble(args.get(1));

            if (num2 == 0) {
                throw new NumberFormatException("NaN");
            }

            return String.valueOf(num1 % num2);
        }

        throw new OperationException("Mod operation requires two arguments");
    }

    public String Times(ArrayList<String> args) throws OperationException {
        if (args.size() == 2) {
            double num1 = Double.parseDouble(args.get(0));
            double num2 = Double.parseDouble(args.get(1));

            return String.valueOf(num1 * num2);
        }

        throw new OperationException("Times operation requires two arguments");
    }

    public String Divide(ArrayList<String> args) throws OperationException {
        if (args.size() == 2) {
            double num1 = Double.parseDouble(args.get(0));
            double num2 = Double.parseDouble(args.get(1));

            if (num2 == 0) {
                throw new NumberFormatException("NaN");
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

    public String Plus(ArrayList<String> args) throws OperationException {
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

