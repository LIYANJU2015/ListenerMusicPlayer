package io.hefuyi.listener.respository;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import io.hefuyi.listener.mvp.model.HomeSound;

/**
 * Created by liyanju on 2017/11/19.
 */

public class HomeSoundDeserializer implements JsonDeserializer<HomeSound.ContentsBeanX> {


    @Override
    public HomeSound.ContentsBeanX deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        final JsonObject jsonObject = jsonElement.getAsJsonObject();
        int dataType = jsonObject.get("data_type").getAsInt();
        HomeSound.ContentsBeanX contentsBeanX = new HomeSound.ContentsBeanX();
        contentsBeanX.setStyle(jsonObject.get("style").getAsString());
        contentsBeanX.setName(jsonObject.get("name").getAsString());
        contentsBeanX.setId(jsonObject.get("id").getAsInt());
        contentsBeanX.setData_type(jsonObject.get("data_type").getAsInt());
        JsonArray jsonArray = jsonObject.get("contents").getAsJsonArray();

        if (dataType == 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject jsonObject1 = jsonArray.get(i).getAsJsonObject();
                HomeSound.ContentsBean2 contentsBean2 = context.deserialize(jsonObject1, HomeSound.ContentsBean2.class);
                contentsBeanX.contents2.add(contentsBean2);
            }
        } else {
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject jsonObject1 = jsonArray.get(i).getAsJsonObject();
                HomeSound.ContentsBeanX.ContentsBean contentsBean = context.deserialize(jsonObject1, HomeSound.ContentsBeanX.ContentsBean.class);
                contentsBeanX.contents.add(contentsBean);
            }
        }
        return contentsBeanX;
    }
}
