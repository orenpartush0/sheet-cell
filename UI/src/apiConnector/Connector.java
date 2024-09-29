package apiConnector;

import com.google.gson.reflect.TypeToken;
import constant.Constants;
import dto.FilterDto;
import dto.SheetDto;
import dto.SortDto;
import dto.UpdateCellDto;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import sheetpage.app.AppController;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.range.Range;
import util.HttpClientUtil;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

import static constant.Constants.*;
import static constant.UIConstants.*;


public class Connector {

    public interface RangesCallback {
        void onSuccess(List<Range> ranges);
        void onFailure(Exception e);
    }


    public static void SetSheet(String path) throws Exception {
    }

    public static SheetDto applyFilter(String sheetName ,Range range , Map<Integer, List<String>> filters) throws IOException {
        Response res = HttpClientUtil.runSync(
                FILTER + "?" + SHEET_NAME + "=" + sheetName,
                POST,
                new FilterDto(range,filters)
        );

        return GSON.fromJson(res.body().string(),SheetDto.class);
    }

//    private SheetData GetSheetFromXML(String fileName) throws Exception {
//        File file = new File(fileName);
//        InputStream inputStream = new FileInputStream(file);
//        return SchemBaseJaxb.CreateSheetFromXML(inputStream);
//    }

    public static void UpdateCellByCoordinate(String sheetName ,Coordinate coordinate, String newValue) throws NumberFormatException, IOException {
        Response res = HttpClientUtil.runSync(
                CELL + "?" + SHEET_NAME + "=" + sheetName,
                PUT,
                new UpdateCellDto(coordinate, newValue)
        );

        if (res.code() != 200) {
            AppController.showError(res.body().string());
        }
    }

    public static SheetDto getSheet(String sheetName) throws IOException {
        Response res = HttpClientUtil.runSync(
                SHEET + "?" + SHEET_NAME + "=" + sheetName,
                GET,
                null
        );
        return GSON.fromJson(res.body().string(), SheetDto.class);
    }

    public static SheetDto GetSheetByVersion(int version,String sheetName) throws IOException {
        Response response = HttpClientUtil.runSync(
                GET_SHEET_BY_VERSION + "?" + Constants.SHEET_NAME + "=" + sheetName + "&" + VERSION + "=" + version,
                GET,
                null
        );

        return GSON.fromJson(response.body().string(), SheetDto.class);
    }

    public static void AddRange(Range rangeDto,String sheetName, Callback callback){
        HttpClientUtil.runAsync(
                RANGE + "?" + SHEET_NAME + "=" + sheetName,
                PUT,
                rangeDto,
                new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {}

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        callback.onResponse(call,response);
                    }
                }
        );
    }

    public static void getRanges(String sheetName, RangesCallback callback) {
        HttpClientUtil.runAsync(
                GET_RANGES + "?" + SHEET_NAME + "=" + sheetName,
                GET,
                null,
                new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        // Notify failure through the callback
                        callback.onFailure(e);
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        Type listType = new TypeToken<List<Range>>() {}.getType();
                        List<Range> ranges = GSON.fromJson(response.body().string(), listType);
                        // Notify success through the callback
                        callback.onSuccess(ranges);
                    }
                }
        );
    }

    public static void removeRange(String rangeName,String sheetName,Callback callback) {
        HttpClientUtil.runAsync(
                RANGE + "?" + SHEET_NAME + "=" + sheetName + "&" + RANGE_NAME + "=" + rangeName,
                DELETE,
                null,
                new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        callback.onResponse(call,response);
                    }
                });
    }

    public static SheetDto applySort(Queue<String> cols, Range range,String sheetName) throws IOException {
        Response res = HttpClientUtil.runSync(
                SORT + "?" + SHEET_NAME + "=" + sheetName,
                POST,
                new SortDto(range,cols)
        );

        return GSON.fromJson(res.body().string(), SheetDto.class);
    }

    public static SheetDto applyDynamicCalculate(Coordinate coordinate , String numStr){
        return null;
    }

}
