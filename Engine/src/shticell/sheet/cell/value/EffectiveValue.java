package shticell.sheet.cell.value;

public interface EffectiveValue extends Cloneable {
    Object getValue();
    ValueType getValueType();
    <T> T getValueWithExpectation(Class<T> type);
    EffectiveValue Clone();
}
