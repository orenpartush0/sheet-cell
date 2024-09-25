package Connector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.CellDto;
import dto.SheetDto;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.range.Range;
import util.HttpClientUtil;

import java.io.IOException;
import java.util.*;


public class Connector {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final OkHttpClient  client = new OkHttpClient.Builder()
            .cookieJar(new CookieJar() {
                private final List<Cookie> cookies = new ArrayList<>();

                @Override
                public void saveFromResponse(@NotNull HttpUrl url, @NotNull List<Cookie> cookies) {
                    this.cookies.addAll(cookies);
                }

                @NotNull
                @Override
                public List<Cookie> loadForRequest(@NotNull HttpUrl url) {
                    return cookies;
                }
            })
            .build();


    public static void SetSheet(String path) throws Exception {
    }

    public static Map<Integer, List<String>> getValuesInColumn(Range range) {
        return null;
    }

    public static SheetDto applyFilter(Range range , Map<Integer, List<String>> filters) {
        return null;
    }

//    private SheetData GetSheetFromXML(String fileName) throws Exception {
//        File file = new File(fileName);
//        InputStream inputStream = new FileInputStream(file);
//        return SchemBaseJaxb.CreateSheetFromXML(inputStream);
//    }

    public static void UpdateCellByCoordinate(Coordinate coordinate, String newValue) throws NumberFormatException{
    }

    public static SheetDto getSheet()
    {
        return null;
    }

    public static CellDto GetCellByCoordinate(Coordinate coordinate){
        return null;
    }

    public static SheetDto GetSheetByVersion(int version){
        return null;
    }

    public static void AddRange(Range rangeDto){
    }

    public static Range GetRangeDto(String rangeName){
        return null;
    }

    public static List<Range> getRanges(){
        return null;
    }

    public static void removeRange(String rangeName) throws Exception{
    }

    public static SheetDto applySort(Queue<String> cols, Range range){
        return null;
    }

    public static SheetDto applyDynamicCalculate(Coordinate coordinate , String numStr){
        return null;
    }

}
