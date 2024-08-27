package shticell.sheet.cell.value;

import java.io.Serializable;

public interface EffectiveValue extends Cloneable, Serializable {
    Object getValue();
    ValueType getValueType();
    <T> T getValueWithExpectation(Class<T> type);
    EffectiveValue Clone();
}
