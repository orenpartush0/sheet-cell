package shticell.sheet.cell.value;

public class EffectiveValueImpl implements EffectiveValue {

    Object value = null;
    ValueType valueType;

    public EffectiveValueImpl(Object value, ValueType valueType) {
        this.value = value;
        this.valueType = valueType;
    }

    public EffectiveValueImpl(EffectiveValue effectiveValue) {
        this.value = effectiveValue.getValue();
        this.valueType = effectiveValue.getValueType();
    }

    @Override
    public Object getValue() {
        return value;
    }


    @Override
    public ValueType getValueType() {
        return valueType;
    }


    @Override
    public <T> T getValueWithExpectation(Class<T> type) {
            if (valueType.isAssignableFrom(type)) {
                return type.cast(value);
            }

            throw new IllegalArgumentException("Invalid argument in the function");
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public EffectiveValue Clone() {
        return new EffectiveValueImpl(value,valueType);
    }
}
