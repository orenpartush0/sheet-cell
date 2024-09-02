package shticell.sheet.cell.value;


import shticell.expression.impl.typeexpression.NaNExpression;
import shticell.expression.impl.typeexpression.Undefinedxpression;
import shticell.sheet.range.Range;

public enum ValueType {
    STRING(String.class),
    NUMERIC(Double.class),
    BOOLEAN(Boolean.class),
    RANGE(Range.class),
    UNDEFINED(Undefinedxpression.class),
    NAN(NaNExpression.class);

    private final Class<?> type;

    ValueType(Class<?> type) {
        this.type = type;
    }

    public boolean isAssignableFrom(Class<?> aType){
        return type.isAssignableFrom(aType);
    }
}
