package ir.ninigraph.ninigraph.Model;

import java.util.List;

public class HomePage {

    public List<SliderModel> slider;
    public List<AdsModel> ads;
    public List<NewestModel> newest;
    public List<OccasionalModel> occasional;

    public class SliderModel{
        public int id;
        public String url;

        public String getUrl() {
            return url;
        }
    }

    public class AdsModel{
        public int id;
        public String url;
        public int isLinkEnable;
        public String link;

        public String getUrl() {
            return url;
        }

        public int getIsLinkEnable() {
            return isLinkEnable;
        }

        public String getLink() {
            return link;
        }
    }

    public class NewestModel {
        public int id;
        public String url;

        public String getUrl() {
            return url;
        }
    }

    public class OccasionalModel {
        public int id;
        public String title;
        public String url;

        public String getUrl() {
            return url;
        }

        public String getTitle() {
            return title;
        }

        public int getId() {
            return id;
        }
    }
}
