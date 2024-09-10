package shticell.sheet.cell.value;

import java.io.Serializable;

public class EffectiveValueImpl implements EffectiveValue, Serializable, Cloneable {

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
    public void setValue(Object _value,ValueType _valueType){
        value = _value;
        valueType = _valueType;
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


    @Override
    public EffectiveValueImpl clone() {
        try {
            return (EffectiveValueImpl) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
