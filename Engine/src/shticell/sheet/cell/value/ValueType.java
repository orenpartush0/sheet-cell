package shticell.sheet.cell.value;

import com.sun.codemodel.JClass;

public enum ValueType {
    STRING(String.class),
    NUMERIC(Double.class),
    BOOLEAN(Boolean.class);

    private final Class<?> type;

    ValueType(Class<?> type) {
        this.type = type;
    }

    public boolean isAssignableFrom(Class<?> aType){
        return type.isAssignableFrom(aType);
    }
}
