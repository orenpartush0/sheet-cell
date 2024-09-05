package shticell.expression.impl.MathematicalExpressions;

import shticell.expression.api.Expression;
import shticell.sheet.cell.value.EffectiveValue;
import shticell.sheet.cell.value.EffectiveValueImpl;
import shticell.sheet.cell.value.ValueType;


public class PercentExpression implements Expression {

    @Override
    public EffectiveValue eval(Expression... expressions) {
        if(expressions.length != 2){
            throw new UnsupportedOperationException("Percent needs two arguments");
        }
        EffectiveValue part = expressions[0].eval();
        EffectiveValue whole = expressions[1].eval();

        try{
        if(whole.getValueWithExpectation(Double.class) == 0){
            throw new ArithmeticException("Can't calculate percent with 0");
        }
            return new EffectiveValueImpl(part.getValueWithExpectation(Double.class) /
                    whole.getValueWithExpectation(Double.class)*100, ValueType.NUMERIC);
        }
        catch (IllegalArgumentException e){
            throw new ArithmeticException();
        }


    }
}
