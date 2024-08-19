package shticell.expression.api;

import shticell.sheet.cell.value.EffectiveValue;

public interface Expression {
    public EffectiveValue eval(Expression... expressions);
}
