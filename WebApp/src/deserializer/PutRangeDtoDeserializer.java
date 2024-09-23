package deserializer;

import com.google.gson.*;
import dto.AuthDto;
import dto.PutRangeDto;
import shticell.sheet.coordinate.CoordinateFactory;
import shticell.sheet.range.Range;

import java.lang.reflect.Type;

public class PutRangeDtoDeserializer implements JsonDeserializer<PutRangeDto> {
    @Override
    public PutRangeDto deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        JsonObject authJson = jsonObject.getAsJsonObject("authDto");
        String userName = authJson.get("userName").getAsString();
        String sheetName = authJson.get("sheetName").getAsString();
        JsonObject rangeJson = jsonObject.getAsJsonObject("range");
        String rangeName = rangeJson.get("rangeName").getAsString();
        String startCellCoordinate = rangeJson.get("startCellCoordinate").getAsString();
        String endCellCoordinate = rangeJson.get("endCellCoordinate").getAsString();

        Range range = new Range(rangeName, CoordinateFactory.getCoordinate(startCellCoordinate),
                CoordinateFactory.getCoordinate(endCellCoordinate));

        AuthDto authDto = new AuthDto(userName, sheetName);
        return new PutRangeDto(range, authDto);
    }
}