package shticell.expression.impl.operationexpression;

import shticell.expression.api.Expression;
import shticell.sheet.cell.value.EffectiveValue;
import shticell.sheet.cell.value.EffectiveValueImpl;
import shticell.sheet.cell.value.ValueType;

public class SubExpression implements Expression {
    @Override
    public EffectiveValue eval(Expression... expressions) {
        if(expressions.length != 3){
            throw new UnsupportedOperationException("Sub needs two arguments");
        }

        String sourceValue = expressions[0].eval().getValueWithExpectation(String.class);
        double startIndexValue = expressions[1].eval().getValueWithExpectation(Double.class);
        double endIndexValue = expressions[2].eval().getValueWithExpectation(Double.class);

        if(startIndexValue >= sourceValue.length() || endIndexValue >= sourceValue.length() ||
                startIndexValue < 0 || endIndexValue < 0){
            throw new IndexOutOfBoundsException();
        }

        return new EffectiveValueImpl(sourceValue.substring((int)startIndexValue,(int)endIndexValue),ValueType.STRING);
    }
}
