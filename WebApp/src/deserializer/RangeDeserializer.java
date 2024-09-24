package deserializer;

import com.google.gson.*;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.coordinate.CoordinateFactory;
import shticell.sheet.range.Range;

import java.lang.reflect.Type;

public class RangeDeserializer implements JsonDeserializer<Range> {
    @Override
    public Range deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        // Manually extract row and col from the coordinate JSON object
        JsonObject rangeJason = jsonObject.getAsJsonObject("coordinate");
        String rangeName = rangeJason.get("rangeName").getAsString();
        String startCellCoordinateStr = rangeJason.get("startCellCoordinate").getAsString();
        String endCellCoordinateStr = rangeJason.get("endCellCoordinate").getAsString();

        Coordinate startCellCoordinate = CoordinateFactory.getCoordinate(startCellCoordinateStr);
        Coordinate endCellCoordinate = CoordinateFactory.getCoordinate(endCellCoordinateStr);

        return new Range(rangeName,startCellCoordinate,endCellCoordinate);
    }
}
