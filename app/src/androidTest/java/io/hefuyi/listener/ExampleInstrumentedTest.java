package io.hefuyi.listener;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

//        assertEquals("io.hefuyi.listener", appContext.getPackageName());
//
//        byte asBytes[] = Base64.decode("eyJhaWQiOiIzMDFmYTZiNmMxOTVlOGEzIiwibGFuZyI6InpoX0NOIiwiY291bnRyeSI6ImNuIiwiY2hhbm5lbCI6MjAwLCJjdmVyc2lvbl9udW1iZXIiOjgzLCJjdmVyc2lvbl9uYW1lIjoiMi4xLjUiLCJnb2lkIjoiMTUxMDQ3MjUxNjkxODMwMWZhNmI2YzE5NWU4YTMifQ%3D%3D",2 );
//
//        Log.v("xx", "decodedecode:: " + new String(asBytes, "utf-8"));

        JSONObject jSONObject = new JSONObject();
        jSONObject.put("aid", "301fa6b6c195e8a3");
        jSONObject.put("lang", "en");
        jSONObject.put("country", "en");
        jSONObject.put("channel", 200);
        jSONObject.put("cversion_number", 83);
        jSONObject.put("cversion_name", "2.1.5");
        jSONObject.put("goid", "1510472516918301fa6b6c195e8a3");
        String string = Base64.encodeToString(jSONObject.toString().getBytes(), 2);

        Log.v("xx", "encodeToString string " + string);
    }
}
