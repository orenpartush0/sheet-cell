package deserializer;

import com.google.gson.*;
import dto.UpdateRequestDto;

import java.lang.reflect.Type;

public class RequestDtoDeserializer implements JsonDeserializer<UpdateRequestDto> {
    @Override
    public UpdateRequestDto deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        int reqId = jsonObject.get("reqId").getAsInt();
        boolean approved = jsonObject.get("approved").getAsBoolean();

        return new UpdateRequestDto(reqId,approved);
    }
}

