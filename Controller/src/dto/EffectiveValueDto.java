package dto;

import shticell.sheet.cell.value.EffectiveValue;
import shticell.sheet.cell.value.ValueType;

public record EffectiveValueDto(Object value, ValueType valueType) {

    public EffectiveValueDto(EffectiveValue effectiveValue){
        this(effectiveValue.getValue(), effectiveValue.getValueType());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
