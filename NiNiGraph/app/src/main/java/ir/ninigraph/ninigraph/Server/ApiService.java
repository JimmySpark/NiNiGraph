package ir.ninigraph.ninigraph.Server;

import java.util.List;

import ir.ninigraph.ninigraph.Model.Ads;
import ir.ninigraph.ninigraph.Model.Picture;
import retrofit2.Call;
import retrofit2.http.GET;

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
}
