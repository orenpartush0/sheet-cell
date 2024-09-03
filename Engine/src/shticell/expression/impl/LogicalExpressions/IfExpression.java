package shticell.expression.impl.LogicalExpressions;

import shticell.expression.api.Expression;
import shticell.sheet.cell.value.EffectiveValue;
import shticell.sheet.cell.value.EffectiveValueImpl;
import shticell.sheet.cell.value.ValueType;

public class IfExpression implements Expression {
    @Override
    public EffectiveValue eval(Expression... expressions) {
        if(expressions.length != 3){
            throw new IllegalArgumentException("If must have 3 expressions");
        }

        EffectiveValue condition = expressions[0].eval();

        Boolean conditionValue = condition.getValueWithExpectation(Boolean.class);
        if(conditionValue){
            return expressions[1].eval();
        }
        else{
            return expressions[2].eval();
        }

    }
}
