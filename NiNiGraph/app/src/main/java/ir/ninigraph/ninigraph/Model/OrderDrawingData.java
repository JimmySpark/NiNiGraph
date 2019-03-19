package ir.ninigraph.ninigraph.Model;

import java.util.List;

public class OrderDrawingData {

    public List<WorkModel> work;
    public List<PricesModel> prices;

    public class WorkModel{
        String url1, url2;

        public String getUrl1() {
            return url1;
        }

        public String getUrl2() {
            return url2;
        }
    }

    public class PricesModel{
        public long drawing30, drawing35, drawing50, print, post;
    }
}
