package deserializer;

import com.google.gson.*;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.coordinate.CoordinateFactory;
import shticell.sheet.range.Range;

import java.lang.reflect.Type;

import static constant.Constants.GSON;

public class RangeDeserializer implements JsonDeserializer<Range> {
    @Override
    public Range deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String rangeName = jsonObject.get("rangeName").getAsString();
        JsonObject startCellCoordinateJsonObj = jsonObject.get("startCellCoordinate").getAsJsonObject();
        JsonObject endCellCoordinateStrJsonObj = jsonObject.get("endCellCoordinate").getAsJsonObject();

        Coordinate startCellCoordinate = GSON.fromJson(startCellCoordinateJsonObj, Coordinate.class);
        Coordinate endCellCoordinate = GSON.fromJson(endCellCoordinateStrJsonObj, Coordinate.class);

        return new Range(rangeName,startCellCoordinate,endCellCoordinate);
    }
}
