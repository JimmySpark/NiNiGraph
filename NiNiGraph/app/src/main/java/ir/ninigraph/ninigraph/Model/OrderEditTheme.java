package ir.ninigraph.ninigraph.Model;

import java.util.List;

public class OrderEditTheme {

    public List<orderThemeModel> order_theme;

    public class orderThemeModel {
        public int id;
        public String image;
        public int image_status;
        public String url;

        public int getId() {
            return id;
        }

        public String getImage() {
            return image;
        }

        public int getImage_status() {
            return image_status;
        }

        public String getUrl() {
            return url;
        }
    }
}
