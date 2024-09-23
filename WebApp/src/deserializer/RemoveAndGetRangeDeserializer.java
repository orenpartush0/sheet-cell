package deserializer;

import com.google.gson.*;
import dto.AuthDto;
import dto.RemoveAndGetRangeDto;


import java.lang.reflect.Type;

public record RemoveAndGetRangeDeserializer() implements JsonDeserializer<RemoveAndGetRangeDto> {
    @Override
    public RemoveAndGetRangeDto deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String rangeName = context.deserialize(jsonObject.get("rangeName"), String.class);
        JsonObject authJson = jsonObject.getAsJsonObject("authDto");
        String userName = authJson.get("userName").getAsString();
        String sheetName = authJson.get("sheetName").getAsString();

        AuthDto authDto = new AuthDto(userName, sheetName);

        return new RemoveAndGetRangeDto(authDto,rangeName);
    }
}