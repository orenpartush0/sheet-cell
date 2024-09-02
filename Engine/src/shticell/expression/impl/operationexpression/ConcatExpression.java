package shticell.expression.impl.operationexpression;

import shticell.expression.api.Expression;
import shticell.sheet.api.HasSheetData;
import shticell.sheet.cell.connection.CellConnection;
import shticell.sheet.cell.value.EffectiveValue;
import shticell.sheet.cell.value.EffectiveValueImpl;
import shticell.sheet.cell.value.ValueType;
import shticell.sheet.range.Range;

public class ConcatExpression implements  Expression {

    HasSheetData hasSheetData;
    CellConnection connections;
    public ConcatExpression(HasSheetData _hasSheetData , CellConnection cellConnection){
        hasSheetData = _hasSheetData;
        connections = cellConnection;
    }

    @Override
    public EffectiveValue eval(Expression... expressions) {

        if(expressions.length == 1 && expressions[0].eval().getValueType() == ValueType.RANGE){
            expressions[0].eval().getValueWithExpectation(Range.class);
        }
        if(expressions.length != 2){
            throw new UnsupportedOperationException("Concat needs two arguments");
        }
        EffectiveValue leftVal = expressions[0].eval();
        EffectiveValue rightVal = expressions[1].eval();

        return new EffectiveValueImpl(leftVal.getValueWithExpectation(String.class).concat(
                rightVal.getValueWithExpectation(String.class)), ValueType.STRING);
    }
}
