package ir.ninigraph.ninigraph.Model;

import java.util.List;

public class OrderPrintPrices {

    public List<OrderPrintPrices.PricesModel> prices;

    public class PricesModel{
        public long print_A4, print_A5, print_A6, print_chassis, post;

        public long getPrint_A4() {
            return print_A4;
        }

        public long getPrint_A5() {
            return print_A5;
        }

        public long getPrint_A6() {
            return print_A6;
        }

        public long getPrint_chassis() {
            return print_chassis;
        }

        public long getPost() {
            return post;
        }
    }
}
