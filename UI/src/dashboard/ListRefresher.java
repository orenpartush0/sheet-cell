package dashboard;

import constant.Constants;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.HttpClientUtil;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

import static constant.Constants.GSON;
import static constant.UIConstants.GET;


public class ListRefresher<T> extends TimerTask {
    private final Consumer<List<T>> listConsumer;
    private final String endPointAndParameters;
    private final Class<T[]> clazz;

    public ListRefresher(String _endPointAndParameters, Consumer<List<T>> _ListConsumer, Class<T[]> _clazz) {
        listConsumer = _ListConsumer;
        endPointAndParameters = _endPointAndParameters;
        clazz = _clazz;
    }

    @Override
    public void run() {
        HttpClientUtil.runAsync(endPointAndParameters, GET, null, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // handle failure
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonArrayOfData = response.body().string();
                T[] list = GSON.fromJson(jsonArrayOfData, clazz); // Use the generic type for deserialization
                listConsumer.accept(Arrays.asList(list)); // Pass the deserialized list to the consumer
            }
        });
    }

}
