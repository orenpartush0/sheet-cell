package deserializer;

import com.google.gson.*;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.coordinate.CoordinateFactory;

import java.lang.reflect.Type;

public class CoordinateDeserializer implements JsonDeserializer<Coordinate> {
    @Override
    public Coordinate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        // Manually extract row and col from the coordinate JSON object
        JsonObject coordinateJson = jsonObject.getAsJsonObject("coordinate");
        int row = coordinateJson.get("row").getAsInt();
        int col = coordinateJson.get("col").getAsInt();

        return CoordinateFactory.getCoordinate(row,col);
    }
}
