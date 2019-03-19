package ir.ninigraph.ninigraph.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ir.ninigraph.ninigraph.Adapter.RecyclerOrdersAdapter1;
import ir.ninigraph.ninigraph.Adapter.RecyclerOrdersAdapter2;
import ir.ninigraph.ninigraph.Model.DesignerLogin;
import ir.ninigraph.ninigraph.Model.Orders;
import ir.ninigraph.ninigraph.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static ir.ninigraph.ninigraph.Activity.MainActivity.apiService;

public class DesignerPanelActivity extends AppCompatActivity {

    Context context = this;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private LinearLayout layLogout;
    private RecyclerView recyclerOrders;
    private FloatingActionButton fabProfile;
    private FloatingActionButton fabChangeOrder;
    private ProgressBar progressBar;
    private TextView txtNoOrder;
    int id;
    boolean doubleBackToExitPressedOnce;
    List<Orders.OrderEditModel> orderEditList;
    List<Orders.OrderDrawingModel> orderDrawingList;
    Runnable editRun, drawingRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_designer_panel);
        initView();

        //Values
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        id = preferences.getInt("designer_id", 0);
        editRun = new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                recyclerOrders.setLayoutManager(new LinearLayoutManager(context));
                recyclerOrders.setAdapter(new RecyclerOrdersAdapter1(context, orderEditList));
                recyclerOrders.setVisibility(View.VISIBLE);
            }
        };
        drawingRun = new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                recyclerOrders.setLayoutManager(new LinearLayoutManager(context));
                recyclerOrders.setAdapter(new RecyclerOrdersAdapter2(context, orderDrawingList));
                recyclerOrders.setVisibility(View.VISIBLE);
            }
        };

        if (preferences.getBoolean("dLogin", false)){

            //Get Data
            switch (preferences.getInt("type", 1)) {

                case 1:
                    fabChangeOrder.setImageResource(R.drawable.icon_drawing);
                    progressBar.setVisibility(View.VISIBLE);
                    recyclerOrders.setVisibility(View.GONE);
                    getOrderDrawings(drawingRun);
                    break;
                case 2:
                    fabChangeOrder.setImageResource(R.drawable.icon_edit_image);
                    progressBar.setVisibility(View.VISIBLE);
                    recyclerOrders.setVisibility(View.GONE);
                    getOrderEdits(editRun);
                    break;
            }

            //Change Order Type
            fabChangeOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    switch (preferences.getInt("type", 1)) {

                        case 1:
                            editor.putInt("type", 2).apply();
                            fabChangeOrder.setImageResource(R.drawable.icon_edit_image);
                            progressBar.setVisibility(View.VISIBLE);
                            recyclerOrders.setVisibility(View.GONE);
                            getOrderEdits(editRun);
                            break;
                        case 2:
                            editor.putInt("type", 1).apply();
                            fabChangeOrder.setImageResource(R.drawable.icon_drawing);
                            progressBar.setVisibility(View.VISIBLE);
                            recyclerOrders.setVisibility(View.GONE);
                            getOrderDrawings(drawingRun);
                            break;
                    }
                }
            });

            //Logout
            layLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    editor.putBoolean("dLogin", false).apply();
                    startActivity(new Intent(context, DesignerLoginActivity.class));
                }
            });
        }
        else
            startActivity(new Intent(context, DesignerLoginActivity.class));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {

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

    private void initView() {
        layLogout = findViewById(R.id.lay_logout);
        recyclerOrders = findViewById(R.id.recycler_orders);
        fabProfile = findViewById(R.id.fab_profile);
        fabChangeOrder = findViewById(R.id.fab_change_order);
        progressBar = findViewById(R.id.progressBar);
        txtNoOrder = findViewById(R.id.txt_no_order);
    }

    private void getOrderEdits(final Runnable runnable) {

        JSONObject data = new JSONObject();

        try {
            data.put("type", "edit");
            data.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        apiService.getOrders(data.toString()).enqueue(new Callback<Orders>() {
            @Override
            public void onResponse(Call<Orders> call, Response<Orders> response) {
                if (response.isSuccessful()) {
                    if (response.body().orderEdit != null) {

                        txtNoOrder.setVisibility(View.GONE);
                        orderEditList = response.body().orderEdit;
                        runnable.run();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        txtNoOrder.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<Orders> call, Throwable t) {

                progressBar.setVisibility(View.GONE);
                Toast.makeText(context, "خطا در دریافت اطلاعات از سرور", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getOrderDrawings(final Runnable runnable) {

        JSONObject data = new JSONObject();

        try {
            data.put("type", "drawing");
            data.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        apiService.getOrders(data.toString()).enqueue(new Callback<Orders>() {
            @Override
            public void onResponse(Call<Orders> call, Response<Orders> response) {
                if (response.isSuccessful()) {
                    if (response.body().orderDrawing != null) {

                        txtNoOrder.setVisibility(View.GONE);
                        orderDrawingList = response.body().orderDrawing;
                        runnable.run();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        txtNoOrder.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<Orders> call, Throwable t) {

                progressBar.setVisibility(View.GONE);
                Toast.makeText(context, "خطا در دریافت اطلاعات از سرور", Toast.LENGTH_SHORT).show();
            }
        });
    }
}