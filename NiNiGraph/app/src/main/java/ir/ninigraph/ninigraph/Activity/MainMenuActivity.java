package ir.ninigraph.ninigraph.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.List;

import ir.ninigraph.ninigraph.Adapter.RecyclerAdsAdapter;
import ir.ninigraph.ninigraph.Adapter.RecyclerNewestAdapter;
import ir.ninigraph.ninigraph.Adapter.RecyclerOccasionalCategoryAdapter;
import ir.ninigraph.ninigraph.Adapter.SliderAdapter;
import ir.ninigraph.ninigraph.Model.Ads;
import ir.ninigraph.ninigraph.Model.HomePage;
import ir.ninigraph.ninigraph.Model.OccasionalCategory;
import ir.ninigraph.ninigraph.Model.Picture;
import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Server.ApiClient;
import ir.ninigraph.ninigraph.Server.ApiService;
import ir.ninigraph.ninigraph.Util.NetworkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static ir.ninigraph.ninigraph.Activity.MainActivity.retroInterface;

public class MainMenuActivity extends AppCompatActivity {

    //Values
    Context context = this;
    TextView txt_logo_title;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    public static TextView txt_occasional_title;
    public static ImageView img_back_category;
    public static RecyclerView recycler_occasional, recycler_occasional_category;
    LinearLayout lay_menu, lay_newest, lay_occasional;
    RelativeLayout item_new_order, item_follow_order, item_edit_info, item_support, item_logout, lay_dark_bg;
    boolean isMenuVisible = false;
    RecyclerView recycler_ads, recycler_newest;
    ss.com.bannerslider.Slider slider;
    SwipeRefreshLayout refreshLayout;
    ConstraintLayout lay_parent, lay_no_con;
    Button btn_try_again;
    ProgressBar progress_bar;
    boolean isConnected, doubleBackToExitPressedOnce;

    HomePage homePageModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //Values
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();

        //Views
        txt_logo_title = findViewById(R.id.txt_logo_title);
        lay_menu = findViewById(R.id.lay_menu);
        lay_dark_bg = findViewById(R.id.lay_dark_bg);
        item_new_order = findViewById(R.id.item_new_order);
        item_follow_order = findViewById(R.id.item_follow_order);
        item_edit_info = findViewById(R.id.item_edit_info);
        item_support = findViewById(R.id.item_support);
        item_logout = findViewById(R.id.item_logout);
        slider = findViewById(R.id.slider);
        recycler_ads = findViewById(R.id.recycler_ads);
        recycler_newest = findViewById(R.id.recycler_newest);
        txt_occasional_title = findViewById(R.id.txt_occasional_title);
        recycler_occasional = findViewById(R.id.recycler_occasional);
        recycler_occasional_category = findViewById(R.id.recycler_occasional_category);
        refreshLayout = findViewById(R.id.refresh_layout);
        lay_parent = findViewById(R.id.lay_parent);
        lay_no_con = findViewById(R.id.lay_no_con);
        btn_try_again = findViewById(R.id.btn_try_again);
        img_back_category = findViewById(R.id.img_back_category);
        progress_bar = findViewById(R.id.progress_bar);
        lay_newest = findViewById(R.id.lay_newest);
        lay_occasional = findViewById(R.id.lay_occasional);


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

