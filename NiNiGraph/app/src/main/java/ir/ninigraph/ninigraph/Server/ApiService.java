package ir.ninigraph.ninigraph.Server;

import java.util.List;

import ir.ninigraph.ninigraph.Model.Ads;
import ir.ninigraph.ninigraph.Model.OccasionalCategory;
import ir.ninigraph.ninigraph.Model.Picture;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    //Picture
    @GET("Slider/slider_get.php")
    Call<List<Picture>> getSliderData();

    //Ads
    @GET("Ads/ads_get.php")
    Call<List<Ads>> getAds();

    //Newest
    @GET("Newest/newest_get.php")
    Call<List<Picture>> getNewest();

    //Occasional
    @GET("Occasional/Category/category_get.php")
    Call<List<OccasionalCategory>> getOccasionalCategory();

    @POST("Occasional/occasional_get.php")
    @FormUrlEncoded
    Call<List<Picture>> getOccasional(@Field("id") int id);
}
