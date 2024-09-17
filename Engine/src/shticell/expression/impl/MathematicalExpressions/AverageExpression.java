package shticell.expression.impl.MathematicalExpressions;

import shticell.expression.api.Expression;
import shticell.sheet.api.HasSheetData;
import shticell.sheet.cell.connection.CellConnection;
import shticell.sheet.cell.value.EffectiveValue;
import shticell.sheet.cell.value.EffectiveValueImpl;
import shticell.sheet.cell.value.ValueType;
import shticell.sheet.range.Range;

import java.util.OptionalDouble;


public class AverageExpression  implements Expression {

    HasSheetData hasSheetData;
    CellConnection connections;
    public AverageExpression(HasSheetData _hasSheetData , CellConnection cellConnection){
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

            OptionalDouble avg =range.getRangeCellsCoordinate().
                    stream().
                    filter(coordinate -> hasSheetData.GetCellEffectiveValue(coordinate).getValueType() == ValueType.NUMERIC).
                    mapToDouble(coordinate->hasSheetData.GetCellEffectiveValue(coordinate).getValueWithExpectation(Double.class)).
                    average();

            hasSheetData.UseRange(connections.GetCellCoordinate(),range);

            range.getRangeCellsCoordinate().
                    forEach(coordinate -> {
                        hasSheetData.GetCellConnections(coordinate).AddToInfluenceOn(connections);
                        connections.AddToDependsOn(hasSheetData.GetCellConnections(coordinate));
                    });


            return new EffectiveValueImpl(avg.getAsDouble(), ValueType.NUMERIC);
        }
    }
}
