package ir.ninigraph.ninigraph.Model;

import java.util.List;

public class Orders {

    public List<OrderEditModel> orderEdit;
    public List<OrderEditThemeModel> orderEditTheme;
    public List<OrderDrawingModel> orderDrawing;
    boolean result;

    public class OrderEditModel{

        int id, count, status, doing_expire, accepting_expire;

        public int getId() {
            return id;
        }

        public int getCount() {
            return count;
        }

        public int getStatus() {
            return status;
        }

        public int getDoing_expire() {
            return doing_expire;
        }

        public int getAccepting_expire() {
            return accepting_expire;
        }
    }

    public class OrderEditThemeModel{

        int id;
        String url;

        public int getId() {
            return id;
        }

        public String getUrl() {
            return url;
        }
    }

    public class OrderDrawingModel{

        int id, size, status, doing_expire, accepting_expire;
        String image_url;

        public int getId() {
            return id;
        }

        public int getSize() {
            return size;
        }

        public int getStatus() {
            return status;
        }

        public int getDoing_expire() {
            return doing_expire;
        }

        public int getAccepting_expire() {
            return accepting_expire;
        }

        public String getImage_url() {
            return image_url;
        }
    }

    public boolean isResult() {
        return result;
    }
}
