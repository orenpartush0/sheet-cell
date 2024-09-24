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
        JsonObject rangeJson = jsonObject.getAsJsonObject("range");
        String rangeName = rangeJson.get("rangeName").getAsString();
        String startCellCoordinate = rangeJson.get("startCellCoordinate").getAsString();
        String endCellCoordinate = rangeJson.get("endCellCoordinate").getAsString();
        Range range = new Range(rangeName, CoordinateFactory.getCoordinate(startCellCoordinate),
                CoordinateFactory.getCoordinate(endCellCoordinate));

        // Deserialize cols (Queue<String>)
        Queue<String> cols = context.deserialize(jsonObject.getAsJsonArray("cols"), LinkedList.class);

        // Return SortDto
        return new SortDto(range, cols);
    }
}
