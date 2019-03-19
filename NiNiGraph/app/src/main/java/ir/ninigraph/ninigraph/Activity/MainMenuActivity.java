package ir.ninigraph.ninigraph.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import ir.ninigraph.ninigraph.Adapter.RecyclerAdsAdapter;
import ir.ninigraph.ninigraph.Adapter.RecyclerNewestAdapter;
import ir.ninigraph.ninigraph.Adapter.RecyclerOccasionalCategoryAdapter;
import ir.ninigraph.ninigraph.Adapter.SliderAdapter;
import ir.ninigraph.ninigraph.Model.HomePage;
import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Util.NetworkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ss.com.bannerslider.Slider;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static ir.ninigraph.ninigraph.Activity.MainActivity.apiService;

public class MainMenuActivity extends AppCompatActivity {

    //Values
    Context context = this;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    public static TextView txtOccasionalTitle;
    public static ImageView imgBackCategory;
    public static RecyclerView recyclerOccasional;
    public static RecyclerView recyclerOccasionalCategory;
    private TextView txtLogoTitle;
    private ConstraintLayout layParent;
    private SwipeRefreshLayout refreshLayout;
    private Slider slider;
    private RecyclerView recyclerAds;
    private LinearLayout layNewest;
    private RecyclerView recyclerNewest;
    private LinearLayout layOccasional;
    private ProgressBar progressBar;
    private RelativeLayout layDarkBg;
    private RelativeLayout itemNewOrder;
    private RelativeLayout itemFollowOrder;
    private RelativeLayout itemSupport;
    private RelativeLayout itemLogout;
    private LinearLayout layMenu;
    private ConstraintLayout layNoCon;
    private Button btnTryAgain;
    boolean isMenuVisible = false;
    boolean isConnected, doubleBackToExitPressedOnce;

    HomePage homePageModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        initView();

        //Values
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();


        //Change Font
        Typeface font = Typeface.createFromAsset(getAssets(), "font/Lalezar-Regular.ttf");
        txtLogoTitle.setTypeface(font);

