package constant;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import deserializer.*;
import dto.*;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.range.Range;

public class Constants {
    public static final String USER_MANAGER = "userManager";
    public static final String SHEET_MANAGER = "sheetManager";
    public static final String USER_NAME = "userName";
    public static final String SHEET_NAME = "sheetName";

    public static final Gson GSON = new GsonBuilder().
            setPrettyPrinting()
            .registerTypeAdapter(Coordinate.class, new CoordinateDeserializer())
            .registerTypeAdapter(Range.class, new RangeDeserializer())
            .registerTypeAdapter(SheetDto.class, new SheetDtoDeserializer())
            .registerTypeAdapter(SortDto.class, new SortDtoDeserializer())
            .registerTypeAdapter(UpdateCellDto.class, new UpdateCellDtoDeserializer())
            .registerTypeAdapter(UpdateRequestDto.class,new RequestDtoDeserializer())
            .registerTypeAdapter(AddRequestDto.class,new AddRequestDtoDeserializer())
            .create();



    public final static String PUT = "PUT";
    public final static String GET = "GET";
    public final static String DELETE = "DELETE";


    public final static String UPDATE_REQUEST = "/updateRequest";
    public final static String REQUEST_DASHBOARD = "/requestDashBoard";
    public final static String SHEET_DASHBOARD = "/sheetDashboard";
    public final static String ADD_REQUEST = "/addRequest";
    public final static String USER = "/user";
    public final static String SHEET = "/sheet";
}
