package ir.ninigraph.ninigraph.Model;

import java.util.List;

public class OrderEditTheme {

    public List<order_theme> order_theme;

    public class order_theme {
        public int id;
        public int image_status;
        public String url;

        public int getId() {
            return id;
        }

        public int getImage_status() {
            return image_status;
        }

        public String getUrl() {
            return url;
        }
    }
}
