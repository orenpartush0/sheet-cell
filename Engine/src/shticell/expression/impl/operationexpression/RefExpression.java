package shticell.expression.impl.operationexpression;

import shticell.expression.api.Expression;
import shticell.sheet.api.HasSheetData;
import shticell.sheet.cell.connection.CellConnection;
import shticell.sheet.cell.value.EffectiveValue;
import shticell.sheet.cell.value.EffectiveValueImpl;
import shticell.sheet.coordinate.CoordinateFactory;


public class RefExpression implements Expression {

    HasSheetData hasSheetData;
    CellConnection connections;
    public RefExpression(HasSheetData _hasSheetData , CellConnection cellConnection){
        hasSheetData = _hasSheetData;
        connections = cellConnection;
    }

    @Override
    public EffectiveValue eval(Expression... expressions) {
        if(expressions.length != 1){
            throw new UnsupportedOperationException("Ref needs one argument");
        }

        String coordinate = expressions[0].eval().getValueWithExpectation(String.class);
        if(!CoordinateFactory.isValidCoordinate(coordinate)
                || !hasSheetData.IsCoordinateInSheet(CoordinateFactory.getCoordinate(coordinate))){
            throw new RuntimeException("Unknown coordinate " + coordinate);
        }

        hasSheetData.GetCellConnections(
                CoordinateFactory.getCoordinate(coordinate))
                .AddToInfluenceOn(connections);

        connections.AddToDependsOn(
                hasSheetData.GetCellConnections(
                        CoordinateFactory.getCoordinate(coordinate)));

        return new EffectiveValueImpl(hasSheetData.GetCellEffectiveValue(CoordinateFactory.getCoordinate(expressions[0].eval().getValueWithExpectation(String.class))));
    }
}