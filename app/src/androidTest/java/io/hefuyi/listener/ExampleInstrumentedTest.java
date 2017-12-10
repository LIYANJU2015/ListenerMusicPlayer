package io.hefuyi.listener;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.junit.runner.RunWith;

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
        try {

//            Document document = Jsoup.connect("https://genius.com/Big-shaq-mans-not-hot-lyrics").get();
//            Element element = document.select("meta").last();
//            String content = element.attr("content");
//            Log.v("xx", "content:: " + content);
//
//            JSONObject jsonObject = new JSONObject(content);
//            JSONObject songJO = jsonObject.getJSONObject("song");
//            String url = songJO.getString("youtube_url");
//            Log.v("xx", "url :: "+ url);
            Document document = Jsoup.connect("https://genius.com/").get();
            Element element = document.select("meta").last();
            String content = element.attr("content");
            Log.v("xx", "content:: " + content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
