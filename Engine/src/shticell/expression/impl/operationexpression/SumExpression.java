package shticell.expression.impl.operationexpression;

import shticell.expression.api.Expression;
import shticell.sheet.api.HasSheetData;
import shticell.sheet.cell.connection.CellConnection;
import shticell.sheet.cell.value.EffectiveValue;
import shticell.sheet.cell.value.EffectiveValueImpl;
import shticell.sheet.cell.value.ValueType;
import shticell.sheet.range.Range;

public class SumExpression  implements Expression {

    HasSheetData hasSheetData;
    CellConnection connections;
    public SumExpression(HasSheetData _hasSheetData , CellConnection cellConnection){
        hasSheetData = _hasSheetData;
        connections = cellConnection;
    }

    @Override
    public EffectiveValue eval(Expression... expressions) {
        if (expressions.length != 1) {
            throw new UnsupportedOperationException("Concat needs two arguments");
        } else {
            EffectiveValue val = expressions[0].eval();
            Range range = val.getValueWithExpectation(Range.class);
            if(!hasSheetData.IsRangeInSheet(range.rangeName())){
                throw new RuntimeException("Unknown range");
            }

            double sum =range.getRangeCellsCoordinate().
                    stream().
                    filter(coordinate -> hasSheetData.GetCellEffectiveValue(coordinate).getValueType() == ValueType.NUMERIC).
                    mapToDouble(coordinate->hasSheetData.GetCellEffectiveValue(coordinate).getValueWithExpectation(double.class)).
                    sum();

            range.getRangeCellsCoordinate().
                    forEach(coordinate -> {
                        hasSheetData.GetCellConnections(coordinate).AddToInfluenceOn(connections);
                        connections.AddToDependsOn(hasSheetData.GetCellConnections(coordinate));
                    });


            return new EffectiveValueImpl(sum, ValueType.STRING);
        }
    }
}
