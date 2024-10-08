package shticell.expression.impl.LogicalExpressions;

import shticell.expression.api.Expression;
import shticell.expression.impl.BooleanFuncUnknown;
import shticell.sheet.cell.value.EffectiveValue;
import shticell.sheet.cell.value.EffectiveValueImpl;
import shticell.sheet.cell.value.ValueType;

public class LessExpression implements Expression {
    @Override
    public EffectiveValue eval(Expression... expressions) {
        if(expressions.length != 2){
            throw new UnsupportedOperationException("Less must have 2 expressions");
        }

        EffectiveValue leftVal = expressions[0].eval();
        EffectiveValue rightVal = expressions[1].eval();
        try {
            return new EffectiveValueImpl(leftVal.getValueWithExpectation(Double.class) <= rightVal.getValueWithExpectation(Double.class)
                    , ValueType.BOOLEAN);
        }
        catch (IllegalArgumentException e){
            throw new BooleanFuncUnknown();
        }
    }

}
