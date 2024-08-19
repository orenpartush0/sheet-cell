package shticell.expression.impl.operationexpression;

import shticell.expression.api.Expression;
import shticell.sheet.cell.value.EffectiveValue;
import shticell.sheet.cell.value.EffectiveValueImpl;
import shticell.sheet.cell.value.ValueType;

public class ModExpression implements Expression {
    @Override
    public EffectiveValue eval(Expression... expressions) {
        if(expressions.length != 2){
            throw new UnsupportedOperationException("Mod needs two arguments");
        }
        EffectiveValue leftVal = expressions[0].eval();
        EffectiveValue rightVal = expressions[1].eval();

        if(rightVal.getValueWithExpectation(Double.class) == 0){
            throw new ArithmeticException("Can't mod with 0");
        }

        return new EffectiveValueImpl(leftVal.getValueWithExpectation(Double.class) %
                rightVal.getValueWithExpectation(Double.class), ValueType.NUMERIC);
    }
}