        //Show Menu Items
        layMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isMenuVisible) {

                    showMenuItems();
                    itemNewOrder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            hideMenuItems();
                            showNewOrderDialog();
                        }
                    });

                    itemFollowOrder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            hideMenuItems();
                            startActivity(new Intent(context, FollowOrderActivity.class));
                        }
                    });

                    itemSupport.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            hideMenuItems();
                            showSupportDialog(context);
                        }
                    });

                    itemLogout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            hideMenuItems();
                            editor.putBoolean("login", false).apply();
                            startActivity(new Intent(context, MainActivity.class));
                            finish();
                        }
                    });

                    layDarkBg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            hideMenuItems();
                        }
                    });
                } else {

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

                btnTryAgain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        checkConnection();
                    }
                });
            }
        });


        //Check Connection
        checkConnection();

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkConnection();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {

        if (isMenuVisible)
            hideMenuItems();
        else if (doubleBackToExitPressedOnce) {

            //Exit App
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        } else {
            doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "برای خروج دوباره لمس کنید.", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    //Classes
    private void initView() {
        layParent = findViewById(R.id.lay_parent);
        recyclerAds = findViewById(R.id.recycler_ads);
        layNewest = findViewById(R.id.lay_newest);
        recyclerNewest = findViewById(R.id.recycler_newest);
        layOccasional = findViewById(R.id.lay_occasional);
        imgBackCategory = findViewById(R.id.img_back_category);
        txtOccasionalTitle = findViewById(R.id.txt_occasional_title);
        recyclerOccasionalCategory = findViewById(R.id.recycler_occasional_category);
        recyclerOccasional = findViewById(R.id.recycler_occasional);
        progressBar = findViewById(R.id.progress_bar);
        layDarkBg = findViewById(R.id.lay_dark_bg);
        itemNewOrder = findViewById(R.id.item_new_order);
        itemFollowOrder = findViewById(R.id.item_follow_order);
        itemSupport = findViewById(R.id.item_support);
        itemLogout = findViewById(R.id.item_logout);
        layMenu = findViewById(R.id.lay_menu);
        layNoCon = findViewById(R.id.lay_no_con);
        btnTryAgain = findViewById(R.id.btn_try_again);
        txtLogoTitle = findViewById(R.id.txt_logo_title);
        refreshLayout = findViewById(R.id.refresh_layout);
        slider = findViewById(R.id.slider);
    }

    private void showMenuItems() {

        isMenuVisible = true;
        itemNewOrder.setVisibility(View.VISIBLE);
        itemFollowOrder.setVisibility(View.VISIBLE);
        itemSupport.setVisibility(View.VISIBLE);
        itemLogout.setVisibility(View.VISIBLE);
        layDarkBg.setVisibility(View.VISIBLE);

        //..Start Animations
        itemNewOrder.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_item_new_order));
        itemFollowOrder.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_item_follow_order));
        itemSupport.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_item_support));
        itemLogout.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_item_logout));
    }

    private void hideMenuItems() {

        isMenuVisible = false;
        itemNewOrder.setVisibility(View.GONE);
        itemFollowOrder.setVisibility(View.GONE);
        itemSupport.setVisibility(View.GONE);
        itemLogout.setVisibility(View.GONE);
        layDarkBg.setVisibility(View.GONE);

        //..Start Animations
        itemNewOrder.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_items));
        itemFollowOrder.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_items));
        itemSupport.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_items));
        itemLogout.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_items));
    }

    private void showNewOrderDialog() {

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_new_order, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        RelativeLayout btn_edit = view.findViewById(R.id.btn_edit);
        RelativeLayout btn_drawing = view.findViewById(R.id.btn_drawing);
        RelativeLayout btn_print = view.findViewById(R.id.btn_print);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dialog));
        dialog.show();

        //Edit
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                startActivity(new Intent(context, NewEditOrderActivity.class));
            }
        });
        //Drawing
        btn_drawing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*dialog.dismiss();
                startActivity(new Intent(context, NewDrawingOrderActivity.class));*/
                Toast.makeText(context, "به زودی", Toast.LENGTH_SHORT).show();
            }
        });
        //Print
        btn_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*dialog.dismiss();
                startActivity(new Intent(context, NewPrintOrderActivity.class));*/
                Toast.makeText(context, "به زودی", Toast.LENGTH_SHORT).show();
            }
        });

        //Change Layout
        Display display = (getWindowManager().getDefaultDisplay());
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        width = (int) ((width) * (((double) 4 / 5)));
        dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void checkConnection() {

        isConnected = NetworkUtil.isConnected(context);

        if (!isConnected) {

            layParent.setVisibility(View.GONE);
            layNoCon.setVisibility(View.VISIBLE);
        } else {

            layParent.setVisibility(View.VISIBLE);
            layNoCon.setVisibility(View.GONE);
            imgBackCategory.setVisibility(View.GONE);
            txtOccasionalTitle.setText("مناسبتی ها");
            recyclerOccasional.setVisibility(View.GONE);
            recyclerOccasionalCategory.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            slider.setVisibility(View.GONE);
            recyclerAds.setVisibility(View.GONE);
            layNewest.setVisibility(View.GONE);
            layOccasional.setVisibility(View.GONE);

            getHomePage(new Runnable() {
                @Override
                public void run() {
                    slider.setAdapter(new SliderAdapter(context, homePageModel.slider));
                    slider.setInterval(5000);

                    recyclerNewest.setLayoutManager(new LinearLayoutManager(
                            context, LinearLayoutManager.HORIZONTAL, false));
                    recyclerOccasionalCategory.setLayoutManager(new GridLayoutManager(context, 2));

                    recyclerAds.setLayoutManager(new LinearLayoutManager(
                            getApplicationContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false
                    ));

                    recyclerAds.setAdapter(new RecyclerAdsAdapter(context, homePageModel.ads));

                    recyclerNewest.setAdapter(new RecyclerNewestAdapter(context, homePageModel.newest));

                    recyclerOccasionalCategory.setAdapter(new RecyclerOccasionalCategoryAdapter(
                            context, homePageModel.occasional));
                }
            });
        }
    }

    public void getHomePage(final Runnable runnable) {
        JSONObject input = new JSONObject();
        try {
            input.put("mode", "HOME_PAGE");
        } catch (Exception ignored) {
        }

        apiService.getHomePage(input.toString()).enqueue(new Callback<HomePage>() {
            @Override
            public void onResponse(@NonNull Call<HomePage> call, @NonNull Response<HomePage> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        progressBar.setVisibility(View.GONE);
                        slider.setVisibility(View.VISIBLE);
                        recyclerAds.setVisibility(View.VISIBLE);
                        layNewest.setVisibility(View.VISIBLE);
                        layOccasional.setVisibility(View.VISIBLE);

                        homePageModel = response.body();
                        runnable.run();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<HomePage> call, @NonNull Throwable t) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showSupportDialog(Context context) {

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_support, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setView(view);

        TextView title = view.findViewById(R.id.title);
        RelativeLayout btn_call = view.findViewById(R.id.btn_call);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dialog_support));
        Typeface font = Typeface.createFromAsset(getAssets(), "font/Vazir-Bold-FD-WOL.ttf");
        title.setTypeface(font);
        dialog.show();

        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Intent dial = new Intent(Intent.ACTION_DIAL);
                dial.setData(Uri.parse("tel:09397996639"));
                startActivity(dial);
            }
        });

        //Change Layout
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        width = (int) ((width) * (((double) 4 / 5)));
        dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
}
