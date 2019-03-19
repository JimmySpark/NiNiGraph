package ir.ninigraph.ninigraph.Server;

import org.json.JSONArray;

import java.util.List;

import ir.ninigraph.ninigraph.Model.DesignerLogin;
import ir.ninigraph.ninigraph.Model.Discount;
import ir.ninigraph.ninigraph.Model.HomePage;
import ir.ninigraph.ninigraph.Model.OrderDrawing;
import ir.ninigraph.ninigraph.Model.OrderDrawingData;
import ir.ninigraph.ninigraph.Model.OrderEdit;
import ir.ninigraph.ninigraph.Model.OrderEditTheme;
import ir.ninigraph.ninigraph.Model.OrderPrintPrices;
import ir.ninigraph.ninigraph.Model.Orders;
import ir.ninigraph.ninigraph.Model.Picture;
import ir.ninigraph.ninigraph.Model.Prices;
import ir.ninigraph.ninigraph.Model.SMS;
import ir.ninigraph.ninigraph.Model.Save;
import ir.ninigraph.ninigraph.Model.Theme;
import ir.ninigraph.ninigraph.Model.ThemeCategory;
import ir.ninigraph.ninigraph.Model.Verification;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @POST("api/home_page.php")
    Call<HomePage> getHomePage(@Body String model);

    //SMS Verification
    @POST("Login/code_send.php")
    @FormUrlEncoded
    Call<List<SMS>> sendSMS(@Field("phone") String phone);

    @POST("Login/code_check.php")
    @FormUrlEncoded
    Call<List<Verification>> checkCode(@Field("phone") String phone, @Field("code") String code);

    @POST("Occasional/occasional_get.php")
    @FormUrlEncoded
    Call<List<Picture>> getOccasional(@Field("id") int id);

    //Order Edit
    @GET("Theme/Category/category_get.php")
    Call<List<ThemeCategory>> getThemeCategory();

    @POST("Theme/theme_get.php")
    @FormUrlEncoded
    Call<List<Theme>> getTheme(@Field("id") int id);

    @GET("Prices/prices_get.php")
    Call<List<Prices>> getPrices();

    @POST("Discount/discount_get.php")
    @FormUrlEncoded
    Call<List<Discount>> applyDiscount(@Field("code") String code);

    @POST("Order/Edit/order_get.php")
    @FormUrlEncoded
    Call<List<OrderEdit>> getOrderEdit(@Field("id") int id);

    @POST("Order/Edit/order_save.php")
    @FormUrlEncoded
    Call<Save> saveOrderEdit(@Field("user_id") int id, @Field("count") int count, @Field("price") long price, @Field("themes") JSONArray themes);

    @POST("Order/Edit/order_theme_get.php")
    Call<OrderEditTheme> getOrderEditTheme(@Body String themes);

    @POST("Order/Edit/picture_get.php")
    Call<Save> uploadThemePicture(@Body String data);

    //Order Drawing
    @GET("Order/Drawing/data_get.php")
    Call<OrderDrawingData> getDrawingData();

    @POST("Order/Drawing/order_save.php")
    Call<DesignerLogin> saveOrderDrawing(@Body String data);

    @POST("Order/Drawing/order_get.php")
    Call<OrderDrawing> getOrderDrawing(@Body String data);

    @POST("Order/Drawing/order_delete.php")
    Call<Save> deleteOrder(@Body String data);

    //Order Print
    @GET("Order/Print/prices_get.php")
    Call<OrderPrintPrices> getPrintPrices();

    @POST("Order/Print/order_save.php")
    Call<Save> saveOrderPrint(@Body String data);

    @POST("Order/Print/order_get.php")
    Call<OrderDrawing> getOrderPrint(@Body String data);

    //Designer Panel
    @POST("User/Designer/login.php")
    @FormUrlEncoded
    Call<DesignerLogin> loginAsDesigner(@Field("user") String user, @Field("pass") String pass);

    @POST("User/Designer/order_get.php")
    Call<Orders> getOrders(@Body String data);
}
