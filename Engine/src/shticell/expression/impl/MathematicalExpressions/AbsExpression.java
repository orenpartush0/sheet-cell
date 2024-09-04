package shticell.expression.impl.MathematicalExpressions;

import shticell.expression.api.Expression;
import shticell.sheet.cell.value.EffectiveValue;
import shticell.sheet.cell.value.EffectiveValueImpl;
import shticell.sheet.cell.value.ValueType;

public class AbsExpression implements Expression{
    @Override
    public EffectiveValue eval(Expression... expressions) {
        if(expressions.length != 1){
            throw new UnsupportedOperationException("Abs needs one argument");
        }
        try {
            return new EffectiveValueImpl(Math.abs(expressions[0].eval().getValueWithExpectation(Double.class)), ValueType.NUMERIC);
        }
        catch(IllegalArgumentException e){
            throw new ArithmeticException(e.getMessage());
        }
    }
}
