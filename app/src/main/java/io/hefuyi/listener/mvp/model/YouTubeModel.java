package io.hefuyi.listener.mvp.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by liyanju on 2017/12/12.
 */

public class YouTubeModel implements Serializable {


    public ArrayList<Object> youTubeItems = new ArrayList<>();

    public static class YouTubeContent implements Serializable {
        public String style;
        public String name;
        public int data_type;
        public String extra;
        public int type;
        public int id;
        public String icon;
    }

    public static class Title implements Serializable {

        public Title(String style, String name) {
            this.style = style;
            this.name = name;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }

            if (obj instanceof Title && ((Title) obj).name.equals(name)) {
                return true;
            }

            return false;
        }

        public String style;

        public String name = "";
    }
}
