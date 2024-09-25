package deserializer;

import com.google.gson.*;
import dto.RequestDto;

import java.lang.reflect.Type;

public class RequestDtoDeserializer implements JsonDeserializer<RequestDto> {
    @Override
    public RequestDto deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonObject rangeJason = jsonObject.getAsJsonObject("requestDto");
        int reqId = rangeJason.get("reqId").getAsInt();
        boolean approved = rangeJason.get("approved").getAsBoolean();

        return new RequestDto(reqId,approved);
    }
}
