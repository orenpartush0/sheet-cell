package shticell.expression.api;

import shticell.expression.Enum.Operation;
import shticell.expression.impl.LogicalExpressions.*;
import shticell.expression.impl.MathematicalExpressions.*;
import shticell.expression.impl.StringExpressions.ConcatExpression;
import shticell.expression.impl.StringExpressions.SubExpression;
import shticell.expression.impl.SystemExpressions.*;
import shticell.sheet.api.HasSheetData;
import shticell.sheet.cell.connection.CellConnection;

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
                return new ConcatExpression();
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
            case Operation.SUM -> {
                return new SumExpression(hasSheetData,connection);
            }
            case Operation.AVERAGE -> {
                return new AverageExpression(hasSheetData,connection);
            }
            case Operation.PERCENT -> {
                return new PercentExpression();
            }
            case Operation.EQUAL -> {
                return new EqualExpression();
            }
            case Operation.POW -> {
                return new PowExpression();
            }
            case Operation.NOT -> {
                return new NotExpression();
            }
            case Operation.AND -> {
                return new AndExpression();
            }
            case Operation.OR -> {
                return new OrExpression();
            }
            case Operation.IF -> {
                return new IfExpression();
            }
            case Operation.BIGGER -> {
                return new BiggerExpression();
            }
            case Operation.LESS -> {
                return new LessExpression();
            }

            default -> throw new IllegalArgumentException("Unsupported operation: " + operation);
        }
    }
}
