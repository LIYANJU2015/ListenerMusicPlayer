package io.hefuyi.listener.mvp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liyanju on 2017/11/18.
 */

public class HomeSound {

    private int id;
    private String name;
    private String style;
    private int data_type;
    private int type;
    private List<ContentsBeanX> contents = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public int getData_type() {
        return data_type;
    }

    public void setData_type(int data_type) {
        this.data_type = data_type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<ContentsBeanX> getContents() {
        return contents;
    }

    public void setContents(List<ContentsBeanX> contents) {
        this.contents = contents;
    }


    public static class ContentsBean2 {
        private int id;
        private String name;
        private String style;
        private int data_type;
        private int type;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStyle() {
            return style;
        }

        public void setStyle(String style) {
            this.style = style;
        }

        public int getData_type() {
            return data_type;
        }

        public void setData_type(int data_type) {
            this.data_type = data_type;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        private List<ContentsBeanX.ContentsBean> contents;

        public List<ContentsBeanX.ContentsBean> getContents() {
            return contents;
        }

        public void setContents(List<ContentsBeanX.ContentsBean> contents) {
            this.contents = contents;
        }
    }

    public static class ContentsBeanX {


        private int id;
        private String name;
        private String style;
        private int data_type;
        private int type;
        public List<ContentsBean> contents = new ArrayList<>();

        public List<ContentsBean2> contents2 = new ArrayList<>();


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStyle() {
            return style;
        }

        public void setStyle(String style) {
            this.style = style;
        }

        public int getData_type() {
            return data_type;
        }

        public void setData_type(int data_type) {
            this.data_type = data_type;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }


        public static class ContentsBean {
            /**
             * id : 76312
             * source_id : 200302
             * song_name : XXXTENTACION - Fuck Love  (feat. Trippie Redd)
             * singer : XXXTENTACION
             * album_name :
             * album_images : https://i1.sndcdn.com/artworks-Gp90YQmWXXFN-0-large.jpg
             * album_local_images : http://resource.gomocdn.com/soft/micro/module/music/BL2huJ8hwb.jpg
             * song_play_time : 146611
             * song_download_url : https://api.soundcloud.com/tracks/339318387/stream
             * license_url :
             * style : listView
             * reference_id : 294a68cbbcdb8b006c05645625a830b3
             * update_time_in_mills : 1505232985000
             */

            private int id;
            private int source_id;
            private String song_name;
            private String singer;
            private String album_name;
            private String album_images;
            private String album_local_images;
            private String song_play_time;
            private String song_download_url;
            private String license_url;
            private String style;
            private String reference_id;
            private long update_time_in_mills;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getSource_id() {
                return source_id;
            }

            public void setSource_id(int source_id) {
                this.source_id = source_id;
            }

            public String getSong_name() {
                return song_name;
            }

            public void setSong_name(String song_name) {
                this.song_name = song_name;
            }

            public String getSinger() {
                return singer;
            }

            public void setSinger(String singer) {
                this.singer = singer;
            }

            public String getAlbum_name() {
                return album_name;
            }

            public void setAlbum_name(String album_name) {
                this.album_name = album_name;
            }

            public String getAlbum_images() {
                return album_images;
            }

            public void setAlbum_images(String album_images) {
                this.album_images = album_images;
            }

            public String getAlbum_local_images() {
                return album_local_images;
            }

            public void setAlbum_local_images(String album_local_images) {
                this.album_local_images = album_local_images;
            }

            public String getSong_play_time() {
                return song_play_time;
            }

            public void setSong_play_time(String song_play_time) {
                this.song_play_time = song_play_time;
            }

            public String getSong_download_url() {
                return song_download_url;
            }

            public void setSong_download_url(String song_download_url) {
                this.song_download_url = song_download_url;
            }

            public String getLicense_url() {
                return license_url;
            }

            public void setLicense_url(String license_url) {
                this.license_url = license_url;
            }

            public String getStyle() {
                return style;
            }

            public void setStyle(String style) {
                this.style = style;
            }

            public String getReference_id() {
                return reference_id;
            }

            public void setReference_id(String reference_id) {
                this.reference_id = reference_id;
            }

            public long getUpdate_time_in_mills() {
                return update_time_in_mills;
            }

            public void setUpdate_time_in_mills(long update_time_in_mills) {
                this.update_time_in_mills = update_time_in_mills;
            }
        }
    }
}
