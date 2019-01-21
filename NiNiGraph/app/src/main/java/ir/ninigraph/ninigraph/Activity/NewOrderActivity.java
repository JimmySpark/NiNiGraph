package ir.ninigraph.ninigraph.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import ir.ninigraph.ninigraph.Adapter.RecyclerThemeCategoryAdapter;
import ir.ninigraph.ninigraph.Model.ThemeCategory;
import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Server.ApiClient;
import ir.ninigraph.ninigraph.Server.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NewOrderActivity extends AppCompatActivity {

    //Values
    Context context = this;
    RecyclerView recycler_theme_category;
    ProgressBar progressBar;
    ImageView img_back;
    public static TextView txt_choose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

        //Views
        recycler_theme_category = findViewById(R.id.recycler_theme_category);
        progressBar = findViewById(R.id.progressBar);
        img_back = findViewById(R.id.img_back);
        txt_choose = findViewById(R.id.txt_choose);


        //Get Data From Server
        getThemeCategory();

        //Back
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(context, MainMenuActivity.class));
            }
        });
    }

    //Classes
    private void getThemeCategory(){

        progressBar.setVisibility(View.VISIBLE);

        ApiService apiService = ApiClient.getApi().create(ApiService.class);
        Call<List<ThemeCategory>> call = apiService.getThemeCategory();

        call.enqueue(new Callback<List<ThemeCategory>>() {
            @Override
            public void onResponse(Call<List<ThemeCategory>> call, Response<List<ThemeCategory>> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){

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

    //Override Methods
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    public void onBackPressed() {

        startActivity(new Intent(context, MainMenuActivity.class));
    }
}
