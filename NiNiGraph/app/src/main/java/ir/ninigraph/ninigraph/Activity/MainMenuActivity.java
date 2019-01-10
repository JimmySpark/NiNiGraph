package ir.ninigraph.ninigraph.Activity;

import android.content.Context;
import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ir.ninigraph.ninigraph.Adapter.RecyclerAdsAdapter;
import ir.ninigraph.ninigraph.Adapter.RecyclerNewestAdapter;
import ir.ninigraph.ninigraph.Adapter.SliderAdapter;
import ir.ninigraph.ninigraph.Model.Ads;
import ir.ninigraph.ninigraph.Model.Picture;
import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Server.ApiClient;
import ir.ninigraph.ninigraph.Server.ApiService;
import ir.ninigraph.ninigraph.Util.NetworkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainMenuActivity extends AppCompatActivity {

    //Values
    Context context = this;
    TextView txt_logo_title;
    LinearLayout lay_menu;
    RelativeLayout item_new_order, item_follow_order, item_edit_info, item_support;
    boolean isMenuVisible = false;
    RecyclerView recycler_ads, recycler_newest;
    ss.com.bannerslider.Slider slider;
    SwipeRefreshLayout refreshLayout;
    ConstraintLayout lay_main_menu, lay_no_con;
    Button btn_try_again;
    boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //Views
        txt_logo_title = findViewById(R.id.txt_logo_title);
        lay_menu = findViewById(R.id.lay_menu);
        item_new_order = findViewById(R.id.item_new_order);
        item_follow_order = findViewById(R.id.item_follow_order);
        item_edit_info = findViewById(R.id.item_edit_info);
        item_support = findViewById(R.id.item_support);
        recycler_ads = findViewById(R.id.recycler_ads);
        recycler_newest = findViewById(R.id.recycler_newest);
        refreshLayout = findViewById(R.id.refresh_layout);
        lay_main_menu = findViewById(R.id.lay_main_menu);
        lay_no_con = findViewById(R.id.lay_no_con);
        btn_try_again = findViewById(R.id.btn_try_again);


        //Change Font
        Typeface font = Typeface.createFromAsset(getAssets(), "font/Lalezar-Regular.ttf");
        txt_logo_title.setTypeface(font);

        //Show Menu Items
        lay_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isMenuVisible) {

                    showMenuItems();
                    item_new_order.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Toast.makeText(MainMenuActivity.this, "ثبت سفارش", Toast.LENGTH_SHORT).show();
                            hideMenuItems();
                        }
                    });

                    item_follow_order.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Toast.makeText(MainMenuActivity.this, "پیگیری سفارش", Toast.LENGTH_SHORT).show();
                            hideMenuItems();
                        }
                    });

                    item_edit_info.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Toast.makeText(MainMenuActivity.this, "ویرایش اطلاعات", Toast.LENGTH_SHORT).show();
                            hideMenuItems();
                        }
                    });

                    item_support.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Toast.makeText(MainMenuActivity.this, "پشتیبانی و تماس با ما", Toast.LENGTH_SHORT).show();
                            hideMenuItems();
                        }
                    });
                }
                else {

                    hideMenuItems();
                }
            }
        });

        //Refresh
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refreshLayout.setRefreshing(false);
                checkConnection();

                btn_try_again.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        checkConnection();
                    }
                });
            }
        });


        //On Create
        checkConnection();

        btn_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkConnection();
            }
        });
    }

    //Classes
    private void showMenuItems(){

        isMenuVisible = true;
        item_new_order.setVisibility(View.VISIBLE);
        item_follow_order.setVisibility(View.VISIBLE);
        item_edit_info.setVisibility(View.VISIBLE);
        item_support.setVisibility(View.VISIBLE);

        //..Start Animations
        item_new_order.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_item_new_order));
        item_follow_order.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_item_follow_order));
        item_edit_info.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_item_edit_info));
        item_support.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_item_support));
    }
    private void hideMenuItems(){

        isMenuVisible = false;
        item_new_order.setVisibility(View.GONE);
        item_follow_order.setVisibility(View.GONE);
        item_edit_info.setVisibility(View.GONE);
        item_support.setVisibility(View.GONE);

        //..Start Animations
        item_new_order.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_items));
        item_follow_order.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_items));
        item_edit_info.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_items));
        item_support.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_items));
    }
    private void getSliderData(){

        ApiService apiService = ApiClient.getApi().create(ApiService.class);
        Call<List<Picture>> call = apiService.getSliderData();

        //call.request().cacheControl().noCache();
        call.enqueue(new Callback<List<Picture>>() {
            @Override
            public void onResponse(Call<List<Picture>> call, Response<List<Picture>> response) {

                if (response.isSuccessful()){
                    if (response.body() != null){

                        slider.setAdapter(new SliderAdapter(context, response.body()));
                        slider.setInterval(5000);

                        //response.body().clear();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Picture>> call, Throwable t) {
                Toast.makeText(MainMenuActivity.this, "خطا در دریافت اطلاعات از سرور", Toast.LENGTH_SHORT).show();
            }
        });

        //call.cancel();
    }
    private void getAds(){

        ApiService apiService = ApiClient.getApi().create(ApiService.class);
        Call<List<Ads>> call = apiService.getAds();

        call.enqueue(new Callback<List<Ads>>() {
            @Override
            public void onResponse(Call<List<Ads>> call, Response<List<Ads>> response) {

                if (response.isSuccessful() && response.body() != null){

                    recycler_ads.setLayoutManager(new LinearLayoutManager(
                            getApplicationContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false
                    ));
                    recycler_ads.setAdapter(new RecyclerAdsAdapter(
                            context,
                            response.body()
                    ));
                }
            }

            @Override
            public void onFailure(Call<List<Ads>> call, Throwable t) {

            }
        });
    }
    private void getNewest(){

        ApiService apiService = ApiClient.getApi().create(ApiService.class);
        Call<List<Picture>> call = apiService.getNewest();

        call.enqueue(new Callback<List<Picture>>() {
            @Override
            public void onResponse(Call<List<Picture>> call, Response<List<Picture>> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){

                        recycler_newest.setLayoutManager(new LinearLayoutManager(
                                context,
                                LinearLayoutManager.HORIZONTAL,
                                false
                        ));
                        recycler_newest.setAdapter(new RecyclerNewestAdapter(
                                context,
                                response.body()
                        ));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Picture>> call, Throwable t) {

            }
        });
    }
    private void checkConnection(){

        isConnected = NetworkUtil.isConnected(context);

        if (!isConnected){

            lay_main_menu.setVisibility(View.GONE);
            lay_no_con.setVisibility(View.VISIBLE);
        }
        else {

            lay_main_menu.setVisibility(View.VISIBLE);
            lay_no_con.setVisibility(View.GONE);
            slider = findViewById(R.id.slider);
            getSliderData();
            getAds();
            getNewest();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {

        if (isMenuVisible)
            hideMenuItems();
        else
            super.onBackPressed();
    }
}
