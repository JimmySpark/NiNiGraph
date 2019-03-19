package ir.ninigraph.ninigraph.Model;

import java.util.List;

public class OrderDrawing {

    public List<OrderDrawingModel> order;

    public class OrderDrawingModel {
        public int id, size, status;
        public long price;
        public String date, time;

        public int getId() {
            return id;
        }

        public int getSize() {
            return size;
        }

        public int getStatus() {
            return status;
        }

        public long getPrice() {
            return price;
        }

        public String getDate() {
            return date;
        }

        public String getTime() {
            return time;
        }
    }
}