                            hideMenuItems();
                            showNewOrderDialog();
                        }
                    });

                    item_follow_order.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            hideMenuItems();
                            startActivity(new Intent(context, FollowOrderActivity.class));
                        }
                    });

                    item_edit_info.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            hideMenuItems();
                            Intent intent = new Intent(context, PersonalInfoActivity.class);
                            intent.putExtra("edit_mode", true);
                            startActivity(intent);
                        }
                    });

                    item_support.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            hideMenuItems();
                            showSupportDialog(context);
                        }
                    });

                    item_logout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            hideMenuItems();
                            editor.putBoolean("login", false).apply();
                            startActivity(new Intent(context, MainActivity.class));
                            finish();
                        }
                    });

                    lay_dark_bg.setOnClickListener(new View.OnClickListener() {
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
    private void showMenuItems() {

        isMenuVisible = true;
        item_new_order.setVisibility(View.VISIBLE);
        item_follow_order.setVisibility(View.VISIBLE);
        item_edit_info.setVisibility(View.VISIBLE);
        item_support.setVisibility(View.VISIBLE);
        item_logout.setVisibility(View.VISIBLE);
        lay_dark_bg.setVisibility(View.VISIBLE);

        //..Start Animations
        item_new_order.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_item_new_order));
        item_follow_order.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_item_follow_order));
        item_edit_info.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_item_edit_info));
        item_support.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_item_support));
        item_logout.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_item_logout));
    }

    private void hideMenuItems() {

        isMenuVisible = false;
        item_new_order.setVisibility(View.GONE);
        item_follow_order.setVisibility(View.GONE);
        item_edit_info.setVisibility(View.GONE);
        item_support.setVisibility(View.GONE);
        item_logout.setVisibility(View.GONE);
        lay_dark_bg.setVisibility(View.GONE);

        //..Start Animations
        item_new_order.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_items));
        item_follow_order.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_items));
        item_edit_info.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_items));
        item_support.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_items));
        item_logout.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_items));
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

                if (preferences.getBoolean("isInfoEntered", false))

                    startActivity(new Intent(context, NewEditOrderActivity.class));
                else
                    remindEnterInfo();
            }
        });
        //Drawing
        btn_drawing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

                if (preferences.getBoolean("isInfoEntered", false))

                    startActivity(new Intent(context, NewDrawingOrderActivity.class));
                else
                    remindEnterInfo();
            }
        });
        //Print
        btn_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

                if (preferences.getBoolean("isInfoEntered", false))

                    Toast.makeText(context, "چاپ", Toast.LENGTH_SHORT).show();
                else
                    remindEnterInfo();
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

            lay_parent.setVisibility(View.GONE);
            lay_no_con.setVisibility(View.VISIBLE);
        } else {

            lay_parent.setVisibility(View.VISIBLE);
            lay_no_con.setVisibility(View.GONE);
            img_back_category.setVisibility(View.GONE);
            txt_occasional_title.setText("مناسبتی ها");
            recycler_occasional.setVisibility(View.GONE);
            recycler_occasional_category.setVisibility(View.VISIBLE);
            progress_bar.setVisibility(View.VISIBLE);
            slider.setVisibility(View.GONE);
            recycler_ads.setVisibility(View.GONE);
            lay_newest.setVisibility(View.GONE);
            lay_occasional.setVisibility(View.GONE);

            getHomePage(new Runnable() {
                @Override
                public void run() {
                    slider.setAdapter(new SliderAdapter(context, homePageModel.slider));
                    slider.setInterval(5000);

                    recycler_newest.setLayoutManager(new LinearLayoutManager(
                            context, LinearLayoutManager.HORIZONTAL, false));
                    recycler_occasional_category.setLayoutManager(new GridLayoutManager(context, 2));

                    recycler_ads.setLayoutManager(new LinearLayoutManager(
                            getApplicationContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false
                    ));

                    recycler_ads.setAdapter(new RecyclerAdsAdapter(context, homePageModel.ads));

                    recycler_newest.setAdapter(new RecyclerNewestAdapter(context, homePageModel.news));

                    recycler_occasional_category.setAdapter(new RecyclerOccasionalCategoryAdapter(
                            context, homePageModel.occasional));
                }
            });
        }
    }

    private void remindEnterInfo() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage("اطلاعات کاربری شما کامل نیست، برای ثبت سفارش نیاز به تکمیل آن دارید");
        builder.setPositiveButton("باشه، کامل میکنم", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                startActivity(new Intent(context, PersonalInfoActivity.class));
            }
        });
        builder.setNegativeButton("بعدا کامل میکنم", null);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dialog));
        dialog.show();

        //Change Layout
        Display display = (getWindowManager().getDefaultDisplay());
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        width = (int) ((width) * (((double) 4 / 5)));
        dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public void getHomePage(final Runnable runnable) {
        JSONObject input = new JSONObject();
        try {
            input.put("mode", "HOME_PAGE");
        } catch (Exception ignored) {
        }

        retroInterface.getHomePage(input.toString()).enqueue(new Callback<HomePage>() {
            @Override
            public void onResponse(@NonNull Call<HomePage> call, @NonNull Response<HomePage> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        progress_bar.setVisibility(View.GONE);
                        slider.setVisibility(View.VISIBLE);
                        recycler_ads.setVisibility(View.VISIBLE);
                        lay_newest.setVisibility(View.VISIBLE);
                        lay_occasional.setVisibility(View.VISIBLE);

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

    //Override Methods
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
}
