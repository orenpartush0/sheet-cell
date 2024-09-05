package shticell.expression.impl.LogicalExpressions;

import shticell.expression.api.Expression;
import shticell.expression.impl.typeexpression.BooleanExpression;
import shticell.sheet.cell.value.EffectiveValue;
import shticell.sheet.cell.value.EffectiveValueImpl;
import shticell.sheet.cell.value.ValueType;

public class EqualExpression implements Expression {
    @Override
    public EffectiveValue eval(Expression... expressions) {
        if (expressions.length != 2) {
            throw new UnsupportedOperationException("Equal needs two arguments");
        }
        EffectiveValue leftVal = expressions[0].eval();
        EffectiveValue rightVal = expressions[1].eval();

        if(leftVal.getValueType().equals(rightVal.getValueType())){
            return new EffectiveValueImpl(true , ValueType.BOOLEAN);
        }
        else{
            return new EffectiveValueImpl(false , ValueType.BOOLEAN);
        }
    }


}


