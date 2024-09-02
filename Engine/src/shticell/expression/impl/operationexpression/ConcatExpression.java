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
        StringBuilder strBuilder = new StringBuilder();

        if (expressions.length != 2) {
            throw new UnsupportedOperationException("Concat needs two arguments");
        } else {
            EffectiveValue leftVal = expressions[0].eval();
            EffectiveValue rightVal = expressions[1].eval();
            strBuilder.append(leftVal.getValueWithExpectation(String.class).concat(rightVal.getValueWithExpectation(String.class)));

            return new EffectiveValueImpl(strBuilder.toString(), ValueType.STRING);
        }
    }
}
