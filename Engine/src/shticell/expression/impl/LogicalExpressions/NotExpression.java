package shticell.expression.impl.LogicalExpressions;

import shticell.expression.api.Expression;
import shticell.expression.impl.BooleanFuncUnknown;
import shticell.sheet.cell.value.EffectiveValue;
import shticell.sheet.cell.value.EffectiveValueImpl;
import shticell.sheet.cell.value.ValueType;

public class NotExpression implements Expression {
    @Override
    public EffectiveValue eval(Expression... expressions) {
        if(expressions.length != 1){
            throw new UnsupportedOperationException("Not must have 1 expression");
        }
        try {
            return new EffectiveValueImpl(!expressions[0].eval().getValueWithExpectation(Boolean.class), ValueType.BOOLEAN);
        }
        catch (IllegalArgumentException e){
            throw new BooleanFuncUnknown();
        }
    }
}
