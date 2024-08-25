package shticell.sheet.cell.value;

public interface EfectiveValueFactory {

    static EffectiveValueImpl getEffectiveValue(String value){
        if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")){
            return new EffectiveValueImpl(Boolean.parseBoolean(value),ValueType.BOOLEAN);
        }
        else if(value.matches( "^-?(0|[1-9]\\d*)(\\.\\d+)?$")){
            return new EffectiveValueImpl(Double.parseDouble(value),ValueType.NUMERIC);
        }
        else{
            return new EffectiveValueImpl(value,ValueType.STRING);
        }
    }
}
