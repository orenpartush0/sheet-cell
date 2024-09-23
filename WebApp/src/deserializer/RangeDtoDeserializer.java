package deserializer;

import com.google.gson.*;
import dto.SheetDto;
import shticell.sheet.coordinate.CoordinateFactory;
import shticell.sheet.range.Range;

import java.lang.reflect.Type;

public class RangeDtoDeserializer implements JsonDeserializer<Range> {
    @Override
    public Range deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String rangeName = jsonObject.get("rangeName").getAsString();
        String startCellCoordinateSquare = jsonObject.get("startCellCoordinate").getAsString();
        String endCellCoordinateSquare = jsonObject.get("endCellCoordinate").getAsString();


        return new Range(rangeName, CoordinateFactory.getCoordinate(startCellCoordinateSquare),
                CoordinateFactory.getCoordinate(endCellCoordinateSquare));
    }
}
