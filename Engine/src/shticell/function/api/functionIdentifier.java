package shticell.function.api;

import shticell.expression.Enum.Operation;
import shticell.expression.api.Expression;
import shticell.expression.api.OperationsExpressionFactory;
import shticell.expression.api.TypeExpressionFactory;
import shticell.sheet.cell.connection.CellConnection;
import shticell.sheet.cell.value.EffectiveValue;
import shticell.sheet.cell.value.EffectiveValueImpl;
import shticell.sheet.cell.value.ValueType;
import shticell.sheet.api.HasSheetData;
import java.util.ArrayList;
import java.util.List;

public interface functionIdentifier {

    static OperationAndArguments decipherFunc(String funcText){
        List<EffectiveValue> Arguments = new ArrayList<>();
        int startIndex = 1; int endIndex = 0; int inFunction = 0;

        while(true){
            inFunction = funcText.charAt(endIndex) == '{' ? inFunction + 1: inFunction;
            inFunction = funcText.charAt(endIndex) == '}' ? inFunction - 1 : inFunction;

            if (inFunction == 1) {
                if (funcText.charAt(endIndex) == ',') {
                    Arguments.add(new EffectiveValueImpl(funcText.substring(startIndex, endIndex).replaceAll(" ", ""), ValueType.STRING));
                    startIndex = endIndex + 1;
                }
            } else if (inFunction == 0) {
                Arguments.add(new EffectiveValueImpl(funcText.substring(startIndex, endIndex).replaceAll(" ", ""), ValueType.STRING));
                break;
            }

            endIndex++;
        }

        String funcName = Arguments.removeFirst().getValueWithExpectation(String.class);
        return new OperationAndArguments(Operation.valueOf(funcName), Arguments);
    }

    static boolean isFunc(String func){
        return func.charAt(0) =='{' && func.charAt(func.length()-1) == '}';
    }

    static EffectiveValue calcFunc(EffectiveValue func, CellConnection connections, HasSheetData hasSheetData) throws NumberFormatException {
        if(func.getValueType() == ValueType.STRING && isFunc(func.getValueWithExpectation(String.class))){
            OperationAndArguments operationAndArguments = decipherFunc(func.getValueWithExpectation(String.class));
            Expression expression = OperationsExpressionFactory.getExpression(operationAndArguments.operation()
                    , hasSheetData,connections);


            List<Expression> arguments = operationAndArguments.arguments().stream()
                    .map(argument -> TypeExpressionFactory.getExpression(calcFunc(argument, connections, hasSheetData)))
                    .toList();


            return expression.eval(arguments.toArray(new Expression[0]));
        }
        else{
            return func;
        }
    }
}
