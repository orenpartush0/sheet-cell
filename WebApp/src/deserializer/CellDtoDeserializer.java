package dto;

import com.google.gson.*;
import shticell.sheet.coordinate.Coordinate;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CellDtoDeserializer implements JsonDeserializer<CellDto> {
    @Override
    public CellDto deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Coordinate coordinate = context.deserialize(jsonObject.get("coordinate"), Coordinate.class);
        int latestSheetVersionUpdated = jsonObject.get("LatestSheetVersionUpdated").getAsInt();
        String originalValue = jsonObject.get("originalValue").getAsString();
        String effectiveValue = jsonObject.get("effectiveValue").getAsString();
        ArrayList<Coordinate> dependsOn = context.deserialize(jsonObject.get("dependsOn"), ArrayList.class);
        ArrayList<Coordinate> influenceOn = context.deserialize(jsonObject.get("influenceOn"), ArrayList.class);

        return new CellDto(coordinate, latestSheetVersionUpdated, originalValue, effectiveValue, dependsOn, influenceOn);
    }
}
