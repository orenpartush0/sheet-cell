package shticell.expression.impl.operationexpression;

import shticell.expression.api.Expression;
import shticell.sheet.cell.value.EffectiveValue;
import shticell.sheet.cell.value.EffectiveValueImpl;
import shticell.sheet.cell.value.ValueType;

public class DivideExpression implements Expression {
    @Override
    public EffectiveValue eval(Expression... expressions) {
        if(expressions.length != 2){
            throw new UnsupportedOperationException("Divide needs two arguments");
        }
        EffectiveValue leftVal = expressions[0].eval();
        EffectiveValue rightVal = expressions[1].eval();

        if(rightVal.getValueWithExpectation(Double.class) == 0){
            throw new ArithmeticException("Cant divide by zero");
        }

        return new EffectiveValueImpl(leftVal.getValueWithExpectation(Double.class) /
                rightVal.getValueWithExpectation(double.class), ValueType.NUMERIC);
    }
}
