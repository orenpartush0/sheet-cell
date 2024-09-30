package sheetpage.app;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.HttpClientUtil;
import java.io.IOException;
import java.util.TimerTask;
import java.util.function.Consumer;

import static constant.Constants.*;
import static constant.UIConstants.GET;

public class NeedToBeUpdatedRefresher extends TimerTask {

    private final Consumer<Boolean> booleanConsumer;
    private final String sheetName;

    public NeedToBeUpdatedRefresher(Consumer<Boolean> booleanConsumer, String sheetName) {
        this.booleanConsumer = booleanConsumer;
        this.sheetName = sheetName;
    }

    @Override
    public void run() {
        HttpClientUtil.runAsync(
                NEED_TO_BE_UPDATED + "?" + SHEET_NAME + "=" + sheetName,
                GET,
                null,
                new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    String jsonArrayOfData = response.body().string();
                    boolean needToBeUpdated = GSON.fromJson(jsonArrayOfData, Boolean.class);
                    booleanConsumer.accept(needToBeUpdated);
                }
                else{
                    System.out.println("failed");
                }

            }
        });
    }
}
