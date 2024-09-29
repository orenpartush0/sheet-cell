package dto;

import com.google.gson.*;
import shticell.sheet.coordinate.Coordinate;

import java.lang.reflect.Type;

public class CoordinateDeserializer implements JsonDeserializer<Coordinate> {
    @Override
    public Coordinate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        int row = jsonObject.get("row").getAsInt();
        int col = jsonObject.get("col").getAsInt();
        return new Coordinate(row, col);
    }
}
