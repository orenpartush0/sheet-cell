package deserializer;

import com.google.gson.*;
import dto.CellDto;
import dto.SheetDto;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.coordinate.CoordinateFactory;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class SheetDtoDeserializer implements JsonDeserializer<SheetDto> {
    @Override
    public SheetDto deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String name = jsonObject.get("Name").getAsString();
        int version = jsonObject.get("version").getAsInt();
        int numberOfRows = jsonObject.get("numberOfRows").getAsInt();
        int numberOfColumns = jsonObject.get("numberOfColumns").getAsInt();
        int colsWidth = jsonObject.get("colsWidth").getAsInt();
        int rowsHeight = jsonObject.get("rowsHeight").getAsInt();

        Map<Coordinate, CellDto> cells = null;
        JsonObject cellsJson = jsonObject.getAsJsonObject("cells");
        if(cellsJson != null) {
            cells = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry : cellsJson.entrySet()) {
                Coordinate coordinate = CoordinateFactory.getCoordinate(context.deserialize(new JsonParser().parse(entry.getKey()), String.class));
                CellDto cellDto = context.deserialize(entry.getValue(), CellDto.class);
                cells.put(coordinate, cellDto);
            }
        }

        return new SheetDto(name, version, numberOfRows, numberOfColumns, colsWidth, rowsHeight, cells);
    }
}
