package shticell.expression.impl.typeexpression;

import shticell.expression.api.Expression;
import shticell.sheet.cell.value.EffectiveValue;
import shticell.sheet.cell.value.EffectiveValueImpl;
import shticell.sheet.cell.value.ValueType;
import shticell.sheet.range.Range;

public class RangeExpression implements Expression {
    private final Range expression;
    public RangeExpression(Range expression) {
        this.expression = expression;
    }

    @Override
    public EffectiveValue eval(Expression... expressions) {
        return new EffectiveValueImpl(expression, ValueType.RANGE);
    }
}
