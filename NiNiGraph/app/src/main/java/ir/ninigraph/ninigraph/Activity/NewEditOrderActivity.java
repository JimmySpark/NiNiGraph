package ir.ninigraph.ninigraph.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import ir.ninigraph.ninigraph.Adapter.RecyclerThemeCategoryAdapter;
import ir.ninigraph.ninigraph.Model.ThemeCategory;
import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Util.NetworkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static ir.ninigraph.ninigraph.Activity.MainActivity.apiService;

public class NewEditOrderActivity extends AppCompatActivity {

    //Values
    Context context = this;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    ConstraintLayout lay_parent, lay_no_con;
    Button btn_try_again;
    RecyclerView recycler_theme_category;
    ProgressBar progressBar;
    ImageView img_back;
    public static TextView txt_choose;
    boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_edit_order);

        //Values
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        editor.remove("c").apply();

        //Views
        recycler_theme_category = findViewById(R.id.recycler_theme_category);
        progressBar = findViewById(R.id.progressBar);
        img_back = findViewById(R.id.img_back);
        txt_choose = findViewById(R.id.txt_choose);
        lay_parent = findViewById(R.id.lay_parent);
        lay_no_con = findViewById(R.id.lay_no_con);
        btn_try_again = findViewById(R.id.btn_try_again);


        //Check Connection
        checkConnection();

        btn_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkConnection();
            }
        });

        //Back
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(context, MainMenuActivity.class));
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(context, MainMenuActivity.class));
    }

    //Classes
    private void checkConnection() {

        isConnected = NetworkUtil.isConnected(context);

        if (!isConnected) {

            lay_parent.setVisibility(View.GONE);
            lay_no_con.setVisibility(View.VISIBLE);
        } else {

            lay_parent.setVisibility(View.VISIBLE);
            lay_no_con.setVisibility(View.GONE);

            //Get Data From Server
            getThemeCategory();
        }
    }

    private void getThemeCategory() {

        progressBar.setVisibility(View.VISIBLE);

        apiService.getThemeCategory().enqueue(new Callback<List<ThemeCategory>>() {
            @Override
            public void onResponse(Call<List<ThemeCategory>> call, Response<List<ThemeCategory>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        progressBar.setVisibility(View.GONE);
                        recycler_theme_category.setLayoutManager(new LinearLayoutManager(context));
                        recycler_theme_category.setAdapter(new RecyclerThemeCategoryAdapter(
                                context,
                                response.body()
                        ));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ThemeCategory>> call, Throwable t) {

            }
        });
    }
}
