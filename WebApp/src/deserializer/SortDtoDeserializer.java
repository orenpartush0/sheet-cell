package deserializer;

import com.google.gson.*;
import dto.SortDto;
import shticell.sheet.coordinate.CoordinateFactory;
import shticell.sheet.range.Range;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.Queue;

public class SortDtoDeserializer implements JsonDeserializer<SortDto> {
    @Override
    public SortDto deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        // Deserialize Range
        Range range = context.deserialize(jsonObject.get("range"), Range.class);

        // Deserialize cols (Queue<String>)
        Queue<String> cols = context.deserialize(jsonObject.getAsJsonArray("cols"), LinkedList.class);

        // Return SortDto
        return new SortDto(range, cols);
    }
}
