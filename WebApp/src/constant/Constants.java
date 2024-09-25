package constant;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import deserializer.*;
import dto.RequestDto;
import dto.SheetDto;
import dto.SortDto;
import dto.UpdateCellDto;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.range.Range;

public class Constants {
    public static final String USER_MANAGER = "userManager";
    public static final String SHEET_MANAGER = "sheetManager";
    public static final String USER = "userName";
    public static final String SHEET = "sheetName";

    public static final Gson GSON = new GsonBuilder().
            setPrettyPrinting()
            .registerTypeAdapter(Coordinate.class, new CoordinateDeserializer())
            .registerTypeAdapter(Range.class, new RangeDeserializer())
            .registerTypeAdapter(SheetDto.class, new SheetDtoDeserializer())
            .registerTypeAdapter(SortDto.class, new SortDtoDeserializer())
            .registerTypeAdapter(UpdateCellDto.class, new UpdateCellDtoDeserializer())
            .registerTypeAdapter(RequestDto.class,new RequestDtoDeserializer())
            .create();
}
