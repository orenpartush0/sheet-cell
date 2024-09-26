package deserializer;

import com.google.gson.*;
import dto.CellDto;
import dto.SheetDto;
import shticell.sheet.cell.value.EffectiveValue;
import shticell.sheet.coordinate.Coordinate;

import java.lang.reflect.Type;
import java.util.*;

public class SheetDtoDeserializer implements JsonDeserializer<SheetDto> {
    @Override
    public SheetDto deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        // Deserialize SheetDto
        String name = jsonObject.get("Name").getAsString();
        int version = jsonObject.get("version").getAsInt();
        int numberOfRows = jsonObject.get("numberOfRows").getAsInt();
        int numberOfColumns = jsonObject.get("numberOfColumns").getAsInt();
        int colsWidth = jsonObject.get("colsWidth").getAsInt();
        int rowsHeight = jsonObject.get("rowsHeight").getAsInt();

        Map<String, CellDto> cellsMap = new HashMap<>();
        JsonObject cellsObject = jsonObject.get("cells").getAsJsonObject();
        for (Map.Entry<String, JsonElement> cellEntry : cellsObject.entrySet()) {
            String cellId = cellEntry.getKey();
            JsonObject cellObject = cellEntry.getValue().getAsJsonObject();

            // Deserialize Cell object
            Coordinate coordinate = context.deserialize(cellObject.get("coordinate"), Coordinate.class);
            int latestSheetVersionUpdated = cellObject.get("LatestSheetVersionUpdated").getAsInt();
            String originalValue = cellObject.get("originalValue").getAsString();
            String effectiveValue = context.deserialize(cellObject.get("effectiveValue"), String.class);
            List<String> dependsOn = deserializeList(cellObject.get("dependsOn"));
            List<String> influenceOn = deserializeList(cellObject.get("influenceOn"));

            Cell cell = new Cell(coordinate, latestSheetVersionUpdated, originalValue, effectiveValue, dependsOn, influenceOn);
            cellsMap.put(cellId, cell);
        }

        return new SheetDto(name, version, numberOfRows, numberOfColumns, colsWidth, rowsHeight, cellsMap);
    }


    private List<String> deserializeList(JsonElement element) {
        if (element.isJsonArray()) {
            JsonArray jsonArray = element.getAsJsonArray();
            List<String> list = new ArrayList<>();
            for (JsonElement item : jsonArray) {
                list.add(item.getAsString());
            }
            return list;
        } else {
            return Collections.emptyList();
        }
    }
 }

