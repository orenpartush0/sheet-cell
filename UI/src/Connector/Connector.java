package Connector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.CellDto;
import dto.SheetDto;
import okhttp3.*;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.range.Range;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Queue;


public interface Connector {

    String BASE_URL  = "http://localhost:8080/sheetcell";
    OkHttpClient client = new OkHttpClient().newBuilder().build();
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    static boolean setNewSheet(SheetDto sheetDto, String user) throws IOException {
        String json = gson.toJson(sheetDto);

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, json); // Use the JSON string

        Request request = new Request.Builder()
                .url(BASE_URL + "/sheet?userName=" + user)
                .method("PUT", body)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = client.newCall(request).execute();
        return response.isSuccessful();
    }

    static void SetSheet(String path) throws Exception {
    }

    static Map<Integer, List<String>> getValuesInColumn(Range range) {
        return null;
    }

    static SheetDto applyFilter(Range range , Map<Integer, List<String>> filters) {
        return null;
    }

//    private SheetData GetSheetFromXML(String fileName) throws Exception {
//        File file = new File(fileName);
//        InputStream inputStream = new FileInputStream(file);
//        return SchemBaseJaxb.CreateSheetFromXML(inputStream);
//    }

    static void UpdateCellByCoordinate(Coordinate coordinate, String newValue) throws NumberFormatException{
    }

    static SheetDto getSheet(){
        return null;
    }

    static CellDto GetCellByCoordinate(Coordinate coordinate){
        return null;
    }

    static SheetDto GetSheetByVersion(int version){
        return null;
    }

    static void AddRange(Range rangeDto){
    }

    static Range GetRangeDto(String rangeName){
        return null;
    }

    static List<Range> getRanges(){
        return null;
    }

    static void removeRange(String rangeName) throws Exception{
    }

    static SheetDto applySort(Queue<String> cols, Range range){
        return null;
    }

    static SheetDto applyDynamicCalculate(Coordinate coordinate , String numStr){
        return null;
    }

    static boolean Login(String userName) throws IOException {
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url(BASE_URL + "/putUser?userName=" + userName)
                .method("PUT", body)
                .build();
        Response response = client.newCall(request).execute();
        return response.isSuccessful();
    }
}
