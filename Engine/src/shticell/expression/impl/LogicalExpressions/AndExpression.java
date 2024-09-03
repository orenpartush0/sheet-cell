package shticell.expression.impl.LogicalExpressions;

import shticell.expression.api.Expression;
import shticell.sheet.cell.value.EffectiveValue;
import shticell.sheet.cell.value.EffectiveValueImpl;
import shticell.sheet.cell.value.ValueType;

public class AndExpression implements Expression {
    @Override
    public EffectiveValue eval(Expression... expressions) {
        if(expressions.length != 2){
            throw new IllegalArgumentException("And must have 2 expressions");
        }

        EffectiveValue leftVal = expressions[0].eval();
        EffectiveValue rightVal = expressions[1].eval();

        return new EffectiveValueImpl(leftVal.getValueWithExpectation(Boolean.class)  && rightVal.getValueWithExpectation(Boolean.class)
                , ValueType.BOOLEAN);
    }
}
