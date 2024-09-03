package shticell.expression.impl.MathematicalExpressions;

import shticell.expression.api.Expression;
import shticell.sheet.cell.value.EffectiveValue;
import shticell.sheet.cell.value.EffectiveValueImpl;
import shticell.sheet.cell.value.ValueType;


public class PowExpression implements Expression {
    @Override
    public EffectiveValue eval(Expression... expressions) {
        if (expressions.length != 2) {
            throw new UnsupportedOperationException("Pow needs two arguments");
        }
        EffectiveValue left = expressions[0].eval();
        EffectiveValue right = expressions[1].eval();
        try{
            return new EffectiveValueImpl(Math.pow(left.getValueWithExpectation(Double.class),right.getValueWithExpectation(Double.class)), ValueType.NUMERIC);
        }
        catch (IllegalArgumentException e){
            throw new ArithmeticException();
        }
    }
}
