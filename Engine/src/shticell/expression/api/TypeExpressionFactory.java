package shticell.expression.api;

import shticell.expression.impl.typeexpression.*;
import shticell.sheet.cell.value.EffectiveValue;
import shticell.sheet.cell.value.ValueType;

public interface TypeExpressionFactory {
    public static Expression getExpression(EffectiveValue effectiveValue){
        return switch (effectiveValue.getValueType()) {
            case ValueType.NUMERIC -> new NumberExpression(effectiveValue.getValueWithExpectation(Double.class));
            case ValueType.STRING -> new StringExpression(effectiveValue.getValueWithExpectation(String.class));
            case ValueType.BOOLEAN -> new BooleanExpression(effectiveValue.getValueWithExpectation(Boolean.class));
            case ValueType.NAN -> new NaNExpression(effectiveValue.getValueWithExpectation(String.class));
            case ValueType.UNDEFINED -> new Undefinedxpression(effectiveValue.getValueWithExpectation(String.class));
            default -> throw new IllegalArgumentException("Unsupported value type: " + effectiveValue.getValueType());
        };
    }
}
