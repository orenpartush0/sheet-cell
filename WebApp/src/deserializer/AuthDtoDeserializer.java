package deserializer;

import com.google.gson.*;
import dto.AuthDto;
import dto.SheetDto;

import java.lang.reflect.Type;

public class AuthDtoDeserializer implements JsonDeserializer<AuthDto> {
    @Override
    public AuthDto deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String userName = jsonObject.get("userName").getAsString(); // Note the case sensitivity
        String sheetName = jsonObject.get("sheetName").getAsString();


        return new AuthDto(userName, sheetName);
    }
}
