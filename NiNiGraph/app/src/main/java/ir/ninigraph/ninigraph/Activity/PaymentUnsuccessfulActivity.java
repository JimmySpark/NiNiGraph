package ir.ninigraph.ninigraph.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import ir.ninigraph.ninigraph.Model.Save;
import ir.ninigraph.ninigraph.R;
import ir.ninigraph.ninigraph.Util.DateConvertor;
import ir.ninigraph.ninigraph.Util.NetworkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static ir.ninigraph.ninigraph.Activity.MainActivity.apiService;

public class PaymentUnsuccessfulActivity extends AppCompatActivity {

    //Values
    Context context = this;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    boolean isConnected;
    private ConstraintLayout layParent;
    private TextView txtDateTime;
    private TextView txtRefId;
    private TextView txtPrice;
    private TextView txtDescription;
    private TextView btnBack;
    private ConstraintLayout layNoCon;
    private Button btnTryAgain;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_unsuccessful);
        initView();

        //Values
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();


        //Check Connection
        checkConnection();

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkConnection();
            }
        });

        //Date And Time
        Calendar calendar = Calendar.getInstance();
        DateConvertor dateConvertor = new DateConvertor();
        dateConvertor.GregorianToPersian(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        String year = String.valueOf(dateConvertor.getYear());
        String month = String.valueOf(dateConvertor.getMonth());
        String day = String.valueOf(dateConvertor.getDay());
        String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String minute = String.valueOf(calendar.get(Calendar.MINUTE));
        txtDateTime.setText(year + "/" + month + "/" + day + " - " + hour + ":" + minute);

        //Info
        txtRefId.setText(" - ");
        txtPrice.setText(String.valueOf(preferences.getLong("pAll", 0)));
        txtDescription.setText(preferences.getString("payFor", null));

        //Remove Order
        if (preferences.getInt("orderId", 0) != 0)
            deleteOrder(preferences.getInt("orderId", 0));

        //Back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.remove("prefs").apply();
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

        editor.remove("prefs").apply();
        startActivity(new Intent(context, MainMenuActivity.class));
    }

    //Classes
    private void initView() {
        layParent = findViewById(R.id.lay_parent);
        txtDateTime = findViewById(R.id.txt_date_time);
        txtRefId = findViewById(R.id.txt_refId);
        txtPrice = findViewById(R.id.txt_price);
        txtDescription = findViewById(R.id.txt_description);
        btnBack = findViewById(R.id.btn_back);
        layNoCon = findViewById(R.id.lay_no_con);
        btnTryAgain = findViewById(R.id.btn_try_again);
    }

    private void checkConnection() {

        isConnected = NetworkUtil.isConnected(context);

        if (!isConnected) {

            layParent.setVisibility(View.GONE);
            layNoCon.setVisibility(View.VISIBLE);
        } else {

            layParent.setVisibility(View.VISIBLE);
            layNoCon.setVisibility(View.GONE);
        }
    }

    private void deleteOrder(int id){

        JSONObject data = new JSONObject();

        try {
            data.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        apiService.deleteOrder(data.toString()).enqueue(new Callback<Save>() {
            @Override
            public void onResponse(Call<Save> call, Response<Save> response) {

            }

            @Override
            public void onFailure(Call<Save> call, Throwable t) {
            }
        });
    }
}
