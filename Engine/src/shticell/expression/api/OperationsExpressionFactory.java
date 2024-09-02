package shticell.expression.api;

import shticell.expression.Enum.Operation;
import shticell.expression.impl.operationexpression.*;
import shticell.sheet.api.HasSheetData;
import shticell.sheet.cell.connection.CellConnection;

import java.sql.Connection;

public interface OperationsExpressionFactory {
    static Expression getExpression(Operation operation, HasSheetData hasSheetData, CellConnection connection){
        switch (operation){
            case Operation.PLUS -> {
                return new PlusExpression();
            }
            case Operation.MINUS -> {
                return new MinusExpression();
            }
            case Operation.MOD -> {
                return new ModExpression();
            }
            case Operation.SUB -> {
                return new SubExpression();
            }
            case Operation.DIVIDE -> {
                return new DivideExpression();
            }
            case Operation.CONCAT -> {
                return new ConcatExpression(hasSheetData,connection);
            }
            case Operation.TIME -> {
                return new TimesExpression();
            }
            case Operation.ABS -> {
                return new AbsExpression();
            }
            case Operation.REF ->{
                return new RefExpression(hasSheetData,connection);
            }
            default -> throw new IllegalArgumentException("Unsupported operation: " + operation);
        }
    }
}
