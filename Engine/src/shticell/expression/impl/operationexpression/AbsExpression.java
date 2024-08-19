package shticell.expression.impl.operationexpression;

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
        return new EffectiveValueImpl(Math.abs(expressions[0].eval().getValueWithExpectation(Double.class)), ValueType.NUMERIC);
    }
}
