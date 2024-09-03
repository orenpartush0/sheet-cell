package shticell.expression.impl.StringExpressions;

import shticell.expression.api.Expression;
import shticell.sheet.cell.value.EffectiveValue;
import shticell.sheet.cell.value.EffectiveValueImpl;
import shticell.sheet.cell.value.ValueType;

public class ConcatExpression implements  Expression {

    @Override
    public EffectiveValue eval(Expression... expressions) {
        StringBuilder strBuilder = new StringBuilder();

        if (expressions.length != 2) {
            throw new UnsupportedOperationException("Concat needs two arguments");
        } else {
            EffectiveValue leftVal = expressions[0].eval();
            EffectiveValue rightVal = expressions[1].eval();
            strBuilder.append(leftVal.getValueWithExpectation(String.class).concat(rightVal.getValueWithExpectation(String.class)));

            return new EffectiveValueImpl(strBuilder.toString(), ValueType.STRING);
        }
    }
}
