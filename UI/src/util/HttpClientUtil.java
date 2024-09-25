package util;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static constant.Constants.BASE_URL;
import static constant.Constants.GSON;

public class HttpClientUtil {
    private final static OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder().
            cookieJar(new CookieJar() {
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
                    .followRedirects(false)
                    .build();


    public static void runAsync(String endPointsAndParameters, String method, Object objToJson, Callback callback) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = null;

        if (!method.equalsIgnoreCase("GET") && objToJson != null) {
            body = RequestBody.create(mediaType, GSON.toJson(objToJson));
        }

        Request.Builder requestBuilder = new Request.Builder()
                .url(BASE_URL + endPointsAndParameters)
                .method(method, body) // body will be null for GET, automatically handling it correctly
                .addHeader("Content-Type", "application/json");

        // Build the request
        Request request = requestBuilder.build();

        System.out.println(BASE_URL + endPointsAndParameters);

        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);
        call.enqueue(callback);
    }


    public static Response runSync(String endPointsAndParameters,String method,Object objToJson) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType,objToJson == null ? "" : GSON.toJson(objToJson));

        Request request = new Request.Builder()
                .url(BASE_URL + endPointsAndParameters)
                .method(method, body)
                .addHeader("Content-Type", "application/json")
                .build();

        return HTTP_CLIENT.newCall(request).execute();
    }

    public static void shutdown() {
        System.out.println("Shutting down HTTP CLIENT");
        HTTP_CLIENT.dispatcher().executorService().shutdown();
        HTTP_CLIENT.connectionPool().evictAll();
    }
}
