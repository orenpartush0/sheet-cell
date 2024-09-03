package shticell.expression.impl.MathematicalExpressions;

import shticell.expression.api.Expression;
import shticell.sheet.cell.value.EffectiveValue;
import shticell.sheet.cell.value.EffectiveValueImpl;
import shticell.sheet.cell.value.ValueType;

public class MinusExpression implements Expression{

    @Override
    public EffectiveValue eval(Expression... expressions) {
        if(expressions.length != 2){
            throw new UnsupportedOperationException("Minus needs two arguments");
        }
        EffectiveValue leftVal = expressions[0].eval();
        EffectiveValue rightVal = expressions[1].eval();
        return new EffectiveValueImpl(leftVal.getValueWithExpectation(Double.class) -
                rightVal.getValueWithExpectation(double.class), ValueType.NUMERIC);
    }
}
