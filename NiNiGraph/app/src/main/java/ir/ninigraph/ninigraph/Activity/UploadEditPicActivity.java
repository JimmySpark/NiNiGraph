package ir.ninigraph.ninigraph.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import ir.ninigraph.ninigraph.Adapter.RecyclerOrderEditThemeAdapter;
import ir.ninigraph.ninigraph.Model.OrderEditTheme;
import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Util.NetworkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static ir.ninigraph.ninigraph.Activity.MainActivity.apiService;

public class UploadEditPicActivity extends AppCompatActivity {

    //Values
    Context context = this;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    int order_id, status;
    boolean isConnected;
    private ImageView imgBack;
    private ConstraintLayout layParent;
    private RecyclerView recyclerOrderEditTheme;
    private ProgressBar progressBar;
    private ConstraintLayout layNoCon;
    private Button btnTryAgain;
    private TextView txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_edit_pic);
        initView();

        //Values
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        status = getIntent().getIntExtra("status", 0);
        order_id = getIntent().getIntExtra("order_id", 0);


        //Check Connection
        checkConnection();

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkConnection();
            }
        });

        //Back
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(context, FollowOrderActivity.class));
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    //Classes
    private void initView() {
        imgBack = findViewById(R.id.img_back);
        layParent = findViewById(R.id.lay_parent);
        recyclerOrderEditTheme = findViewById(R.id.recycler_order_edit_theme);
        progressBar = findViewById(R.id.progressBar);
        layNoCon = findViewById(R.id.lay_no_con);
        btnTryAgain = findViewById(R.id.btn_try_again);
        txtTitle = findViewById(R.id.txt_title);
    }

    private void checkConnection() {

        isConnected = NetworkUtil.isConnected(context);

        if (!isConnected) {

            layParent.setVisibility(View.GONE);
            layNoCon.setVisibility(View.VISIBLE);
        } else {

            layParent.setVisibility(View.VISIBLE);
            layNoCon.setVisibility(View.GONE);

            //Title
            switch (status){

                case 1:
                    txtTitle.setText("ارسال عکس");
                    break;
                case 2:
                    txtTitle.setText("وضعیت عکس");
                    break;
                case 5:
                    txtTitle.setText("دانلود عکس");
                    break;
            }

            //Get Data
            progressBar.setVisibility(View.VISIBLE);
            recyclerOrderEditTheme.setVisibility(View.GONE);
            getOrderTheme(order_id);
        }
    }

    private void getOrderTheme(int order_id) {

        JSONObject data = new JSONObject();
        try {
            data.put("id", order_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        apiService.getOrderEditTheme(data.toString()).enqueue(new Callback<OrderEditTheme>() {
            @Override
            public void onResponse(Call<OrderEditTheme> call, Response<OrderEditTheme> response) {
                if (response.isSuccessful()) {

                    progressBar.setVisibility(View.GONE);
                    recyclerOrderEditTheme.setVisibility(View.VISIBLE);
                    if (response.body().order_theme != null) {

                        recyclerOrderEditTheme.setLayoutManager(new GridLayoutManager(context, 3));
                        recyclerOrderEditTheme.setAdapter(new RecyclerOrderEditThemeAdapter(context, response.body().order_theme));
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderEditTheme> call, Throwable t) {

                progressBar.setVisibility(View.GONE);
                recyclerOrderEditTheme.setVisibility(View.VISIBLE);
                Toast.makeText(context, "خطا در ارسال درخواست برای دریافت اطلاعات از سرور", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
