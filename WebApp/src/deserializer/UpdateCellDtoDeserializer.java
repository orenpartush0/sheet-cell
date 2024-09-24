package deserializer;

import com.google.gson.*;
import dto.UpdateCellDto;
import shticell.sheet.coordinate.Coordinate;

import java.lang.reflect.Type;

public class UpdateCellDtoDeserializer implements JsonDeserializer<UpdateCellDto> {
    @Override
    public UpdateCellDto deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        // Manually extract row and col from the coordinate JSON object
        JsonObject coordinateJson = jsonObject.getAsJsonObject("coordinate");
        int row = coordinateJson.get("row").getAsInt();
        int col = coordinateJson.get("col").getAsInt();

        // Create the Coordinate object using row and col
        Coordinate coordinate = new Coordinate(row, col);

        // Extract newValue
        String newValue = jsonObject.get("newValue").getAsString();

        // Return the UpdateCellDto instance
        return new UpdateCellDto(coordinate, newValue);
    }
}
