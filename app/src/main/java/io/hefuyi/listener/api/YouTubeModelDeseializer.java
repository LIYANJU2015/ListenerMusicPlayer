package io.hefuyi.listener.api;

import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import io.hefuyi.listener.mvp.model.YouTubeModel;

/**
 * Created by liyanju on 2017/12/12.
 */

public class YouTubeModelDeseializer implements JsonDeserializer<YouTubeModel> {

    @Override
    public YouTubeModel deserialize(JsonElement json, Type typeOfT,
                                    JsonDeserializationContext context)
            throws JsonParseException {
        YouTubeModel youTubeModel = new YouTubeModel();
        JsonObject contentsJsonObject = json.getAsJsonObject();
        JsonArray contentsJsonArray = contentsJsonObject.get("contents").getAsJsonArray();
        JsonObject contentsJsonA2 = contentsJsonArray.get(0).getAsJsonObject();
        JsonArray contentsJsonArray2 = contentsJsonA2.get("contents").getAsJsonArray();
        for (int i = 0; i < contentsJsonArray2.size(); i++) {
            JsonObject contentJsonObject = contentsJsonArray2.get(i).getAsJsonObject();
            String name = contentJsonObject.get("name").getAsString();
            String style = contentJsonObject.get("style").getAsString();
            JsonArray content2Ja = contentJsonObject.get("contents").getAsJsonArray();

            YouTubeModel.Title title = new YouTubeModel.Title(style, name);
            youTubeModel.youTubeItems.add(title);

            ArrayList<YouTubeModel.YouTubeContent> youTubeContents = new ArrayList<>();
            for (int j = 0; j < content2Ja.size(); j++) {
                JsonObject content2JO = content2Ja.get(j).getAsJsonObject();
                YouTubeModel.YouTubeContent content = context.deserialize(content2JO,
                        YouTubeModel.YouTubeContent.class);
                if (TextUtils.isEmpty(content.extra)) {
                    return null;
                }
                youTubeContents.add(content);
            }
            Collections.shuffle(youTubeContents);
            HashMap<YouTubeModel.Title, ArrayList<YouTubeModel.YouTubeContent>> map = new HashMap<>(10);
            map.put(title, youTubeContents);
            youTubeModel.youTubeItems.add(map);
        }
        return youTubeModel;
    }
}
