package shticell.expression.impl.typeexpression;

import shticell.expression.api.Expression;
import shticell.sheet.cell.value.EffectiveValue;
import shticell.sheet.cell.value.EffectiveValueImpl;
import shticell.sheet.cell.value.ValueType;

public class StringExpression implements Expression {
    private final String expression;
    public StringExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public EffectiveValue eval(Expression... expressions) {
        return new EffectiveValueImpl(expression, ValueType.STRING);
    }
}
