package shticell.sheet.cell.value;

import shticell.sheet.api.HasSheetData;

public interface EfectiveValueFactory {

    static EffectiveValueImpl getEffectiveValue(String value, HasSheetData hasSheetData){
        if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")){
            return new EffectiveValueImpl(Boolean.parseBoolean(value),ValueType.BOOLEAN);
        }
        else if(value.matches( "^-?(0|[1-9]\\d*)(\\.\\d+)?$")){
            return new EffectiveValueImpl(Double.parseDouble(value),ValueType.NUMERIC);
        }
        else if(hasSheetData.IsRangeInSheet(value)){
            return new EffectiveValueImpl(hasSheetData.GetRange(value),ValueType.RANGE);
        }
        else{
            return new EffectiveValueImpl(value,ValueType.STRING);
        }
    }
}
