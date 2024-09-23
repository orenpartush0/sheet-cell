package deserializer;

import com.google.gson.*;
import dto.SheetDto;
import java.lang.reflect.Type;

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

        return new SheetDto(name, version, numberOfRows, numberOfColumns, colsWidth, rowsHeight);
    }
}
