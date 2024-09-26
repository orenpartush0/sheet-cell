package deserializer;

import com.google.gson.*;
import dto.AddRequestDto;
import dto.UpdateRequestDto;
import shticell.manager.enums.PermissionType;

import java.lang.reflect.Type;

public class AddRequestDtoDeserializer implements JsonDeserializer<AddRequestDto> {
    @Override
    public AddRequestDto deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        int reqId = jsonObject.get("reqId").getAsInt();
        String  permissionType = jsonObject.get("permissionType").getAsString();

        return new AddRequestDto(reqId, PermissionType.valueOf(permissionType));
    }
}