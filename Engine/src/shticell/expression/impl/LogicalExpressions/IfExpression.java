package shticell.expression.impl.LogicalExpressions;

import shticell.expression.api.Expression;
import shticell.sheet.cell.value.EffectiveValue;
import shticell.sheet.cell.value.EffectiveValueImpl;
import shticell.sheet.cell.value.ValueType;

public class IfExpression implements Expression {
    @Override
    public EffectiveValue eval(Expression... expressions) {
        if(expressions.length != 3 || expressions[1].eval().getValueType() != expressions[2].eval().getValueType()){
            throw new UnsupportedOperationException("If must have 3 expressions");
        }

        EffectiveValue condition = expressions[0].eval();

        Boolean conditionValue = condition.getValueWithExpectation(Boolean.class);
        if(conditionValue){
            return new EffectiveValueImpl(expressions[1].eval().getValue(),expressions[1].eval().getValueType());
        }
        else{
            return new EffectiveValueImpl(expressions[2].eval().getValue(),expressions[2].eval().getValueType());
        }
    }
}
