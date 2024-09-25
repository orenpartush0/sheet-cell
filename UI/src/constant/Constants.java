package constant;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Constants {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final String BASE_URL  = "http://localhost:8080/sheetcell";

}
