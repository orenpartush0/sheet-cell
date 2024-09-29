package deserializer;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonDeserializationContext;
import dto.FilterDto;
import shticell.sheet.range.Range;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterDtoDeserializer implements JsonDeserializer<FilterDto> {

    @Override
    public FilterDto deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        // Deserialize the range field
        Range range = context.deserialize(jsonObject.get("range"), Range.class);
        // Deserialize the filters field
        Map<Integer, List<String>> filters = new HashMap<>();
        JsonObject filtersJson = jsonObject.getAsJsonObject("filters");
        for (Map.Entry<String, JsonElement> entry : filtersJson.entrySet()) {
            Integer key = Integer.valueOf(entry.getKey());
            List<String> value = new ArrayList<>();
            for (JsonElement element : entry.getValue().getAsJsonArray()) {
                value.add(element.getAsString());
            }
            filters.put(key, value);
        }

        return new FilterDto(range, filters);
    }
}
