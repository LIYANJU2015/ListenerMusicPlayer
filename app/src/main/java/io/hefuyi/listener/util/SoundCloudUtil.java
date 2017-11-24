package io.hefuyi.listener.util;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by liyanju on 2017/11/18.
 */

public class SoundCloudUtil {

    public static String getClientId() {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("aid", "301fa6b6c195e8a3");
            jSONObject.put("lang", Locale.getDefault().getLanguage());
            jSONObject.put("country", Locale.getDefault().getCountry());
            jSONObject.put("channel", 200);
            jSONObject.put("cversion_number", 83);
            jSONObject.put("cversion_name", "2.1.5");
            jSONObject.put("goid", "1510472516918301fa6b6c195e8a3");
            return Base64.encodeToString(jSONObject.toString().getBytes(), 2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
}
