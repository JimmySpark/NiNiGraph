package ir.ninigraph.ninigraph.Activity;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ir.ninigraph.ninigraph.Adapter.RecyclerOrderEditThemeAdapter;
import ir.ninigraph.ninigraph.Model.OrderEditTheme;
import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Server.ApiClient;
import ir.ninigraph.ninigraph.Server.ApiService;
import ir.ninigraph.ninigraph.Util.NetworkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static ir.ninigraph.ninigraph.Activity.MainActivity.retroInterface;

public class UploadEditPicActivity extends AppCompatActivity {

    //Values
    Context context = this;
    ConstraintLayout lay_parent, lay_no_con;
    Button btn_try_again;
    RecyclerView recycler_order_edit_theme;
    int order_id;
    boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_edit_pic);

        //Values
        order_id = getIntent().getIntExtra("order_id", 0);

        //Views
        recycler_order_edit_theme = findViewById(R.id.recycler_order_edit_theme);
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
    }

    //Classes
    private void checkConnection(){

        isConnected = NetworkUtil.isConnected(context);

        if (!isConnected){

            lay_parent.setVisibility(View.GONE);
            lay_no_con.setVisibility(View.VISIBLE);
        }
        else {

            lay_parent.setVisibility(View.VISIBLE);
            lay_no_con.setVisibility(View.GONE);

            //Get Data
            getOrderTheme(order_id);
        }
    }
    private void getOrderTheme(int id) {

        JSONObject order_id = new JSONObject();
        try {
            order_id.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        retroInterface.getOrderEditTheme(order_id.toString()).enqueue(new Callback<OrderEditTheme>() {
            @Override
            public void onResponse(Call<OrderEditTheme> call, Response<OrderEditTheme> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        recycler_order_edit_theme.setLayoutManager(new GridLayoutManager(
                                context,
                                3
                        ));
                        recycler_order_edit_theme.setAdapter(new RecyclerOrderEditThemeAdapter(
                                context,response.body().order_theme));
                    } else {
                        Toast.makeText(context, "null", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderEditTheme> call, Throwable t) {

                Toast.makeText(context, "خطا در ارسال درخواست برای دریافت اطلاعات از سرور", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Override Methods
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
